package com.abc.cluster;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;

public class ImprovedAGNESTTest {
	private final String KEYWORD = "二胎生下三胞胎";
	private final String SEARCH_WORDS = KEYWORD + ";";
	private final int CLUSTER_NUM = 15;

	@Test
	public void clustering() {
		long time = System.currentTimeMillis();
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(SEARCH_WORDS);
		AGNEST al = new ImprovedAGNEST(newsList, CLUSTER_NUM, false);
		PrintUtil.printCluster(al);
		System.out.println("总用时：" + (double) (System.currentTimeMillis() - time) / 1000 + "秒");
	}

	@Test
	public void clusteringMapTags() {
		long time = System.currentTimeMillis();
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(SEARCH_WORDS);
		AGNEST al = new ImprovedAGNEST(newsList, CLUSTER_NUM, true);
		PrintUtil.printClusterAndTagMap(al);
		System.out.println("总用时：" + (double) (System.currentTimeMillis() - time) / 1000 + "秒");
	}

	@Test
	public void clusteringListTags() {
		long time = System.currentTimeMillis();
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(SEARCH_WORDS);
		AGNEST al = new ImprovedAGNEST(newsList, CLUSTER_NUM, true);
		PrintUtil.printClusterAndTagList(al);
		System.out.println("总用时：" + (double) (System.currentTimeMillis() - time) / 1000 + "秒");
	}

	@Test
	public void clusteringListTagAndWriteToFile() {
		int lowlimit = 1;
		for (int i = 0; i < 25; i++) {
			long time = System.currentTimeMillis();
			int clusterNum = lowlimit + i;
			List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(SEARCH_WORDS);
			System.out.println(newsList.size());
			AGNEST al = new ImprovedAGNEST(newsList, lowlimit + i, true);
			try {
				PrintUtil.writeClusterAndTagListToFile(KEYWORD,al, time, clusterNum, "ImprovedAGNESTTest");
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(clusterNum + ".txt is OK.");
		}
	}

}
