package com.abc.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.crawler.extract.SearchResultsExtractor;
import com.google.common.collect.Lists;

import us.codecraft.webmagic.Spider;

public class SearchHandler {

	public static final Logger LOG = LoggerFactory.getLogger(SearchHandler.class);
	public SearchResultsExtractor se = new SearchResultsExtractor();

	String dataDir = "";
	String urlDir = "";

	/**
	 * 对一组搜索词(或者说一个搜索主题)执行新闻搜索
	 * @param search_words
	 */
	public void startNewsSearch(ArrayList<String> search_words) {
		StringBuilder sb = new StringBuilder("");
		for (String word : search_words) {
			sb.append(word + "+");
		}
		String query = sb.substring(0, sb.length() - 1);
		LOG.info("*** 当前处理新闻搜索任务的词组：" + query);

		/* 初始化 */
		init(query); // 指定该组词的种子目录和CrawlDb目录

		/************ 获取搜索引擎返回结果 **************/
		/************ 在此指定各类搜索方式 **************/

		LOG.info("*** 开始执行新闻(资讯)搜索");
		List<String> urls = se.getNewsResults(search_words, urlDir);

		/* 执行抓取 */
		// TODO
		String[] arr_urls = new String[urls.size()];
		Spider.create(new NewsPageProcessor())
		.addUrl(urls.toArray(arr_urls))
        //开启5个线程抓取
        .thread(5)
        //启动爬虫
        .run();

		deleteFiles(urlDir); // 删除搜索url文件
	}
	

	private void init(String query) {
		int hash = query.hashCode();
		dataDir = "crawldata_" + hash;
		urlDir = "url_" + hash;
		LOG.info("CRAWL DATA:" + dataDir + ", URL DIR:" + urlDir);

		try {
			File f = new File(dataDir);
			f = new File(urlDir);
			if (!f.exists()) {
				f.mkdir();
			}
		} catch (Exception e) {
			LOG.error("初始化错误：" + e.getMessage());
		}
	}


	private void deleteFiles(String urlDir) {
		File dir = new File(urlDir);
		try {
			for (File f : dir.listFiles()) {
				f.delete();
			}
		} catch (Exception e) {
			LOG.error("ERROR错误: " + e.getMessage());
		}
	}

}
