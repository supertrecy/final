package com.abc.crawler.extract;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 参数与对应搜索格式 1：网页搜索 2：新闻搜索
 * 
 * 说明： 1. 这里百度网页搜索结果页面风格和平时不太一样，因为后面有“&tn=baidulocal”参数,但返回内容是一致的。 2.
 * 百度新闻搜索存在着聚合现象，即相同新闻会聚合在一起。
 * 
 * @author hjy
 *
 */
public class BaiduExtractor extends Extractor {

	public static final Log LOG = LogFactory.getLog(BaiduExtractor.class);
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	private int period = 7; // 针对新闻搜索设置period天以内的新闻
	private int numPerPage = 50; // 每页返回的结果数


	private String encodeSearchUrl(String sQuery, int start, long startTime, long endTime) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();

		qparams.add(new NameValuePair("pn", String.valueOf(start)));
		qparams.add(new NameValuePair("rn", String.valueOf(numPerPage)));
		if (startTime == 0 && endTime == 0) { // 表示不限制时间

		} else {
			qparams.add(new NameValuePair("bt", String.valueOf(startTime)));
			qparams.add(new NameValuePair("et", String.valueOf(endTime)));
		}
		qparams.add(new NameValuePair("cl", "2"));

		HttpURL httpURL = null;
		try {
			httpURL = new HttpURL("news.baidu.com", -1, "/ns",
					EncodingUtil.formUrlEncode(qparams.toArray(new NameValuePair[qparams.size()]), "utf-8"));
			String url = httpURL.getURI().toString() + "&q1=" + sQuery;
			return url;
		} catch (URIException e) {
			LOG.fatal("Get baidu url failed, " + e.getMessage());
		}
		return "";
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
		String encodeQuery = URLEncoder.encode(keyword.trim(), "UTF-8"); // 编码查询词

		int n = resultNum / numPerPage; // 页数
		int remainder = resultNum % numPerPage;
		int start = startIndex;

		/* 计算开始、结束时间的long类型参数，这是是设置7天以内 */
		long startTime = 0, endTime = 0;
		Calendar cal = Calendar.getInstance();
		try {
			cal.add(Calendar.DATE, 1); // 明天
			endTime = (sf.parse(sf.format(cal.getTime())).getTime() - 1) / 1000; // 结束时间(今天)
			cal.add(Calendar.DATE, period * -1); // 最近period天的新闻
			startTime = sf.parse(sf.format(cal.getTime())).getTime() / 1000;
		} catch (ParseException e) {
			LOG.error(e.getMessage());
			return;
		}
		for (int i = 0; i < n; ++i, start += numPerPage) {
			urls.add(encodeSearchUrl(encodeQuery, start,startTime, endTime));
		}
		if (remainder > 0)
			urls.add(encodeSearchUrl(encodeQuery, start, startTime, endTime));
	}

}
