package com.abc.vsm;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.abc.db.entity.NewsInfo;
import com.abc.util.WordSegUtil;

public class Weight {
	
	private static Map<String,Integer> allwords;
	private static int length_of_vector;
	
	public Weight(Map<String,Integer> allwords){
		Weight.allwords = allwords;
		length_of_vector = Weight.allwords.size();
	}
	
	public List<Double> computingBoolWeight(String docment){
		List<Double> vector = initVector();
		List<String> words = WordSegUtil.participle(docment);
		for (String word : words) {
			Integer index = allwords.get(word);
			if(index != null && vector.get(index.intValue())< 1){
				vector.set(index, 1.0);
			}
		}
		return vector;
	}
	
	public List<Double> computingTFWeight(String docment){
		List<Double> vector = initVector();
		List<String> words = WordSegUtil.participle(docment);
		return getTermFrequencies(words, vector);
	}
	
	public List<Double> computingTFIDFWeight(String docment,List<NewsInfo> newsList){
		List<Double> vector = initVector();
		List<String> words = WordSegUtil.participle(docment);
		vector = getTermFrequencies(words, vector);
		int length_of_words = words.size();
		for (String word : words) {
			Integer index = allwords.get(word);
			if(index != null){
				double idf = getIDF(word,newsList);
				vector.set(index, vector.get(index.intValue())*idf/length_of_words);
			}
		}
		return vector;
	}
	
	private double getIDF(String word,List<NewsInfo> newsList) {
		int docNums = newsList.size();
		double docNumsContainWord = 0.0;
		double idf = 0.0;
		for (NewsInfo newsInfo : newsList) {
			if(newsInfo.getContent().contains(word))
				docNumsContainWord++;
		}
		idf = Math.log10(docNumsContainWord>0?(docNums/docNumsContainWord):docNums);
		return idf;
	}

	private List<Double> getTermFrequencies(List<String> words,List<Double> vector){
		for (String word : words) {
			Integer index = allwords.get(word);
			if(index != null){
				vector.set(index, vector.get(index.intValue())+1);
			}
		}
		return vector;
	}
	
	private List<Double> initVector(){
		List<Double> vector = new LinkedList<>();
		for (int i = 0; i < length_of_vector; i++) {
			vector.add(0.0);
		}
		return vector;
	}
}
