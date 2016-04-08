package com.abc.crawler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
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

	protected abstract List<String> extractPubtime(List<String> rawText);

	static {
		urlTimeMap = new HashMap<>();
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
	
	public static void clearSearchWords(){
		if(AbstractCrawler.searchWords != null)
			AbstractCrawler.searchWords.clear();
	}

	public static Map<String, String> getUrlTimeMap() {
		return urlTimeMap;
	}

	void parseNewsHtml(Page page) {
		String html = page.getRawText();
		String url = page.getUrl().toString();
		if(!NewsInfoDao.isExist(url)){
//			System.out.println("开始处理："+url);
			HtmlParser parser = new HtmlParser();
			NewsInfo news = parser.getParse(searchWords, html, url,urlTimeMap.get(url));
			if (news != null){
//				System.out.println("成功解析："+news.getUrl());
				news.setSearchWords(searchWordsStr);
				NewsInfoDao.addNews(news);
				String sourceUrl = news.getSourceUrl();
				if(!"".equals(sourceUrl)&&isNewsUrl(sourceUrl)){
					page.addTargetRequest(sourceUrl);
					System.out.println(sourceUrl);
				}
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
}
