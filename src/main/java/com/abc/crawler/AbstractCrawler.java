package com.abc.crawler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.abc.crawler.extract.MutiplePageNewsCachePool;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.parse.ContentParser;
import com.abc.parse.HtmlParser;
import com.abc.util.URLUtil;
import com.abc.util.Util;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.UrlUtils;

public abstract class AbstractCrawler implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	private static Map<String, String> urlTimeMap;
	private static List<String> searchWords;
	private static String searchWordsStr;
	protected static int i = 0;
	private static MutiplePageNewsCachePool cache;

	protected abstract List<String> extractPubtime(List<String> rawText);

	static {
		urlTimeMap = new HashMap<>();
		cache = MutiplePageNewsCachePool.getInstance();
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void setSearchWords(List<String> searchWords) {
		AbstractCrawler.searchWords = searchWords;
		searchWordsStr = Util.glueSearchWords(searchWords);
	}

	public static void clearSearchWords() {
		if (AbstractCrawler.searchWords != null)
			AbstractCrawler.searchWords.clear();
	}

	public static Map<String, String> getUrlTimeMap() {
		return urlTimeMap;
	}

	void addLinkAndTime(List<String> urls, List<String> pubtimes) {
		Iterator<String> it1 = urls.iterator();
		Iterator<String> it2 = pubtimes.iterator();
		for (; it1.hasNext() && it2.hasNext();) {
			String url = (String) it1.next().toLowerCase();
			String pubtime = (String) it2.next();
			urlTimeMap.put(url, pubtime);
		}
	}

	void parseNewsHtml(Page page) {
		String html = page.getRawText();
		String url = page.getUrl().toString().toLowerCase();//避免因为大小写导致重复的url
		boolean isCached = false;

		/* 如果数据库中不存在该新闻 */
		String key = getNewsUrlKey(url);
		if (!NewsInfoDao.isExist(url) && key != null) {
			System.out.println("开始处理：" + url);
			NewsInfo news = null;

			/* 如果cache中包含key，也就是该新闻有分页 */
			if (!cache.isExist(key)) {
				news = new HtmlParser().getParse(searchWords, html, url, urlTimeMap.get(url));
				if (news != null) {
					System.out.println("成功解析：" + news.getUrl());
					news.setSearchWords(searchWordsStr);

					/* 如果新闻还有转发源，并且该源是新闻页面 */
					String sourceUrl = news.getSourceUrl();
					if (!"".equals(sourceUrl) && URLUtil.isNewsUrl(sourceUrl)) {
						page.addTargetRequest(sourceUrl);
					}
				}
			}
			/* 如果cache中包含key，也就是该新闻有分页 */
			else {
				isCached = true;
				/* 如果该新闻分页没有被放入cache中 ,虽然会设定不提取重复的url，但不同的搜索引擎可能都有该url，所以必须有这个判断*/
				if (!cache.isUrlExist(url)) {
					news = new NewsInfo();
					news.setUrl(url);
					news.setContent(new ContentParser().parseContent(html, url));
				}else{
					return;
				}
			}
			extractPaginationUrl(isCached, news, page, key, url);

		}
	}

	private void extractPaginationUrl(boolean isCached, NewsInfo news, Page page, String key, String url) {
		List<String> links = page.getHtml().css("body").links().regex(key + "[-|_].*").all();
		links = new LinkedList<>(new HashSet<>(links));// url去重
		List<String> pageUrls = new LinkedList<>(links);
		for (String link : links) {
			// 去除本身、不合法的和全文页面
			if (link.contains(url) || link.contains("#") || link.contains("all.")){
				pageUrls.remove(link);
				continue;
			}
			// 去除已经爬取的
			if (cache.isUrlExist(link))
				pageUrls.remove(link);
		}

		// 如果采集到其余分页
		if (pageUrls.size() > 0) {
			System.out.println("匹配到其余分页:");
			for (String link : pageUrls) {
				System.out.println(link);
			}
			System.out.println("------------------------------------------------");
			page.addTargetRequests(pageUrls);
		}
		// 如果没有采集到其余分页，并且它不存在在分页新闻缓冲池内，也就是指它没有分页
		else if (pageUrls.size() == 0 && !isCached) {
			NewsInfoDao.addNews(news);
			return;
		}
		cache.add(key, news);
	}



	/**
	 * 
	 * @param url
	 *            新闻的url
	 * @return
	 * 		<p>
	 *         如果是某条新闻的url，返回新闻第一页的截取部分，
	 *         </p>
	 *         <p>
	 *         例如输入‘http://politics.people.com.cn/n1/2016/0413/c1001-28273470-2.
	 *         html’，返回‘http://politics.people.com.cn/n1/2016/0413/c1001-
	 *         28273470’；
	 *         </p>
	 *         <p>
	 *         不是新闻，则返回null
	 *         </p>
	 */
	private String getNewsUrlKey(String url) {
		if (URLUtil.isNewsUrl(url)) {
			String suffix = url.substring(url.lastIndexOf("/") + 1);
			String prefix = url.substring(0, url.lastIndexOf("/") + 1);
			String key = null;
			int underline_index = suffix.lastIndexOf("_");
			int hyphen_index = suffix.lastIndexOf("-");
			int dot_index = suffix.lastIndexOf(".");
			int separator;

			if (underline_index == -1 && hyphen_index != -1)
				separator = hyphen_index;
			else if (underline_index != -1 && hyphen_index == -1)
				separator = underline_index;
			else if (underline_index != -1 && hyphen_index != -1)
				separator = underline_index > hyphen_index ? underline_index : hyphen_index;
			else {
				if (dot_index != -1)
					suffix = suffix.substring(0, dot_index);
				key = prefix + suffix;
				return key;
			}

			// page可能的情况：'xxxx-2.html','xxxx-xxxx','xxxx-2','xxxx-xxxx.html'(.html代表所有后缀包括.shtml等，-代表所有分隔符)
			String page = suffix.substring(separator + 1);
			int page_dot_index = page.lastIndexOf(".");
			if (dot_index != -1) {
				page = page.substring(0, page_dot_index);
				suffix = suffix.substring(0, dot_index);
			}
			if (page.length() <= 2) {
				suffix = suffix.substring(0, separator);
			}
			key = prefix + suffix;

			return key;
		} else {
			return null;
		}

	}
}
