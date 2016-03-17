package com.abc.crawler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.crawler.extract.SearchUrlExtractor;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;

public class SearchHandler {

	public static final Logger LOG = LoggerFactory.getLogger(SearchHandler.class);
	private SearchUrlExtractor se = new SearchUrlExtractor();

	public SearchHandler() {
	}

	/**
	 * 对一组搜索词(或者说一个搜索主题)执行新闻搜索
	 * 
	 * @param search_words
	 */
	public void startNewsSearch(List<String> search_words) {
		StringBuilder sb = new StringBuilder("");
		for (String word : search_words) {
			sb.append(word + "+");
		}
		String query = sb.substring(0, sb.length() - 1);
		LOG.info("*** 当前处理新闻搜索任务的词组：" + query);
		/************ 获取搜索引擎返回结果 **************/
		/************ 在此指定各类搜索方式 **************/
		LOG.info("*** 开始执行新闻(资讯)搜索");

		/* 执行抓取 */
		Spider.create(new BaiduNewsPageProcessor()).addUrl(se.getSearchUrl(search_words, SearchUrlExtractor.BAIDU))
				.addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(5).run();

		Spider.create(new BingNewsPageProcessor()).addUrl(se.getSearchUrl(search_words, SearchUrlExtractor.BING))
				.addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(5).run();

		Spider.create(new SogouNewsPageProcessor()).addUrl(se.getSearchUrl(search_words, SearchUrlExtractor.SOGOU))
				.addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(5).run();

	}

	public static void main(String[] args) {
		List<String> kwords = new ArrayList<>();
		kwords.add("习近平");
		new SearchHandler().startNewsSearch(kwords);
	}

}
