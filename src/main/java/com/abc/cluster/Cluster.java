package com.abc.cluster;

import java.util.LinkedList;
import java.util.List;

import com.abc.db.entity.NewsInfo;

public class Cluster {
	List<NewsInfo> points;

	public Cluster() {
		points = new LinkedList<NewsInfo>();
	}
	
	public Cluster(List<NewsInfo> points) {
		this.points = points;
	}

	public List<NewsInfo> getPoints() {
		return points;
	}

	public void setPoints(List<NewsInfo> points) {
		this.points = points;
	}
	
	public void addPoint(NewsInfo point){
		points.add(point);
	}
	
	public NewsInfo getFirst(){
		return points.get(0);
	}

}