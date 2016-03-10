package com.abc.crawler.extract;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 负责调用各个搜索引擎提取器获取结果并写到相关目录！
 * 
 * @author hjy
 *
 */
public class SearchUrlExtractor {
	public static final Logger LOG = LoggerFactory.getLogger(SearchUrlExtractor.class);

	private BaiduExtractor be = new BaiduExtractor(); // 百度解析器
	private BingExtractor bing = new BingExtractor(); // Bing解析器
	private SogouExtractor sogou = new SogouExtractor();// Sogou解析器

	public static final int BAIDU = 1;
	public static final int BING = 2;
	public static final int SOGOU = 3;

	public String[] getSearchUrl(List<String> queryWords, int searchEngines) {
		List<String> urls = new LinkedList<String>();
		int baidunewsnum = 500; // 百度新闻搜索结果数, 最大760
		int bingnewsnum = 500; // Bing新闻搜索结果数
		int sogounum = 500;// Sag新闻搜索结果数,最大1000

		switch (searchEngines) {
		case BAIDU:
			try {
				be.generateSearchUrl(urls, queryWords, baidunewsnum, 0);
			} catch (Exception e) {
				LOG.error("错误: 生成百度搜索url出错");
			}
			break;
		case BING:
			try {
				bing.generateSearchUrl(urls, queryWords, bingnewsnum, 0);
			} catch (Exception e) {
				LOG.error("错误: 生成必应搜索url出错");
			}
			break;
		case SOGOU:
			try {
				sogou.generateSearchUrl(urls, queryWords, sogounum, 0);
			} catch (Exception e) {
				LOG.error("错误: 生成搜狗搜索url出错");
			}
			break;
		default:
			break;
		}
		String[] urlsArray = new String[urls.size()];
		return urls.toArray(urlsArray);
	}

}
