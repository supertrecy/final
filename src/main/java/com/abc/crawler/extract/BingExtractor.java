package com.abc.crawler.extract;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 参数与对应搜索格式
 * 1：网页搜索												20141211测
 * 2：资讯搜索：分为按相关度排序、按时间排序两种方式			20141211测
 * 
 * 说明：
 * 1.资讯搜索虽然参数也是以10为单位累加的，但是每页显示往往不到10条，一般为7~9。 
 * 2.Bing资讯没有聚合现象	
 * 
 * Bing搜索的两个词之间默认是AND关系。A AND B与A B的搜索结果是一样的。
 * 短语查询需要加双引号。
 * 没发现百度搜索进行AND搜索的方法……
 * @author hjy
 *
 */
public class BingExtractor extends Extractor{
	public static final Log LOG = LogFactory.getLog(BingExtractor.class);
	private int numPerPage = 10;
	

	@Override
	protected String initQueryWords(List<String> queryWords) {
		StringBuilder sb = new StringBuilder("");
		for (String word : queryWords) {
			sb.append("\"" + word + "\" ");
		}
		String query = sb.toString().trim();
		return query;
	}


	@Override
	public void generateSearchUrl(List<String> urls, List<String> keywords, int resultNum, int startIndex)
			throws UnsupportedEncodingException {
		String keyword = initQueryWords(keywords);
		if (startIndex < 0)
			startIndex = 0;
		if (keyword == null || "".equals(keyword)) {
			return;
		}
		String encodeQuery = URLEncoder.encode(keyword, "UTF-8");
		int page = resultNum / numPerPage;
		if ((resultNum % numPerPage) != 0)
			page += 1;

		int curStart = startIndex;
		for (int i = 0; i < page; i++, curStart += 10) {
			String address = "http://cn.bing.com/news/search?q=" + encodeQuery + "&qft=sortbydate"+ URLEncoder.encode("=\"1\"", "UTF-8") + "&first="
					+ curStart; // 按时间排序
			urls.add(address);
		}
		
	}

}
