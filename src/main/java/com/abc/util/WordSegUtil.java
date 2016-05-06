package com.abc.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class WordSegUtil {

	public static List<String> participle(String text) {
		if(text == null)
			return null;
		List<String> str1 = new LinkedList<String>();// 对输入进行分词

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
}
