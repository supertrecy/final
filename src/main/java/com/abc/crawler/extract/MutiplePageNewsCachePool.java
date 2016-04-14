package com.abc.crawler.extract;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.abc.db.entity.NewsInfo;

public class MutiplePageNewsCachePool {
	private static Map<String, List<NewsInfo>> cachePool = null;

	private static MutiplePageNewsCachePool instance = null;

	private MutiplePageNewsCachePool() {
	}

	public static MutiplePageNewsCachePool getInstance() {
		if (instance == null) {
			instance = new MutiplePageNewsCachePool();
		}
		return instance;
	}

	public void add(String key, NewsInfo news) {
		List<NewsInfo> list = cachePool.get(key);
		if (list != null) {
			list.add(news);
			list.sort(instance.new NewsUrlComparator());
		} else {
			list = new LinkedList<>();
			list.add(news);
		}
	}
	
	public boolean isExist(String key){
		if(cachePool.get(key)!= null)
			return true;
		else
			return false;
			
	}

	public static List<NewsInfo> getCachedNews() {
		Set<String> keyset = cachePool.keySet();
		List<NewsInfo> newsList = new LinkedList<>();
		NewsInfo news = null;
		for (String key : keyset) {
			List<NewsInfo> list = cachePool.get(key);
			StringBuilder sb = new StringBuilder();
			for (NewsInfo newsInfo : list) {
				sb.append(newsInfo.getContent());
				if(news.getSite() != null||!"".equals(news.getSite()))
					news = newsInfo;
			}
			news.setContent(sb.toString());
			newsList.add(news);
			
		}
		return newsList;
	}

	public void clear() {
		cachePool.clear();
	}

	class NewsUrlComparator implements Comparator<NewsInfo> {

		@Override
		public int compare(NewsInfo o1, NewsInfo o2) {
			String url1 = o1.getUrl();
			String url2 = o2.getUrl();
			url1 = url1.substring(0, url1.lastIndexOf("."));
			url2 = url2.substring(0, url2.lastIndexOf("."));
			return url1.compareTo(url2);
		}

	}

}
