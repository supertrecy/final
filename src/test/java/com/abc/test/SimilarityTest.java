package com.abc.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.vsm.DocumentDimension;
import com.abc.vsm.Similarity;
import com.abc.vsm.Weight;

public class SimilarityTest {

	private static Map<Integer,List<Double>> map;
	private static int size;
	
	static{
		map = new HashMap<>();
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("二胎生下三胞胎;");
		DocumentDimension dd = new DocumentDimension();
		Weight wc = new Weight(dd.getAllWordsOfDocument(newsList));
		int i=0;
		for (NewsInfo newsInfo : newsList) {
			map.put(i++, wc.computingTFIDFWeight(newsInfo.getContent(),newsList));
		}
		size = i;
	}
	
	@Test
	public void testDocDistance() {
		for (int i = 0; i < size/2; i++) {
			System.out.println("doc:"+Similarity.docDistance(map.get(i), map.get(size-1-i)));
		}
	}

	@Test
	public void testCosineDistance() {
		for (int i = 0; i < size/2; i++) {
			System.out.println("cos:"+Similarity.cosineDistance(map.get(i), map.get(size-1-i)));
		}
	}

	@Test
	public void testDiceDistance() {
		for (int i = 0; i < size/2; i++) {
			System.out.println("dice:"+Similarity.diceDistance(map.get(i), map.get(size-1-i)));
		}
	}

	@Test
	public void testJaccardDistance() {
		for (int i = 0; i < size/2; i++) {
			System.out.println("j:"+Similarity.jaccardDistance(map.get(i), map.get(size-1-i)));
		}
	}

}
