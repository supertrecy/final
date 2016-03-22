package com.abc.vsm;

import java.util.HashMap;
import java.util.List;

import com.abc.util.WordSegUtil;

public class Seg {
	
	String text = null;
	
	public Seg(String text) {
		this.text = text;
	}

	public HashMap<String, Integer> normalTF() {
		List<String> words = WordSegUtil.participle(text);
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
			/*Set<String> keywords = resTF.keySet();
			Iterator<String> it = keywords.iterator();
			System.out.println("词频大于1的词语有："); //TODO ComputeSimOfTwo中不需要注释
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
