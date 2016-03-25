package com.abc.vsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.abc.db.entity.NewsInfo;

public class Vsm {
	/**
	 * V{v1，v2，v3，...，vn} 模为 |v|=sqrt（v1*v1+v2*v2+…+vn*vn）
	 * 两个向量的点积  m*n=n1*m1+n2*m2+......+nn*mn
	 * 相似度 sim＝（m*n）/（|m|*|n|）
	 * @return 两个向量相似度
	 */
	public static double compareTwo(HashMap<String, Integer> tf1,HashMap<String, Integer> tf2) {
		//System.out.println("------------------------------------------------------------------");
		int mod1 = mod(tf1);
		int mod2 = mod(tf2);
		double dotProduct = 0;
		Set<String> keywords1 = tf1.keySet();
		Set<String> keywords2 = tf2.keySet();
		Iterator<String> it1 = keywords1.iterator();
		Iterator<String> it2 = keywords2.iterator(); //TODO how to process the problem two vectors have different dimension
		while (it1.hasNext() && it2.hasNext()) {
			int num1 = tf1.get(it1.next());
			int num2 = tf2.get(it2.next());
			dotProduct += num1 * num2;
		}
		//System.out.println("两篇文章文章的点积是："+dotProduct);
		return dotProduct/Math.sqrt(mod1*mod2);
	}
	
	/**
	 * @return 两个文本相似度
	 */
	public static double compareTwo(String text1,String text2) {
		HashMap<String, Integer> tf1 = new Seg(text1).normalTF();
		HashMap<String, Integer> tf2 = new Seg(text2).normalTF();
		int mod1 = mod(tf1);
		int mod2 = mod(tf2);
		double dotProduct = 0;
		Set<String> keywords1 = tf1.keySet();
		Set<String> keywords2 = tf2.keySet();
		Iterator<String> it1 = keywords1.iterator();
		Iterator<String> it2 = keywords2.iterator(); //TODO how to process the problem two vectors have different dimension
		while (it1.hasNext() && it2.hasNext()) {
			int num1 = tf1.get(it1.next());
			int num2 = tf2.get(it2.next());
			dotProduct += num1 * num2;
		}
		return dotProduct/Math.sqrt(mod1*mod2);
	}
	
	
	/**
	 * @return 新闻集合，相同内容新闻被划分在一起
	 */
	public static List<List<NewsInfo>> compareMutiple(List<NewsInfo> newsList) {
		List<List<NewsInfo>> newsGroup = new ArrayList<List<NewsInfo>>();
		List<Boolean> tagList = new ArrayList<Boolean>();
		List<HashMap<String, Integer>> tfList = new ArrayList<HashMap<String, Integer>>();
		int size = newsList.size();
		for (int i = 0; i < size; i++) {
			tagList.add(new Boolean(true));
			HashMap<String, Integer> tf = new Seg(newsList.get(i).getContent()).normalTF();
			tfList.add(tf);
		}
		
		for (int i = 0; i < size-1; i++) {
			if(tagList.get(i)){
				List<NewsInfo> sameNews = new ArrayList<NewsInfo>();
				int j = i+1;
				NewsInfo news1 = newsList.get(i);
				sameNews.add(news1);
				for (; j < size; j++) {
					if(tagList.get(j)){
						NewsInfo news2 = newsList.get(j);
						if(Vsm.compareTwo(tfList.get(i),tfList.get(j)) == 1){
							sameNews.add(news2);
							tagList.set(j, new Boolean(false));
						}
					}else{
						continue;
					}
				}
				newsGroup.add(sameNews);
				tagList.set(i, new Boolean(false));
			}else{
				continue;
			}
		}
		return newsGroup;
	}

	private static int mod(HashMap<String, Integer> tf){
		int sum = 0;
		Set<String> keywords = tf.keySet();
		Iterator<String> it = keywords.iterator();
		while (it.hasNext()) {
			int fre = tf.get(it.next());
			sum += fre*fre;
		}
		//System.out.println("該文章的模|v|是："+sum);  //TODO ComputeSimOfTwo中不需要注释
		return sum;
	}
}
