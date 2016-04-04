package com.abc.cluster;

import java.util.LinkedList;
import java.util.List;

import com.abc.db.entity.NewsInfo;

/**
 * 凝聚层次聚类算法
 * 
 * @author w_w
 */
public class ImprovedAGNEST2 extends ImprovedAGNEST {

	private double lowerLimitSimilarity;
	private List<Cluster> result;

	public ImprovedAGNEST2(List<NewsInfo> newsList, double lowerLimitSimilarity,boolean needTag) {
		super(newsList,needTag);
		this.lowerLimitSimilarity = lowerLimitSimilarity;
	}

	public List<Cluster> clustering() {
		List<Cluster> initList = initClusters();
		result = new LinkedList<>();
		int index = 0;
		while (initList.size() > 0) {
			Cluster cluster = initList.get(index);
			initList = mergeCluster(cluster, initList);
			if((++index) >= initList.size()){
				index = 0;
			}
		}
		return result;
	}

	@Override
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
		if(maxSimilarity < lowerLimitSimilarity){
			initList.remove(cluster1);
			result.add(cluster1);
			return initList;
		}else{
			List<NewsInfo> points = cluster1.getPoints();
			if (!points.addAll(closestCluster.getPoints()))
				System.err.println("ERROR:无法合并簇！");
			cluster1.setPoints(points);
			initList.remove(closestCluster);
			return initList;
		}
	}


}
