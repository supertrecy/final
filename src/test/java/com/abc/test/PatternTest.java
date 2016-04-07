package com.abc.test;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {
	public static void main(String[] args) throws FileNotFoundException {
//		Scanner s = new Scanner(new FileInputStream(new File("e:\\1.txt")),"utf-8");
//		StringBuilder sb = new StringBuilder();
//		while (s.hasNext()) {
//			sb.append(s.nextLine());
//		}
//		Pattern p = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//		Matcher m = p.matcher(sb.toString());
//		if(m.find()){
//			String title = m.group(1).trim();
//            int index = title.indexOf("-");
//            if (index != -1) {
//            	title = title.substring(0, index);      	
//            }
//            index = title.indexOf("_");
//            if (index != -1) {
//            	title = title.substring(0, index);      	
//            }
//            System.out.println(title);
//		}
		
		String s= "来源：<a href=\"http://www.yidianzixun.com/home?page=article&id=0CuHUQQ0&up=331\" target=\"_blank\">中国青年网<";
		String reg = "http[s]?://([\\w|\\-|\\.|//]+)+([\\w-./?%&=]*)";
		Pattern p = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(s);
		if (matcher.find())
			System.out.println(matcher.group(0).trim());
		else
			System.out.println("没找到");
		
		
	}	
}
