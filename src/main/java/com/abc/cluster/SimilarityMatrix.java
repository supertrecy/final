package com.abc.cluster;

import java.util.ArrayList;
import java.util.List;

import com.abc.db.entity.NewsInfo;
import com.abc.vsm.DocumentDimension;
import com.abc.vsm.Similarity;
import com.abc.vsm.Weight;

public class SimilarityMatrix {
	
	private double[][] matrix;
	private int dimension;
	private List<List<Double>> vectorlist;

	public SimilarityMatrix(List<NewsInfo> newsList) {
		/*初始化相似度矩阵*/
		dimension = newsList.size();
		matrix = new double[dimension][dimension];
		
		/*获取每条新闻的向量表示*/
		DocumentDimension dd = new DocumentDimension();
		Weight wc = new Weight(dd.getAllWordsOfDocument(newsList));
		vectorlist = new ArrayList<>(dimension);
		for (NewsInfo newsInfo : newsList) {
			vectorlist.add(wc.computingTFIDFWeight(newsInfo.getContent(), newsList));
		}
		
		/*计算不同新闻的相似度，并存入到矩阵中*/
		for (int i = 0; i < dimension ; i++) {
			for (int j = 0; j < dimension; j++) {
				if(i>j)
					matrix[i][j] = Similarity.cosineDistance(vectorlist.get(i), vectorlist.get(j));
				else
					matrix[i][j] = -1;
			}
		}
	}
	
	public double get(int i,int j){
		return i>j?matrix[i][j]:matrix[j][i];
	}
	
	
}
