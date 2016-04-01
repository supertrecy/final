package com.abc.cluster;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.abc.db.entity.NewsInfo;

/**
 * 凝聚层次聚类算法
 * 
 * @author w_w
 */
public class ImprovedAGNES {

	private Map<NewsInfo, Integer> newsMap = null;
	private int clusterNum;
	private SimilarityMatrix smatrix;

	public ImprovedAGNES(List<NewsInfo> newsList, int clusterNum) {
		this.clusterNum = clusterNum;
		this.smatrix = new SimilarityMatrix(newsList);
		this.newsMap = new HashMap<>();
		int index = 0;
		for (NewsInfo newsInfo : newsList) {
			newsMap.put(newsInfo, index++);
		}
	}

	public List<Cluster> clustering() {
		List<Cluster> result = initClusters();
		int index = 0;
		while (result.size() > clusterNum) {
			Cluster cluster = result.get(index);
			result = mergeCluster(cluster, result);
			if((++index) >= result.size()){
				index = 0;
			}
		}
		return result;
	}

	private List<Cluster> mergeCluster(Cluster cluster1, List<Cluster> result) {
		double max_similarity = 0.0;
		Cluster closest_cluster = null;
		for (Cluster cluster2 : result) {
			if (cluster1 != cluster2) {
				double distance = compareClusters(cluster1, cluster2);
				if (distance == 1) {
					closest_cluster = cluster2;
					break;
				} else if (distance > max_similarity) {
					max_similarity = distance;
					closest_cluster = cluster2;
				}
			}
		}
		List<NewsInfo> points = cluster1.getPoints();
		if (!points.addAll(closest_cluster.getPoints()))
			System.err.println("ERROR:无法合并簇！");
		cluster1.setPoints(points);
		result.remove(closest_cluster);
		return result;
	}

	private double compareClusters(Cluster c1, Cluster c2) {
		List<NewsInfo> pointsA = c1.getPoints();
		List<NewsInfo> pointsB = c2.getPoints();
		double max_similarity = 0.0;
		for (NewsInfo pointA : pointsA) {
			for (NewsInfo pointB : pointsB) {
				int i = newsMap.get(pointA);
				int j = newsMap.get(pointB);
				if (i != j) {
					double similarity = smatrix.get(i, j);
					if (similarity == 1)
						return 1;
					else if (similarity > max_similarity)
						max_similarity = similarity;
				}
			}
		}
		return max_similarity;
	}
	
	protected List<Cluster> initClusters() {
		List<Cluster> result = new LinkedList<>();
		Set<NewsInfo> newsSet = newsMap.keySet();
		int i = 0;
		for (NewsInfo newsInfo : newsSet) {
			boolean findCluster = false;
			if(result.size() > 0){
				for (Cluster cluster : result) {
					double sim = smatrix.get(newsMap.get(cluster.getFirst()), newsMap.get(newsInfo));
					if(sim == 1){
						cluster.addPoint(newsInfo);
						findCluster = true;
						break;
					}
				}
			}
			if(!findCluster){
				List<NewsInfo> list = new LinkedList<>();
				list.add(newsInfo);
				result.add(new Cluster(list));
			}
			
		}
		return result;
	}

}
