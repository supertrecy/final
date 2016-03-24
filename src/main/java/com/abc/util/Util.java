package com.abc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {
	/**
	 * 把url写入到一个html文件中，方便点击检查
	 * @param url
	 */
	
	private static String htmlfilename;
	
	static{
		htmlfilename = "E:\\"+System.currentTimeMillis()+".html";
	}
	
	public static void writeToHtmlFile(String url) {
		File f = new File(htmlfilename);
		try {
			if (!f.exists())
				f.createNewFile();
			Writer out = new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8");
			String link = "<a href=\"" + url + "\">" + url + "</a><br>";
			out.write(link);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> normalizeKeyword(String keyword) {
		String[] split = keyword.split("[\\|,; ]");
		List<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(split));
		return list;
	}
	
	public static String glueSearchWords(List<String> search_words){
		StringBuilder sb = new StringBuilder();
		for (String word : search_words) {
			sb.append(word);
			sb.append(";");
		}
		return sb.toString();
	}
}
