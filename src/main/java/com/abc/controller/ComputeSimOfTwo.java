package com.abc.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.abc.vsm.Seg;
import com.abc.vsm.Sim;
import com.abc.vsm.TFIDF;

public class ComputeSimOfTwo {
	private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file
	
	public static void main(String[] args) {
		/*
		 * In dir1,two article is totally same.
		 */
		String dir1 = "E:/testfiles/ComputeSimOfTwo/1";
		test(dir1);
		/*
		 * In dir2,two article is almost same except that one has been added two words at the beginning of document
		 */
		String dir2 = "E:/testfiles/ComputeSimOfTwo/2";
		test(dir2);
		/*
		 * In dir3,two articles describe different things
		 */
		String dir3 = "E:/testfiles/ComputeSimOfTwo/3";
		test(dir3);
		/*
		 * In dir4,two article is almost same except that one has been added two words at the end of document
		 */
		String dir4 = "E:/testfiles/ComputeSimOfTwo/4";
		test(dir4);
		/*
		 * In dir5,two article is almost same except that one has been added two words at the middle of document
		 */
		String dir5 = "E:/testfiles/ComputeSimOfTwo/5";
		test(dir5);
		
	}

	public static void test(String dir){
		List<Seg> list = new ArrayList<Seg>();
		List<String> filelist;
		try {
			filelist = readDirs(dir);
			for(String file : filelist){
				String text = ComputeSimOfTwo.readFile(file);
				Seg seg = new Seg(text);
				list.add(seg);
	        }
			
			System.out.println("两篇文章的相似度："
					+ Sim.vsm(list.get(0).normalTF(), list.get(1).normalTF()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileList.clear();
		System.out.println("****************************************************************************************************************");
	}
	
	public static List<String> readDirs(String filepath)
			throws FileNotFoundException, IOException {
		try {
			File file = new File(filepath);
			if (!file.isDirectory()) {
				System.out.println("输入的[]");
				System.out.println("filepath:" + file.getAbsolutePath());
			} else {
				String[] flist = file.list();
				for (int i = 0; i < flist.length; i++) {
					File newfile = new File(filepath + "\\" + flist[i]);
					if (!newfile.isDirectory()) {
						FileList.add(newfile.getAbsolutePath());
					} else if (newfile.isDirectory()) // if file is a directory,
														// call ReadDirs
					{
						readDirs(filepath + "\\" + flist[i]);
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return FileList;
	}

	// read file
	public static String readFile(String file) throws FileNotFoundException,
			IOException {
		StringBuffer strSb = new StringBuffer(); 
		InputStreamReader inStrR = new InputStreamReader(new FileInputStream(
				file), "gbk"); // byte streams to character streams
		BufferedReader br = new BufferedReader(inStrR);
		String line = br.readLine();
		while (line != null) {
			strSb.append(line).append("\r\n");
			line = br.readLine();
		}

		return strSb.toString();
	}
	
}
