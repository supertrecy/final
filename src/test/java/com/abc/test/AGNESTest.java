package com.abc.test;

import java.util.List;

import org.junit.Test;

import com.abc.cluster.Cluster;
import com.abc.cluster.ImprovedAGNES;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;

public class AGNESTest {

//	@Test
//	public void testClustering() {
//		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("二胎生下三胞胎;");
//		AGNES al = new AGNES(newsList, 20);
//		List<Cluster> clusters = al.clustering();
//		int i = 1;
//		for (Cluster cluster : clusters) {
//			List<NewsInfo> newslist = cluster.getPoints();
//			System.out.println((i++)+":");
//			for (NewsInfo newsInfo : newslist) {
//				System.out.println(newsInfo.getTitle());
//			}
//			System.out.println();
//		}
//	}

	@Test
	public void testImprovedClustering() {
		long time = System.currentTimeMillis();
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("二胎生下三胞胎;");
		ImprovedAGNES al = new ImprovedAGNES(newsList, 20);
		List<Cluster> clusters = al.clustering();
		int i = 1;
		 for (Cluster cluster : clusters) {
			List<NewsInfo> newslist = cluster.getPoints();
			System.out.println((i++)+":");
			for (NewsInfo newsInfo : newslist) {
				System.out.println(newsInfo.getId()+":"+newsInfo.getTitle());
			}
			System.out.println();
		}
		System.out.println("总用时："+(System.currentTimeMillis()-time));
	}
}
