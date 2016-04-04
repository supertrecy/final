package com.abc.test;

import java.util.List;

import org.junit.Test;

import com.abc.cluster.AGNEST;
import com.abc.cluster.Cluster;
import com.abc.cluster.ImprovedAGNEST;
import com.abc.cluster.ImprovedAGNEST2;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;

public class AGNESTest {
	
	private static final int CLUSTER_NUM = 41;

//	@Test
//	public void testClustering() {
//		long time = System.currentTimeMillis();
//		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("同性恋爱情片过审;");
//		AGNEST al = new AGNEST(newsList, CLUSTER_NUM,true);
//		test1(al);
//		System.out.println("总用时："+(double)(System.currentTimeMillis()-time)/1000+"秒");
//	}
//
//	@Test
//	public void testImprovedClustering() {
//		long time = System.currentTimeMillis();
//		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("同性恋爱情片过审;");
//		AGNEST al = new ImprovedAGNEST(newsList, CLUSTER_NUM,true);
//		test1(al);
//		System.out.println("总用时："+(double)(System.currentTimeMillis()-time)/1000+"秒");
//	}
//	
//	@Test
//	public void testImproved2ClusteringByMap() {
//		long time = System.currentTimeMillis();
//		double lowerLimitSimilarity = 0.8;
//		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("同性恋爱情片过审;");
//		AGNEST al = new ImprovedAGNEST2(newsList, lowerLimitSimilarity,true);
//		test1(al);
//		System.out.println("总用时："+(double)(System.currentTimeMillis()-time)/1000+"秒");
//	}
	@Test
	public void testImproved2ClusteringByList() {
		long time = System.currentTimeMillis();
		double lowerLimitSimilarity = 0.99;
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("非法疫苗;");
		AGNEST al = new ImprovedAGNEST2(newsList, lowerLimitSimilarity,true);
		test2(al);
		System.out.println("总用时："+(double)(System.currentTimeMillis()-time)/1000+"秒");
	}
	private void test1(AGNEST al){
		
		List<Cluster> clusters = al.clustering();
		int i = 1;
		 for (Cluster cluster : clusters) {
			List<NewsInfo> newslist = cluster.getPoints();
			System.out.println((i++)+":");
			for (NewsInfo newsInfo : newslist) {
				System.out.println(newsInfo.getId()+":"+ al.getNewsTagMap(newsInfo));
			}
			System.out.println();
		}
	}
	
	private void test2(AGNEST al){
		
		List<Cluster> clusters = al.clustering();
		int i = 1;
		 for (Cluster cluster : clusters) {
			List<NewsInfo> newslist = cluster.getPoints();
			System.out.println((i++)+":");
			for (NewsInfo newsInfo : newslist) {
				System.out.println(newsInfo.getId()+":"+ al.getNewsTagList(newsInfo));
			}
			System.out.println();
		}
	}
	
	
}
