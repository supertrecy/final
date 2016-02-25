package com.abc.vsm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Sim {
	/**
	 * V{v1，v2，v3，...，vn}
	 * 
	 * 模为
	 * 
	 * |v|=sqrt（v1*v1+v2*v2+…+vn*vn）
	 * 
	 * 两个向量的点积
	 * 
	 * m*n=n1*m1+n2*m2+......+nn*mn
	 * 
	 * 相似度
	 * 
	 * sim＝（m*n）/（|m|*|n|）
	 * 
	 * @return
	 */
	public static double vsm(HashMap<String, Integer> tf1,HashMap<String, Integer> tf2) {
		System.out.println("------------------------------------------------------------------");
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
		System.out.println("两篇文章文章的点积是："+dotProduct);
		return dotProduct/Math.sqrt(mod1*mod2);
	}
	
	private static int mod(HashMap<String, Integer> tf){
		int sum = 0;
		Set<String> keywords = tf.keySet();
		Iterator<String> it = keywords.iterator();
		while (it.hasNext()) {
			int fre = tf.get(it.next());
			sum += fre*fre;
		}
		System.out.println("該文章的模|v|是："+sum);
		return sum;
	}
}
