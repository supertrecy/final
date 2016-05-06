package com.abc.cluster;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;

public class ImprovedAGNEST2Test {
	
	private final String KEYWORD = "女子动车拒让座";
	private final String SEARCH_WORDS = KEYWORD + ";";
	private double lowerLimitSimilarity = 1.0;
	
	@Test
	public void clustering() {
		long time = System.currentTimeMillis();
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(SEARCH_WORDS);
		AGNEST al = new ImprovedAGNEST2(newsList, lowerLimitSimilarity, false);
		PrintUtil.printCluster(al);
		System.out.println("总用时：" + (double) (System.currentTimeMillis() - time) / 1000 + "秒");
	}
	
	@Test
	public void clusteringMapTags() {
		long time = System.currentTimeMillis();
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(SEARCH_WORDS);
		AGNEST al = new ImprovedAGNEST2(newsList, lowerLimitSimilarity, true);
		PrintUtil.printClusterAndTagMap(al);
		System.out.println("总用时：" + (double) (System.currentTimeMillis() - time) / 1000 + "秒");
	}

	@Test
	public void clusteringListTags() {
		long time = System.currentTimeMillis();
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(SEARCH_WORDS);
		AGNEST al = new ImprovedAGNEST2(newsList, lowerLimitSimilarity, true);
		PrintUtil.printClusterAndTagList(al);
		System.out.println("总用时：" + (double) (System.currentTimeMillis() - time) / 1000 + "秒");
	}
	
	@Test
	public void clusteringListTagAndWriteToFile() {
		double lowerLimitSimilarity = 0.50;
		double increment = 0.01;
		for (int i = 0; i < 50; i++) {
			long time = System.currentTimeMillis();
			double similarity = lowerLimitSimilarity + increment * i;
			List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(SEARCH_WORDS);
			ImprovedAGNEST2 al = new ImprovedAGNEST2(newsList, similarity, true);
			try {
				PrintUtil.writeClusterAndTagListToFile(KEYWORD,al, time, similarity);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(similarity + ".txt is OK.");
		}
	}

}
