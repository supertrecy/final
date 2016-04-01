package com.abc.cluster;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.abc.db.entity.NewsInfo;

/**
 * 凝聚层次聚类算法
 * 
 * @author w_w
 */
public class ImprovedAGNEST extends AGNEST{
	
	public ImprovedAGNEST(List<NewsInfo> newsList){
		super(newsList);
	}
	
	public ImprovedAGNEST(List<NewsInfo> newsList, int clusterNum) {
		super(newsList, clusterNum);
	}

	
	@Override
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

	@Override
	protected List<Cluster> initClusters() {
		List<Cluster> result = new LinkedList<>();
		Set<NewsInfo> newsSet = newsMap.keySet();
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
