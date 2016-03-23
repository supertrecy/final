package com.abc.crawler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public abstract class AbstractCrawler implements PageProcessor{

	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	protected static Map<String,String> url_time_map;
	protected static List<String> search_words;
	
	static{
		url_time_map = new HashMap<>();
	}
	
	protected void addLinkAndTime(List<String> urls,List<String> pubtimes) {
		Iterator it1 = urls.iterator();
		Iterator it2 = pubtimes.iterator();
		for (; it1.hasNext()&&it2.hasNext();) {
			String url = (String) it1.next();
			String pubtime = (String) it2.next();
			url_time_map.put(url, pubtime);
		}
	}

	public void setSearchWords(List<String> searchWords) {
		search_words = searchWords;
	}
	
	@Override
	public Site getSite() {
		return site;
	}
	
}
