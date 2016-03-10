package com.abc.crawler.extract;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 参数searchType 与对应搜索格式
 * 1：网页搜索								
 * 2：新闻搜索 分为按时间排序和按相关性排序
 * 3：论坛搜索 分为一天内、一周内、一月内和一年内
 * 4：博客搜索 分为搜博主、搜博文和搜全部
 * 
 * 说明：
 * 1. sogou的新闻搜索中新闻搜索也是聚合在一起的
 * 2. sogou新闻搜索的编码是gbk，网页搜索的编码是UTF-8
 * 3. 搜索关键词也需要编码
 * @author mcx
 *
 */
public class SogouExtractor extends Extractor{

	public static final Log LOG = LogFactory.getLog(SogouExtractor.class);
	private int numPerPage = 10;
	
	@Override
	public void generateSearchUrl(List<String> urls, List<String> keywords, int resultNum, int startIndex)
			throws UnsupportedEncodingException {
		String keyword = initQueryWords(keywords);
		if (startIndex < 0)
			startIndex = 0;
		if (keyword == null || "".equals(keyword)) {
			return;
		}
		int page = resultNum / numPerPage;
		if ((resultNum % numPerPage) != 0) page += 1;
		String charset = "GBK";
			String encodeQuery = URLEncoder.encode(keyword, charset);
			for(int i =0;i < page;i++){
				String address = "http://news.sogou.com/news?query="+ encodeQuery +"&page="+i+"&sort=0";
				urls.add(address);
			}
		
	}
}
