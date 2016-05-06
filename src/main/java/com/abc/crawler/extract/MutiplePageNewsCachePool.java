package com.abc.crawler.extract;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.util.Util;

public class MutiplePageNewsCachePool {
	private static Map<String, List<NewsInfo>> cachePool = null;
	private static Set<String> crawledUrlsSet = null;
	private static MutiplePageNewsCachePool instance = null;
	private static String searchWordsStr;

	private MutiplePageNewsCachePool() {
		cachePool = new HashMap<>();
		crawledUrlsSet = new HashSet<>();
	}

	public static MutiplePageNewsCachePool getInstance() {
		if (instance == null) {
			instance = new MutiplePageNewsCachePool();
		}
		return instance;
	}
	

	public static void setSearchWordsStr(List<String> searchWords) {
		searchWordsStr = Util.glueSearchWords(searchWords);
	}

	public void add(String key, NewsInfo news) {
		List<NewsInfo> list = cachePool.get(key);
		if (list != null) {
			list.add(news);
			list.sort(instance.new NewsUrlComparator(key));
		} else {
			list = new LinkedList<>();
			list.add(news);
		}
		crawledUrlsSet.add(news.getUrl());
		cachePool.put(key, list);
	}
	
	public boolean isExist(String key){
		if(cachePool.get(key)!= null)
			return true;
		else
			return false;
			
	}
	
	public boolean isUrlExist(String url){
		return crawledUrlsSet.contains(url);
			
	}

	public void store() {
		Set<String> keyset = cachePool.keySet();
		List<NewsInfo> newsList = new LinkedList<>();
		NewsInfo news = null;
		for (String key : keyset) {
			List<NewsInfo> list = cachePool.get(key);
			String url = list.get(0).getUrl();
			StringBuilder sb = new StringBuilder();
			for (NewsInfo newsInfo : list) {
				System.out.println(newsInfo.getUrl()+newsInfo.getSite());
				sb.append(newsInfo.getContent());
				if(newsInfo.getSite()!= null&&!"".equals(newsInfo.getSite())){
					news = newsInfo;
				}
			}
			news.setContent(sb.toString());
			news.setUrl(url);
			news.setSearchWords(searchWordsStr);
			System.out.println("-------------------------------------------------------------------------");
			newsList.add(news);
			NewsInfoDao.addNews(news);
			news = null;
		}
	}

	public void clear() {
		cachePool.clear();
		crawledUrlsSet.clear();
		
	}

	class NewsUrlComparator implements Comparator<NewsInfo> {

		private String key;
		
		public NewsUrlComparator(String key) {
			this.key = key;
		}

		@Override
		public int compare(NewsInfo o1, NewsInfo o2) {
			String url1 = o1.getUrl();
			String url2 = o2.getUrl();
			int dot_index1 = url1.lastIndexOf(".");
			int dot_index2 = url2.lastIndexOf(".");
			
			//去掉网页文件后缀
			if(dot_index1 != -1){
				url1 = url1.substring(0, dot_index1);
				url2 = url2.substring(0, dot_index2);
			}
			//截取页数
			url1=url1.substring(key.length());
			url2=url2.substring(key.length());
			int page1 = getPage(url1);
			int page2 = getPage(url2);
			
			return page1 - page2;
		}
		
		private int getPage(String str){
			int underline_index = str.lastIndexOf("_");
			int hyphen_index = str.lastIndexOf("-");
			int page = 0;

			if (underline_index != -1)
				page = Integer.parseInt(str.substring(underline_index+1));
			else if (hyphen_index != -1)
				page = Integer.parseInt(str.substring(hyphen_index+1));
			
			return page;
		}

	}
	

}
