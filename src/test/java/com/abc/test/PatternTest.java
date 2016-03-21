package com.abc.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {
	public static void main(String[] args) throws FileNotFoundException {
		Scanner s = new Scanner(new FileInputStream(new File("e:\\1.txt")),"utf-8");
		StringBuilder sb = new StringBuilder();
		while (s.hasNext()) {
			sb.append(s.nextLine());
		}
		Pattern p = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(sb.toString());
		if(m.find()){
			String title = m.group(1).trim();
            int index = title.indexOf("-");
            if (index != -1) {
            	title = title.substring(0, index);      	
            }
            index = title.indexOf("_");
            if (index != -1) {
            	title = title.substring(0, index);      	
            }
            System.out.println(title);
		}
	}
}
