package com.abc.test;

import java.text.DecimalFormat;
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

	private static Map<Integer, List<Double>> map;
	private static int size;

	static {
		map = new HashMap<>();
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("二胎生下三胞胎;");
		DocumentDimension dd = new DocumentDimension(newsList);
		Weight wc = new Weight(newsList.size(),dd.getAllWordsDF(),dd.getAllWordsOfDocument());
		int i = 0;
		for (NewsInfo newsInfo : newsList) {
			map.put(i++, wc.computingTFIDFWeight(newsInfo.getContent(), newsList));
		}
		size = i;
	}

	@Test
	public void testDocDistance() {
		DecimalFormat df = new DecimalFormat("#.000000");
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print(df.format(Similarity.docDistance(map.get(i), map.get(j))) + " ");
			}
			System.out.println("/end");
		}
		System.out.println("----------------------------------------------------------------");
	}

	@Test
	public void testCosineDistance() {
		DecimalFormat df = new DecimalFormat("#0.00");
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				double tmp = Similarity.cosineDistance(map.get(i), map.get(j));
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

	@Test
	public void testDiceDistance() {
		DecimalFormat df = new DecimalFormat("#0.00");
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				double tmp = Similarity.diceDistance(map.get(i), map.get(j));
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

	@Test
	public void testJaccardDistance() {
		DecimalFormat df = new DecimalFormat("#0.00");
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				double tmp = Similarity.jaccardDistance(map.get(i), map.get(j));
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
