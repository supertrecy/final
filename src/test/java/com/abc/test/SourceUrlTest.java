package com.abc.test;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;

import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.vsm.DocumentDimension;
import com.abc.vsm.Similarity;
import com.abc.vsm.Weight;

public class SourceUrlTest {
//	@Test
//	public void divideList() {
//		List<NewsInfo> newsList = NewsInfoDao.getNewsList();
//		List<String> news = new LinkedList<>();
//		List<String> nonews = new LinkedList<>();
//		for (NewsInfo newsInfo : newsList) {
//			String source_url = newsInfo.getSourceUrl();
//			if (source_url != null && !source_url.equals("")) {
//				if (isNewsUrl(source_url))
//					news.add(source_url);
//				else
//					nonews.add(source_url);
//			}
//
//		}
//		System.out.println("新闻类URL：");
//		printlist(news);
//		System.out.println("======================================================================");
//		System.out.println("非新闻类URL：");
//		printlist(nonews);
//	}

	private void printlist(List<String> list) {
		for (String string : list) {
			System.out.println(string);
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

	@Test
	public void sourceurllist() {
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("女子酒店遇袭;");
		List<NewsInfo> news = new LinkedList<>();
		List<NewsInfo> nonews = new LinkedList<>();
		for (NewsInfo newsInfo : newsList) {
			String source_url = newsInfo.getSourceUrl();
			if (source_url != null && !source_url.equals("")) {
				if (isNewsUrl(source_url))
					news.add(newsInfo);
				else
					nonews.add(newsInfo);
			}

		}

		Map<String, List<NewsInfo>> map = new HashMap<>();
		for (NewsInfo newsInfo : news) {
			String source_url = newsInfo.getSourceUrl();
			List<NewsInfo> list = map.get(source_url);
			if (list == null) {
				list = new LinkedList<>();
			}
			list.add(newsInfo);
			map.put(source_url, list);
		}
		System.out.println("source url相同的新闻有" + map.size() + "簇");
		System.out.println("下面开始聚类……");
		Map<Integer, List<Double>> dmap = new HashMap<>();
		DocumentDimension dd = new DocumentDimension();
		Weight wc = new Weight(dd.getAllWordsOfDocument(newsList));
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String sourceUrl = (String) iterator.next();
			if(map.get(sourceUrl).size()>1&&isNewsUrl(sourceUrl)){
				System.out.println(sourceUrl);
				System.out.println("以下是url：");
				int index = 0;
				int size;
				dmap.clear();
				for (NewsInfo newsInfo : map.get(sourceUrl)) {
					dmap.put(index++, wc.computingTFIDFWeight(newsInfo.getContent(), newsList));
					System.out.println(newsInfo.getUrl());
				}
				size = index;
				DecimalFormat df = new DecimalFormat("#0.00");

				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						double tmp = Similarity.cosineDistance(dmap.get(i), dmap.get(j));
						if (tmp > 0.5 && i >= j)
							System.out.print(df.format(tmp) + " ");
						else
							System.out.print("-.-- ");
					}
					System.out.println("/end");
					System.out.println(" ");
					
				}
				System.out.println("----------------------------------------------------------------");
			}
		}
	}
	
}