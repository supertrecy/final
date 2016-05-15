package com.abc.experiment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
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

public class RITest {
	String keyword = "空姐银行卡被盗刷";
	private int totalNum;

	@Test
	public void RI() {
		List<Cluster> artificialClusters = readHumanResult(keyword);
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(keyword + ";");
		SimilarityContext sContext = new SimilarityContext(newsList);
		System.out.println("x=0.01:0.01:0.99;");
		System.out.print("ri=[");
		for (double similarity = 0.01; similarity < 1; similarity += 0.01) {
			List<Cluster> programClusters = new ImprovedAGNEST3(sContext.matrix(), similarity).clustering(newsList);
			System.out.print(getRIResult(artificialClusters, programClusters) + " ");
		}
		System.out.println("];");
	}

	private double getRIResult(List<Cluster> artificialClusters, List<Cluster> programClusters) {
		int TP, FP, TN, FN;
		int denominator = getTotalNum() * (getTotalNum() - 1) / 2;
		Set<Pair> sameClassPairs = sameClusterPairs(artificialClusters);
		Set<Pair> sameClusterPairs = sameClusterPairs(programClusters);
		TP = getTP(sameClassPairs, sameClusterPairs);
		FP = sameClusterPairs.size() - TP;
		FN = sameClassPairs.size() - TP;// getFN(sameClassPairs,sameClusterPairs);
		TN = denominator - TP - FP - FN;
//		System.out.println("TP="+TP+",FN="+FN+",TN="+TN+",FP="+FP);
		return (double) (TP + TN) / denominator;

	}

	private int getTP(Set<Pair> sameClassPairs, Set<Pair> sameClusterPairs) {
		int count = 0;
		for (Pair pair : sameClusterPairs) {
			for (Pair pair2 : sameClassPairs) {
				if(pair.equals(pair2)){
					//System.out.println(pair.toString()+":"+pair2.toString());
					count++;
					break;
				}
			}
		}
		return count;
	}

	private Set<Pair> sameClusterPairs(List<Cluster> artificialClusters) {
		Set<Pair> pairs = new HashSet<>();
		for (Cluster cluster : artificialClusters) {
			List<NewsInfo> points = cluster.getPoints();
			int size = cluster.getSize();
//			 System.out.println(size);
			for (int i = 0; i < size; i++) {
				for (int j = i + 1; j < size; j++) {
					if (!pointsEqual(points.get(i), points.get(j)))
						pairs.add(new Pair(points.get(i).getId(), points.get(j).getId()));
				}
			}
		}
//		 System.out.println(pairs.size());
//		 System.out.println("---------------------------------");
		return pairs;
	}

	private boolean pointsEqual(NewsInfo newsInfo, NewsInfo newsInfo2) {
		int id1 = newsInfo.getId();
		int id2 = newsInfo2.getId();
		return id1==id2?true:false;
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
								temp = temp * 10 + (string.charAt(i) - '0');
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


