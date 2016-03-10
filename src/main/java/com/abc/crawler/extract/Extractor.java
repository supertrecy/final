package com.abc.crawler.extract;

import java.io.UnsupportedEncodingException;
import java.util.List;

public abstract class Extractor {

	protected String initQueryWords(List<String> queryWords){
		StringBuilder sb = new StringBuilder("\"");
		for (String word : queryWords) {
			sb.append(word + " ");
		}
		String query = sb.toString().trim() + "\"";
		return query;
	}

	public abstract void generateSearchUrl(List<String> urls,List<String> keywords,int resultNum, int startIndex) throws UnsupportedEncodingException;
}
