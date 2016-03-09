package com.abc.crawler.extract;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 负责调用各个搜索引擎提取器获取结果并写到相关目录！
 * @author hjy
 *
 */
public class SearchResultsExtractor {
	public static final Logger LOG = LoggerFactory.getLogger(SearchResultsExtractor.class);
	
	private BaiduExtractor be = new BaiduExtractor(); // 百度解析器
	private BingExtractor bing = new BingExtractor(); // Bing解析器
	private SogouExtractor sogou = new SogouExtractor();//Sogou解析器
		/**
	 * 对各个搜索引擎执行新闻搜索
	 * @param queryWords
	 * @param outputDir
	 */
	public void getNewsSearchResults(ArrayList<String> queryWords, String outputDir) {
		LinkedList<String> urls = new LinkedList<String>();
		LinkedList<String> time = new LinkedList<String>();
		int baidunewsnum = 500; // 百度新闻搜索结果数, 最大760
		int bingnewsnum = 500; // Bing新闻搜索结果数
		int sogounum = 500;// Sag新闻搜索结果数,最大1000
		
		try{
			be.extractUrls(queryWords, baidunewsnum, 0, urls, time, 2);
		}catch(Exception e){
			LOG.error("错误: 获取百度新闻搜索结果出错");
		}
		
		try{
			bing.extractUrls(queryWords, bingnewsnum, 0, urls, time, 2);
		}catch(Exception e){
			LOG.error("错误: 获取必应新闻搜索结果出错");
		}
		
		try{
			sogou.extractUrls(queryWords, sogounum, urls, time, 2);
		}catch(Exception e){
			LOG.error("错误: 获取搜狗新闻搜索结果出错");
		}
		
		
		String filename = outputDir + "/newssearch-" + System.currentTimeMillis() + ".txt";
		writeRootUrls(urls, time, filename);
	}
	//LOOKAT add by yh
	public List<String> getNewsResults(ArrayList<String> queryWords, String outputDir) {
		LinkedList<String> urls = new LinkedList<String>();
		LinkedList<String> time = new LinkedList<String>();
		int baidunewsnum = 500; // 百度新闻搜索结果数, 最大760
		int bingnewsnum = 500; // Bing新闻搜索结果数
		int sogounum = 500;// Sag新闻搜索结果数,最大1000
		
		try{
			be.extractUrls(queryWords, baidunewsnum, 0, urls, time, 2);
		}catch(Exception e){
			LOG.error("错误: 获取百度新闻搜索结果出错");
		}
		
		try{
			bing.extractUrls(queryWords, bingnewsnum, 0, urls, time, 2);
		}catch(Exception e){
			LOG.error("错误: 获取必应新闻搜索结果出错");
		}
		
		try{
			sogou.extractUrls(queryWords, sogounum, urls, time, 2);
		}catch(Exception e){
			LOG.error("错误: 获取搜狗新闻搜索结果出错");
		}
		return urls;
	}
	

	/**
	 * 基于新闻站点的网页搜索(目前只采用百度+sogou)
	 * @param queryWords
	 * @param outputDir
	 */
	public void getWebSearchResultsByNewsSites(ArrayList<String> queryWords, HashSet<String> sitelist, 
			String outputDir) {
		LinkedList<String> urls = new LinkedList<String>();
		LinkedList<String> time = new LinkedList<String>();
		int baidusitenum = 400; // 搜索结果数, 最大760
		int sogounum = 400;
		
		String query = be.translateBaiduQueryWords(queryWords);
		
		for (String site : sitelist) {
			String q = query + " site:" + site;
			LOG.info("当前站点搜索词: " + q);
			try {
				be.extractUrls(q, baidusitenum, 0, urls, time, 1);
			} catch (Exception e) {
				LOG.error("错误ERROR: 获取百度网页搜索结果出错:  " + q);
			}
			
		}
		
		for (String site : sitelist) {
			String q = query + " site:" + site;
			LOG.info("当前站点搜索词: " + q);
			try {
				sogou.extractUrls(q, sogounum, urls, time, 1);
			} catch (Exception e) {
				LOG.error("错误ERROR: 获取搜狗网页搜索结果出错:  " + q);
			}
			
		}
		
		

		String filename = outputDir + "/newssitesearch-" + System.currentTimeMillis() + ".txt";
		writeRootUrls(urls, time, filename);
	}	
	public void writeRootUrls(List<String> urls, List<String> time, String filename) {
		File file = new File(filename);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter writer = new FileWriter(file, true); // 追加
	        for (int i = 0; i < urls.size(); i++) {
	        	String s = urls.get(i) + "\t" + "pubtime=" + time.get(i);
	        	String pubtime = time.get(i);
	        	if (!"".equals(pubtime)) { // 时间不为空的才写入文件
	        		writer.write(s);
		        	writer.write("\n");
	        	}
	        }
	        writer.flush();
	        writer.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SearchResultsExtractor se = new SearchResultsExtractor();
		
		String outputDir = "urls";
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("四川大学");
		
//		se.getNewsSearchResults(list, outputDir);
		
		HashSet<String> sitelist = new HashSet<String>();
		sitelist.add("news.sina.com.cn");
		
		se.getWebSearchResultsByNewsSites(list, sitelist, outputDir);
	}

	
}
