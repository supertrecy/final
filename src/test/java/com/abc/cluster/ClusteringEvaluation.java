package com.abc.cluster;

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

import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;

public class ClusteringEvaluation {
	
	String keyword = "空姐银行卡被盗刷";

	@Test
	public void purity() {
		List<Cluster> artificialClusters = readHumanResult(keyword);
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(keyword+";");
		List<Double> purityResult = new ArrayList<>(50);
		SimilarityContext sContext = new SimilarityContext(newsList);
		for (double similarity = 0.50; similarity < 1; similarity+=0.01) {
			List<Cluster> programClusters = new ImprovedAGNEST3(sContext.matrix(), similarity).clustering(newsList);
			for (Cluster cluster : artificialClusters) {
				Cluster defination = find(cluster,programClusters);
				
			}
		}
	}

	private Cluster find(Cluster cluster, List<Cluster> programClusters) {
		
		for (Cluster cluster2 : programClusters) {
			
		}
		return null;
	}
	/**
	 * 把人工分类结果转化为簇的形式
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
								temp = temp *10+string.charAt(i);
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
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return clusterlist;
	}

}
