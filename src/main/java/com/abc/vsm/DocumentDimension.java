package com.abc.vsm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.abc.db.entity.NewsInfo;
import com.abc.util.WordSegUtil;

public class DocumentDimension {
	private Map<String, Integer> allwords = null;
	private Map<String, Integer> allwordsDF = null;

//	public DocumentDimension() {
//		allwords = new HashMap<String, Integer>();
//	}

	public DocumentDimension(List<NewsInfo> newsList) {
		allwords = new HashMap<String, Integer>();
		allwordsDF = new HashMap<String, Integer>();
		int index = 0;
		for (NewsInfo newsInfo : newsList) {
			List<String> words = WordSegUtil.participle(newsInfo.getContent());
			List<String> wordsDF = new LinkedList<>();
			// 把该文章中还未包含到词语组的词语放入
			for (String word : words) {
				if (allwords.get(word) == null) {
					allwords.put(word, index++);
				}
				if (!wordsDF.contains(word))
					wordsDF.add(word);
			}
			// 统计该文章每个词的文档频率
			for (String word : wordsDF) {
				Integer num = allwordsDF.get(word);
				if (num == null) {
					allwordsDF.put(word, 1);
				} else {
					allwordsDF.put(word, 1 + num);
				}
			}
		}
	}

	/*public Map<String, Integer> getAllWordsOfDocument(List<NewsInfo> newsList) {
		int index = 0;
		for (NewsInfo newsInfo : newsList) {
			List<String> words = WordSegUtil.participle(newsInfo.getContent());
			List<String> wordsDF = new LinkedList<>();
			// 把该文章中还未包含到词语组的词语放入
			for (String word : words) {
				if (allwords.get(word) == null) {
					allwords.put(word, index++);
				}
				if (!wordsDF.contains(word))
					wordsDF.add(word);
			}
			// 统计该文章每个词的文档频率
			for (String word : wordsDF) {
				Integer num = allwordsDF.get(word);
				if (num == null) {
					allwordsDF.put(word, 1);
				} else {
					allwordsDF.put(word, 1 + num);
				}
			}
		}
		return allwords;
	}*/

	public Map<String, Integer> getAllWordsOfDocument() {
		return allwords;
	}

	public Map<String, Integer> getAllWordsDF() {
		return allwordsDF;
	}

	public void clear() {
		allwords.clear();
	}
}
