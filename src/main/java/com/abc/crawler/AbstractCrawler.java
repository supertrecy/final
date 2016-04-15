package com.abc.crawler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.abc.crawler.extract.MutiplePageNewsCachePool;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.parse.ContentParser;
import com.abc.parse.HtmlParser;
import com.abc.util.Util;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

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

	void addLinkAndTime(List<String> urls, List<String> pubtimes) {
		Iterator<String> it1 = urls.iterator();
		Iterator<String> it2 = pubtimes.iterator();
		for (; it1.hasNext() && it2.hasNext();) {
			String url = (String) it1.next();
			String pubtime = (String) it2.next();
			urlTimeMap.put(url, pubtime);
		}
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

	void parseNewsHtml(Page page) {
		String html = page.getRawText();
		String url = page.getUrl().toString();
		/* 如果数据库中不存在该新闻 */
		String key = getNewsUrlKey(url);
		if (!NewsInfoDao.isExist(url) && key != null) {
			System.out.println("开始处理："+url);
			if (!cache.isExist(key)) {
				NewsInfo news = new HtmlParser().getParse(searchWords, html, url, urlTimeMap.get(url));
				if (news != null) {
					 System.out.println("成功解析："+news.getUrl());
					news.setSearchWords(searchWordsStr);
					//NewsInfoDao.addNews(news);
					String sourceUrl = news.getSourceUrl();
					if (!"".equals(sourceUrl)) {
						page.addTargetRequest(sourceUrl);
					}
					List<String> links = page.getHtml().css("body").links().regex(key + "[-|_].*").all();// 
					System.out.println("匹配到分页");
					for (String string : links) {
						System.out.println(string);
					}
					System.out.println("------------------------------------------------");
					//page.addTargetRequests(links);
					cache.add(key, news);
				}
			} else {
				System.out.println("已经存在");
				
				NewsInfo news = new NewsInfo();
				news.setUrl(url);
				news.setContent(new ContentParser().parseContent(html, url));
				cache.add(key, news);
			}
		}
	}

	private boolean isNewsUrl(String url) {
		if (!url.contains(".htm") && !url.contains(".shtml")) {
			String temp = url.substring(url.indexOf("://") + 3);
			if (temp.contains("/")) {
				temp = temp.substring(temp.indexOf("/") + 1);
				Pattern pattern = Pattern.compile(".*\\d+.*");
				return pattern.matcher(temp).matches();

			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @param url
	 *            新闻的url
	 * @return
	 *         <p>
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
		System.out.println("==================================================");
		//System.out.println(url);
		if (isNewsUrl(url)) {
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
			if (page.length() <= 2){
				suffix = suffix.substring(0, separator);
				System.out.println(url);
				System.out.println(prefix + suffix);
			}
			key = prefix + suffix;
			
			return key;
		} else {
			System.out.println("不是新闻");
			return null;
		}

	}
}
