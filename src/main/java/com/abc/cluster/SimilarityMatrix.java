package com.abc.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.abc.db.entity.NewsInfo;
import com.abc.vsm.DocumentDimension;
import com.abc.vsm.Similarity;
import com.abc.vsm.Weight;

public class SimilarityMatrix implements AttachTag {

	private double[][] matrix;
	private int dimension;
	private List<List<Double>> vectorlist;
	protected Map<NewsInfo, Map<String, Double>> newsVectorMap = null;

	public SimilarityMatrix(List<NewsInfo> newsList, boolean needTag) {
		/* 初始化相似度矩阵 */
		dimension = newsList.size();
		matrix = new double[dimension][dimension];

		/* 获取每条新闻的向量表示 */
		DocumentDimension dd = new DocumentDimension();
		Weight wc = new Weight(dd.getAllWordsOfDocument(newsList));
		vectorlist = new ArrayList<>(dimension);
		if (needTag) {
			newsVectorMap = new HashMap<>();
			for (NewsInfo newsInfo : newsList) {
				vectorlist.add(wc.computingTFIDFWeight2(newsInfo.getContent(), newsList));
				newsVectorMap.put(newsInfo, wc.getWordWeightMap());
			}
		} else {
			for (NewsInfo newsInfo : newsList)
				vectorlist.add(wc.computingTFIDFWeight(newsInfo.getContent(), newsList));
		}

		/* 计算不同新闻的相似度，并存入到矩阵中 */
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (i > j)
					matrix[i][j] = Similarity.cosineDistance(vectorlist.get(i), vectorlist.get(j));
				else
					matrix[i][j] = -1;
			}
		}
	}

	public SimilarityMatrix(List<NewsInfo> newsList) {
		/* 初始化相似度矩阵 */
		dimension = newsList.size();
		matrix = new double[dimension][dimension];

		/* 获取每条新闻的向量表示 */
		DocumentDimension dd = new DocumentDimension(newsList);
		Weight wc = new Weight(dimension, dd.getAllWordsOfDocument(), dd.getAllWordsDF());
		vectorlist = new ArrayList<>(dimension);
		for (NewsInfo newsInfo : newsList)
			vectorlist.add(wc.computingTFIDFWeight(newsInfo.getContent()));

		/* 计算不同新闻的相似度，并存入到矩阵中 */
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (i > j)
					matrix[i][j] = Similarity.cosineDistance(vectorlist.get(i), vectorlist.get(j));
				else
					matrix[i][j] = -1;
			}
		}
	}

	public double get(int i, int j) {
		return i > j ? matrix[i][j] : matrix[j][i];
	}

	@Override
	public Map<String, Double> getNewsTagMap(NewsInfo news) {
		return newsVectorMap.get(news);
	}

	@Override
	public List<String> getNewsTagList(NewsInfo news) {
		return new LinkedList<>(newsVectorMap.get(news).keySet());
	}

//	public static void main(String[] args) {
//		String keyword = "呼吁实现无核世界;";
//		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(keyword);
//		int dimension = newsList.size();
//		long start = System.currentTimeMillis();
//		DecimalFormat df = new DecimalFormat("#0.00");
//		SimilarityMatrix sm = new SimilarityMatrix(newsList);
//		for (int i = 0; i < dimension; i++) {
//			for (int j = 0; j < dimension; j++) {
//				if (i > j)
//					System.out.print(df.format(sm.get(i, j)) + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("新方法耗时" + (double) (System.currentTimeMillis() - start) / 1000 + "秒");
//
//		start = System.currentTimeMillis();
//		SimilarityMatrix sm2 = new SimilarityMatrix(newsList);
//		for (int i = 0; i < dimension; i++) {
//			for (int j = 0; j < dimension; j++) {
//				if (i > j)
//					System.out.print(df.format(sm2.get(i, j)) + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("旧方法耗时" + (double) (System.currentTimeMillis() - start) / 1000 + "秒");
//
//	}

}
