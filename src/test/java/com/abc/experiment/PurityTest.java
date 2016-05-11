package com.abc.experiment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.abc.cluster.Cluster;
import com.abc.cluster.ImprovedAGNEST3;
import com.abc.cluster.SimilarityContext;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;

public class PurityTest {

	String keyword = "空姐银行卡被盗刷";
	private int totalNum;

	@Test
	public void purity() {
		List<Cluster> artificialClusters = readHumanResult(keyword);
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(keyword + ";");
		SimilarityContext sContext = new SimilarityContext(newsList);
		System.out.println("x=0.50:0.001:0.999;");
		System.out.print("y=[");
		for (double similarity = 0.50; similarity < 1; similarity += 0.001) {
			int correctNum = 0;
			List<Cluster> programClusters = new ImprovedAGNEST3(sContext.matrix(), similarity).clustering(newsList);
			for (Cluster cluster : artificialClusters) {
				correctNum += countCorrectPoint(cluster, programClusters);
			}
			System.out.print((double) correctNum / getTotalNum() + " ");
//			System.out.println("----------------------------------------------------------------");
		}
		System.out.println("];plot(x,y);");
	}

	private int countCorrectPoint(Cluster cluster, List<Cluster> programClusters) {
		int pointNum = cluster.getSize();
		List<NewsInfo> points = cluster.getPoints();
		List<Boolean> pointFlags = new ArrayList<>(pointNum);
		List<Cluster> clusters = new LinkedList<>();
		for (int i = 0; i < pointNum; i++) {
			pointFlags.add(true);
		}
		
		int correctNum = 0;
		for (int i = 0; i < pointNum; i++) {
			//找到point所在的算法分类簇
			NewsInfo point = points.get(i);
			Cluster defination = findDefinationCluster(point,programClusters);
			int artificialClusterSize = cluster.getSize();
			int definationClusterSize = defination.getSize();
			if(artificialClusterSize == 1&&definationClusterSize == 1){
				return 1;
			}else if(artificialClusterSize != 1&&definationClusterSize != 1){
				Cluster newone = new Cluster();
				for (NewsInfo news : defination.getPoints()) {
					newone.addPoint(news);
				}
				clusters.add(newone);
			}else{
				pointFlags.set(i, false);
			}
		}
		//去除无关的
		if(clusters.size()>0){
			int max = 0;
			for (Cluster cluster2 : clusters) {
				List<NewsInfo> list = cluster2.getPoints();
				int temp = cluster2.getSize();
				for (NewsInfo newsInfo : list) {
					if(!contain(cluster, newsInfo))
						temp--;
				}
				if(temp > max)
					max = temp;
			}
			
			correctNum += max;
		}
			
		return correctNum;
	}

	private int compareCluster(Cluster cluster, Cluster defination) {
		return 0;
	}

	private Cluster findDefinationCluster(NewsInfo point, List<Cluster> programClusters) {
		for (Cluster cluster : programClusters) {
			if(contain(cluster, point))
				return cluster;
		}
		System.out.println("没找到");
		return null;
	}
	
	private boolean contain(Cluster cluster,NewsInfo point){
		List<NewsInfo> points = cluster.getPoints();
		for (NewsInfo newsInfo : points ) {
			if (newsInfo.getId() == point.getId())
				return true;
		}
		return false;
	}

	/**
	 * 把人工分类结果转化为簇的形式
	 * 
	 * @param keyword
	 * @return
	 */
	private List<Cluster> readHumanResult(String keyword) {
		String filename = "E:\\testfiles\\ClusteringEvaluation\\" + keyword + "\\人工分类.txt";
		List<Cluster> clusterlist = new LinkedList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"));
			String line = reader.readLine();
			Set<String> idList = new TreeSet<>();
			int count = 0;
			int id;
			while (line != null && !"".equals(line)) {
				String[] ids = line.split("，");
				Cluster cluster = new Cluster();
				for (String string : ids) {
					NewsInfo news = new NewsInfo();
					try {
						id = Integer.parseInt(string.trim());
					} catch (NumberFormatException e) {
						System.err.println("转化失败了，包含其他字符");
						int temp = 0;
						for (int i = 0; i < string.length(); i++) {
							if (Character.isDigit(string.charAt(i))) {
								temp = temp * 10 + (string.charAt(i)-'0');
							}
						}
						id = temp;
					}
					news.setId(id);
					idList.add(string);
					cluster.addPoint(news);
				}
				line = reader.readLine();
				clusterlist.add(cluster);
			}
			// 用于发现是否有错误
			for (String string : idList) {
				System.out.println(string);
				count++;
			}
			System.out.println("总共" + count);
			totalNum = count;
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return clusterlist;
	}

	private int getTotalNum() {
		return totalNum;
	}

}
