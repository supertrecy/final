package com.abc.cluster;

import java.util.List;
import java.util.Map;

import com.abc.db.entity.NewsInfo;

public interface AttachTag {
	
	public Map<String, Double> getNewsTagMap(NewsInfo news);
	
	public List<String> getNewsTagList(NewsInfo news);
	
}
