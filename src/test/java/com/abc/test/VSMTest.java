package com.abc.test;

import java.util.List;

import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.vsm.DocumentDimension;
import com.abc.vsm.Weight;

public class VSMTest {
	
	public static void main(String[] args) {
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("二胎生下三胞胎;");
		DocumentDimension dd = new DocumentDimension();
		Weight wc = new Weight(dd.getAllWordsOfDocument(newsList));
		testBool(wc,newsList);
		testTF(wc,newsList);
		testTFIDF(wc,newsList);
	}

	private static void testTFIDF(Weight wc, List<NewsInfo> newsList) {
		for (NewsInfo newsInfo : newsList) {
			System.out.println(wc.computingTFIDFWeight(newsInfo.getContent(),newsList).toString());
			System.out.println("--------------------------------------------------------------------------------");
		}
	}

	private static void testTF(Weight wc, List<NewsInfo> newsList) {
		for (NewsInfo newsInfo : newsList) {
			System.out.println(wc.computingTFWeight(newsInfo.getContent()).toString());
			System.out.println("--------------------------------------------------------------------------------");
		}
	}

	private static void testBool(Weight wc, List<NewsInfo> newsList) {
		for (NewsInfo newsInfo : newsList) {
			System.out.println(wc.computingBoolWeight(newsInfo.getContent()).toString());
			System.out.println("--------------------------------------------------------------------------------");
		}
	}

}
