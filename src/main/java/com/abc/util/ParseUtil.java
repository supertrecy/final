package com.abc.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ParseUtil {

	public static String parseNewsSource(String html) {
		Document doc = Jsoup.parse(html);
		Elements source = doc.select(":matches((?:来源|来源于|稿源|摘自)[：:\\s]\\s*?(?:<.*?>)+([^<>\\s]+)<)");
		if (source == null) {
			System.out.println("第一种来源匹配失败");
			source = doc.select(":matches((?:来源|来自)[：:\\s]\\s*?(.*?)\\s*?[<&\\)）])");
		}
		if (source == null) {
			System.out.println("第二种来源匹配失败，无法提取文章来源 ！");
		}
		
		String s = source.get(0).nextElementSibling().text();
		if(s!=null){
			s.substring(s.lastIndexOf(">")+1);
			if(s.lastIndexOf("<")!=-1)
				s.substring(0,s.lastIndexOf("<")-1);
			return s;
		}
		return "";
	}
}
