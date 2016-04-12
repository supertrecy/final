package com.abc.cluster;

import java.util.ArrayList;
import java.util.List;

import com.abc.db.entity.NewsInfo;
import com.abc.vsm.DocumentDimension;
import com.abc.vsm.Similarity;
import com.abc.vsm.Weight;

public class SimilarityRealTimeMatrix {

	private double[][] matrix;
	private int dimension;
	private List<List<Double>> vectorlist;
	private List<NewsInfo> newsList;

	public SimilarityRealTimeMatrix(List<NewsInfo> newsList) {
		long start = System.currentTimeMillis();
		/* 初始化相似度矩阵 */
		this.newsList = newsList;
		dimension = newsList.size();
		matrix = new double[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				matrix[j][i] = -1;
			}
		}
		System.out.println("***初始化矩阵耗时" + (double) (System.currentTimeMillis() - start) / 1000 + "秒");

		/* 获取每条新闻的向量表示 */
		start = System.currentTimeMillis();
		DocumentDimension dd = new DocumentDimension(newsList);
		System.out.println("***获取维度耗时" + (double) (System.currentTimeMillis() - start) / 1000 + "秒");

		Weight wc = new Weight(dimension, dd.getAllWordsOfDocument(), dd.getAllWordsDF());
		vectorlist = new ArrayList<>(dimension);
		for (NewsInfo newsInfo : newsList) {
			start = System.currentTimeMillis();
			vectorlist.add(wc.computingTFIDFWeight(newsInfo.getContent()));
		}
		System.out.println("***向量化耗时" + (double) (System.currentTimeMillis() - start) / 1000 + "秒");

	}
	

	public double get(NewsInfo news1, NewsInfo news2) {
		int i = newsList.indexOf(news1);
		int j = newsList.indexOf(news2);
		if (i == j)
			return 1.0;

		double result;
		if (matrix[i][j] == -1) {
			if (i > j)
				result = Similarity.cosineDistance(vectorlist.get(i), vectorlist.get(j));
			else
				result = Similarity.cosineDistance(vectorlist.get(j), vectorlist.get(i));
		} else {
			result = i > j ? matrix[i][j] : matrix[j][i];
		}
		return result;
	}

}
