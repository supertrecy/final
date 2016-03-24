package com.abc.crawler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.abc.db.dao.NewsDao;
import com.abc.db.entity.NewsInfo;
import com.abc.parse.HtmlParser;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public abstract class AbstractCrawler implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	private static Map<String, String> urlTimeMap;
	private static List<String> searchWords;
	private static NewsDao dao;
	protected static int i = 0;

	protected abstract List<String> extractPubtime(List<String> rawText);

	static {
		urlTimeMap = new HashMap<>();
		dao = new NewsDao();
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
	}

	public static Map<String, String> getUrlTimeMap() {
		return urlTimeMap;
	}

	void parseNewsHtml(String html, String url) {
		if(!dao.isExist(url)){
			System.out.println("开始处理："+url);
			HtmlParser parser = new HtmlParser();
			NewsInfo news = parser.getParse(searchWords, html, url,urlTimeMap.get(url));
			if (news != null){
				System.out.println("成功解析："+news.getUrl());
				NewsDao.addNews(news);
			}
		}
	}
}
