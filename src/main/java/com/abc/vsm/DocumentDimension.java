package com.abc.vsm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abc.db.entity.NewsInfo;
import com.abc.util.WordSegUtil;

public class DocumentDimension {
	private Map<String,Integer> allwords = null;
	
	public DocumentDimension() {
		allwords = new HashMap<String, Integer>();
	}

	public Map<String,Integer> getAllWordsOfDocument(List<NewsInfo> newsList){
		int index = 0;
		for (NewsInfo newsInfo : newsList) {
			List<String> words = WordSegUtil.participle(newsInfo.getContent());
			for (String word : words) {
				if(allwords.get(word) == null){
					allwords.put(word, index++);
				}
			}
		}
		return allwords;
	}
	
	public void clear(){
		allwords.clear();
	}
}
