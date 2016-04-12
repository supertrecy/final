package com.abc.cluster;

import java.util.LinkedList;
import java.util.List;

import com.abc.db.entity.NewsInfo;

/**
 * 凝聚层次聚类算法
 * 
 * @author w_w
 */
public class ImprovedAGNEST3 {

	private List<NewsInfo> newsList;
	private SimilarityRealTimeMatrix smatrix;
	private double lowerLimitSimilarity;
	private List<Cluster> result;

	public ImprovedAGNEST3(SimilarityRealTimeMatrix smMatrix, double lowerLimitSimilarity) {
		this.smatrix = smMatrix;
		this.lowerLimitSimilarity = lowerLimitSimilarity;
	}

	public List<Cluster> clustering(List<NewsInfo> newslist) {
		this.newsList = newslist;
		List<Cluster> initList = initClusters();
		result = new LinkedList<>();
		int index = 0;
		while (initList.size() > 0) {
			Cluster cluster = initList.get(index);
			initList = mergeCluster(cluster, initList);
			if ((++index) >= initList.size()) {
				index = 0;
			}
		}
		return result;
	}

	protected List<Cluster> mergeCluster(Cluster cluster1, List<Cluster> initList) {
		double maxSimilarity = 0.0;
		Cluster closestCluster = null;
		for (Cluster cluster2 : initList) {
			if (cluster1 != cluster2) {
				double distance = compareClusters(cluster1, cluster2);
				if (distance == 1) {
					closestCluster = cluster2;
					break;
				} else if (distance > maxSimilarity) {
					maxSimilarity = distance;
					closestCluster = cluster2;
				}
			}
		}
		if (maxSimilarity < lowerLimitSimilarity) {
			initList.remove(cluster1);
			result.add(cluster1);
			return initList;
		} else {
			List<NewsInfo> points = cluster1.getPoints();
			if (!points.addAll(closestCluster.getPoints()))
				System.err.println("ERROR:无法合并簇！");
			cluster1.setPoints(points);
			initList.remove(closestCluster);
			return initList;
		}
	}

	protected List<Cluster> initClusters() {
		List<Cluster> result = new LinkedList<>();
		for (NewsInfo newsInfo : newsList) {
			boolean findCluster = false;
			if (result.size() > 0) {
				for (Cluster cluster : result) {
					double sim = smatrix.get(cluster.getFirst(), newsInfo);
					if (sim == 1) {
						cluster.addPoint(newsInfo);
						findCluster = true;
						break;
					}
				}
			}
			if (!findCluster) {
				List<NewsInfo> list = new LinkedList<>();
				list.add(newsInfo);
				result.add(new Cluster(list));
			}

		}
		return result;
	}

	protected double compareClusters(Cluster c1, Cluster c2) {
		List<NewsInfo> pointsA = c1.getPoints();
		List<NewsInfo> pointsB = c2.getPoints();
		double max_similarity = 0.0;
		for (NewsInfo pointA : pointsA) {
			for (NewsInfo pointB : pointsB) {
				if (pointA != pointB) {
					double similarity = smatrix.get(pointA,pointB);
					if (similarity == 1)
						return 1;
					else if (similarity > max_similarity)
						max_similarity = similarity;
				}
			}
		}
		return max_similarity;
	}
}
