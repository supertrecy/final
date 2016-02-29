package com.abc.vsm;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * Hello world!
 *
 */
public class Seg {
	
	String text = null;
	
	public Seg(String text) {
		this.text = text;
	}

	private Vector<String> participle() {

		Vector<String> str1 = new Vector<String>();// 对输入进行分词

		try {
			StringReader reader = new StringReader(text);
			IKSegmenter ik = new IKSegmenter(reader, true);// 当为true时，分词器进行最大词长切分
			Lexeme lexeme = null;
			while ((lexeme = ik.next()) != null) {
				str1.add(lexeme.getLexemeText());
			}
			if (str1.size() == 0) {
				System.out.println("分词失败："+text);
				return str1;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return str1;
	}

	public HashMap<String, Integer> normalTF() {
		Vector<String> words = this.participle();
		HashMap<String, Integer> resTF = new HashMap<String, Integer>();

		if(words == null){
			System.out.println(words.size());
			return null;
		}
		else{
			for (String word : words) {
				if (resTF.get(word) == null) {
					resTF.put(word, 1);
				} else {
					resTF.put(word, resTF.get(word) + 1);
				}
			}
			Set<String> keywords = resTF.keySet();
			Iterator<String> it = keywords.iterator();
			/*System.out.println("词频大于1的词语有："); //TODO ComputeSimOfTwo中不需要注释
			while (it.hasNext()) {
				String word = it.next();
				int fre = resTF.get(word);
				if (fre > 0) {
					System.out.print(word + ":" + fre);
				}

			}
			System.out.println();*/
			return resTF;
		}
	}

}
