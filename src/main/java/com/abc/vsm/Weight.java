package com.abc.vsm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.abc.db.entity.NewsInfo;
import com.abc.util.WordSegUtil;

public class Weight {

	private static Map<String, Integer> allwords;
	private static int lengthOfVector;
	private Map<String, Double> wordWeightMap;

	public Weight(Map<String, Integer> allwords) {
		Weight.allwords = allwords;
		lengthOfVector = Weight.allwords.size();
		wordWeightMap = new HashMap<>();
	}

	public List<Double> computingBoolWeight(String docment) {
		List<Double> vector = initVector();
		List<String> words = WordSegUtil.participle(docment);
		for (String word : words) {
			Integer index = allwords.get(word);
			if (index != null && vector.get(index.intValue()) < 1) {
				vector.set(index, 1.0);
			}
		}
		return vector;
	}

	public List<Double> computingTFWeight(String docment) {
		List<Double> vector = initVector();
		List<String> words = WordSegUtil.participle(docment);
		return getTermFrequencies(words, vector);
	}

	public List<Double> computingTFIDFWeight(String docment, List<NewsInfo> newsList) {
		List<Double> vector = initVector();
		List<String> words = WordSegUtil.participle(docment);
		vector = getTermFrequencies(words, vector);
		int length_of_words = words.size();
		for (String word : words) {
			Integer index = allwords.get(word);
			if (index != null) {
				double idf = getIDF(word, newsList);
				vector.set(index, vector.get(index.intValue()) * idf / length_of_words);
			}
		}
		return vector;
	}

	public List<Double> computingTFIDFWeight2(String docment, List<NewsInfo> newsList) {
		List<Double> vector = initVector();
		List<String> words = WordSegUtil.participle(docment);
		vector = getTermFrequencies(words, vector);
		int length_of_words = words.size();
		for (String word : words) {
			Integer index = allwords.get(word);
			if (index != null) {
				double idf = getIDF(word, newsList);
				vector.set(index, vector.get(index.intValue()) * idf / length_of_words);
			}
		}
		wordWeightMap.clear();
		for (String word : words) {
			Integer index = allwords.get(word);
			double tmptf = vector.get(index);
			if (tmptf != 0) {
				wordWeightMap.put(word, tmptf);
			}
		}
		return vector;
	}

	private double getIDF(String word, List<NewsInfo> newsList) {
		int docNums = newsList.size();
		double docNumsContainWord = 0.0;
		double idf = 0.0;
		for (NewsInfo newsInfo : newsList) {
			if (newsInfo.getContent().contains(word))
				docNumsContainWord++;
		}
		idf = Math.log10(docNumsContainWord > 0 ? (docNums / docNumsContainWord) : docNums);
		return idf;
	}

	private List<Double> getTermFrequencies(List<String> words, List<Double> vector) {
		for (String word : words) {
			Integer index = allwords.get(word);
			if (index != null) {
				vector.set(index, vector.get(index.intValue()) + 1);
			}
		}
		return vector;
	}

	private List<Double> initVector() {
		List<Double> vector = new ArrayList<>(lengthOfVector);
		for (int i = 0; i < lengthOfVector; i++) {
			vector.add(0.0);
		}
		return vector;
	}

	private DecimalFormat df = new DecimalFormat("#0.000");
	private final int TAGS_NUM = 10;

	private Map<String, Double> optimizeMap(Map<String, Double> initMap) {
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		if (initMap != null && !initMap.isEmpty()) {
			List<Map.Entry<String, Double>> entryList = new ArrayList<>(initMap.entrySet());
			for (int i = 0; i < TAGS_NUM && i < wordWeightMap.size(); i++) {
				double max = 0;
				Entry<String, Double> e = null;
				for (Entry<String, Double> entry : entryList) {
					double tmp = entry.getValue();
					if (tmp > max) {
						e = entry;
						max = tmp;
					}
				}
				sortedMap.put(e.getKey(), Double.parseDouble(df.format(e.getValue())));
				e.setValue(-1.0);
			}
		}
		return sortedMap;
	}

	public Map<String, Double> getWordWeightMap() {
		return optimizeMap(wordWeightMap);
	}

}
