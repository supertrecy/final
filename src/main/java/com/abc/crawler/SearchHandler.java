package com.abc.crawler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.crawler.extract.SearchUrlExtractor;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;

public class SearchHandler {

	public static final Logger LOG = LoggerFactory.getLogger(SearchHandler.class);
	private SearchUrlExtractor se = new SearchUrlExtractor();
	private static final int THREAD_NUM = 5;

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

		BaiduNewsPageProcessor baidu = new BaiduNewsPageProcessor();
		BingNewsPageProcessor bing = new BingNewsPageProcessor();
		SogouNewsPageProcessor sogou = new SogouNewsPageProcessor();
		BaiduNewsPageProcessor.setSearchWords(search_words);

		/* 执行抓取 */
		Spider.create(baidu).addUrl(se.getSearchUrl(search_words, SearchUrlExtractor.BAIDU))
				.addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(THREAD_NUM).run();

		Spider.create(bing).addUrl(se.getSearchUrl(search_words, SearchUrlExtractor.BING))
				.addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(THREAD_NUM).run();

		Spider.create(sogou).addUrl(se.getSearchUrl(search_words, SearchUrlExtractor.SOGOU))
				.addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(THREAD_NUM).run();
		
		BaiduNewsPageProcessor.clearSearchWords();

	}

}
