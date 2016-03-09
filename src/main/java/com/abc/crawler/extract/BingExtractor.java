package com.abc.crawler.extract;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class BingExtractor {
	public static final Log LOG = LogFactory.getLog(BingExtractor.class);
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	public void extractUrls(ArrayList<String> queryWords, int num, int start, List<String> urls, List<String> time, 
			int searchType) throws Exception {
		String query = translateBingQueryWords(queryWords);
		extractUrls(query, num, start, urls, time, searchType);
	}
	
	public void extractUrls(String query, int num, int start, List<String> urls, List<String> time, 
			int searchType) throws Exception {		
		String charset = "UTF-8";
		String encodeQuery = URLEncoder.encode(query, charset);
		
		if (searchType == 1) {
			extractUrls_webpage(encodeQuery, num, start, urls, time);
		} else if (searchType == 2) {
			extractUrls_news(encodeQuery, num, start, urls, time);
		}
	}
	
	public void extractUrls_webpage(String encodeQuery, int num, int start, List<String> urls, List<String> time) 
		throws Exception {		
		int page = num / 10;
		if ((num % 10) != 0) page += 1; // 假如num不是10的倍数，会略有多余

		int curStart = start;
		for (int i = 0; i < page; i++, curStart += 10) {
			Thread.sleep(1000);
			String address = "http://cn.bing.com/search?q=" + encodeQuery + "&first=" + curStart;
			URL url = new URL(address);

			LOG.info("Bing网页搜索："+ address);
			String html = getContent(url, "UTF-8");
			Element body = Jsoup.parse(html);
			Element subresults = body.select("#b_results").first(); // 2014-10-11
			Elements resultlist = subresults.select(".b_algo");
	
			for (Element result : resultlist) {
				Element e = result.getElementsByTag("h2").first().child(0);
				String link = e.attr("href");
				String title = e.text().trim();
				String pubtime = result.select(".b_attribution").first().ownText();
				pubtime = translateTime(pubtime);
		
				if (pubtime != null) {
					urls.add(link);
					time.add(pubtime);
					LOG.info(link+"----"+pubtime);
				} else {
					LOG.warn(link+" 时间格式错误!");
				}
			}	
			if (!html.contains("下一页"))
				break;
		}
	}
	
	//Bing资讯搜索
	public void extractUrls_news(String encodeQuery, int num, int start, List<String> urls, List<String> time) 
		throws Exception {		
		int page = num / 10;
		if ((num % 10) != 0) page += 1;
		
		int curStart = start;
		for (int i = 0; i < page; i++, curStart += 10) {
			Thread.sleep(1000);
			String address = "http://cn.bing.com/news/search?q=" + encodeQuery + "&qft=sortbydate%3d\"1\"" +
					"&first=" + curStart; // 按时间排序
			URL url = new URL(address);
			LOG.info("Bing资讯搜索:"+address);
			System.out.println("Bing资讯搜索:"+address);
			String html = getContent(url, "UTF-8");
			Element body = Jsoup.parse(html);
			Element subresults = body.select(".NewsResultSet").first();
			Elements resultlist = subresults.select(".sn_r");

			for (Element result : resultlist) {
				Element titleNode = result.child(0);
				String link = titleNode.child(0).attr("href");
				String title = titleNode.text();
				
				String pubtime = result.select(".sn_tm").first().ownText();
				pubtime = pubtime.replaceAll("/", "-");
				pubtime = translateTime(pubtime);
				
				if (pubtime != null) {
					urls.add(link);
					time.add(pubtime);
					LOG.info(link + "----" + pubtime);
					LOG.info(title);
					System.out.println(link+"----"+pubtime);
					System.out.println(title);
				} else {
					LOG.warn(link+" WARN:时间格式错误!");
					LOG.info(title);
				}
			}	
			if (!html.contains("class=\"sb_pagN\""))
				break;
		}
	}
	
	public String getContent(URL url, String encoding) {
		String content = null;
		HttpURLConnection httpConnection = null; 
		InputStream in = null; 
		ByteArrayOutputStream output = null;
		int bufferSize = 8 * 1024;
		byte[] con;
		
		try {
			httpConnection = (HttpURLConnection)url.openConnection(); 
			httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:28.0) Gecko/20100101 Firefox/28.0");
			httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//			httpConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			httpConnection.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			httpConnection.setRequestProperty("Connection", "keep-alive");
			// 这里cookie有什么规则没？？加入cookie就可以提取20页之后的内容。。。最好用HttpClient，因为会自动管理Cookie
			httpConnection.setRequestProperty("Cookie", "MUID=1F6D18F438D56CE3080E1EE539D66C6A; SRCHD=MS=3299194&SM=1&"
					+ "D=3289107&AF=NOFORM; SRCHUID=V=2&GUID=1D16991CB48B4F379DE88D5FB50EB636; SRCHUSR=AUTOREDIR=0&GEOVAR=&"
					+ "DOB=20140403; SRCHHPGUSR=NTAB=0; s_vnum=1399127473384%26vn%3D1; s_nr=1396535839565; _SS=SID="
					+ "6CA8D318D95A4181BBA6777969C0402B&CW=1362&CH=461&bIm=468467&nhIm=53-; SCRHDN=ASD=0&DURL=#; WLS="
					+ "TS=63532694265; _HOP=");
			httpConnection.setConnectTimeout(10000);  
			httpConnection.setReadTimeout(30000);  
			httpConnection.connect(); 
			
			if (httpConnection.getResponseCode() >= 400) {
				throw new RuntimeException("Build connection failed! " + url);
			}

			in = httpConnection.getInputStream();
			output = new ByteArrayOutputStream(bufferSize);
			byte[] b = new byte[bufferSize];
			int i = 0;
			
			while ((i = in.read(b)) != -1 ) {
				//System.out.println("reading" + b.length + "---" + i);
				output.write(b, 0, i);
			}
			con = output.toByteArray();	
			content = new String(con, encoding);
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());  
		} catch (IOException e) {
			System.out.println("io error-----" + e.getMessage());
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (output != null)
					output.close();
				if (in != null)
					in.close(); 
			} catch (Exception e) {
				;
			}
			if (httpConnection != null)
			    httpConnection.disconnect();
		}   
		return content;
	} 
	
	
	
	private String translateTime(String time) {
		String transTime = "";
		if (time.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
			transTime = translate(time);
		} else {
			transTime = translate2(time);
		}
		return transTime;
	}
	
	/** 将2014-5-7转换成2014-05-07形式 */
	private String translate(String time) {
		if ("".equals(time)) {
			return time;
		}
		StringBuilder sb = new StringBuilder("");
		String[] splits = time.split("-");
		sb.append(splits[0] + "-");
		if (splits[1].length() == 1) {
			sb.append("0");
		}
		sb.append(splits[1] + "-");
		if (splits[2].length() == 1) {
			sb.append("0");
		}
		sb.append(splits[2]);
		return sb.toString();
	}
	
	/* 将“x天前、x分钟前”转换为日期形式 */
	private String translate2(String pubtime) {
		try {
			// 遇到一种情况，有个空格转换为int后是160，通过trim()无法去掉
			pubtime = pubtime.replace((char)160, ' ').trim(); 
			
			Calendar ca = Calendar.getInstance();
			int index, value;
			if ((index = pubtime.indexOf("分钟")) != -1) {
				value = Integer.parseInt(pubtime.substring(0, index).trim()) * -1; // 提取出数字日期
				ca.add(ca.MINUTE, value);
			} else if ((index = pubtime.indexOf("小时")) != -1) {
				value = Integer.parseInt(pubtime.substring(0, index).trim()) * -1;
				ca.add(ca.HOUR, value);
			} else if ((index = pubtime.indexOf("天")) != -1) {
				value = Integer.parseInt(pubtime.substring(0, index).trim()) * -1; 
				ca.add(ca.DATE, value);
			} else if ((index = pubtime.indexOf("月")) != -1) { // 这种是"x个月前"
				value = Integer.parseInt(pubtime.substring(0, index - 1).trim()) * -1; 
				ca.add(ca.MONTH, value);
			} else if ((index = pubtime.indexOf("年")) != -1) { 
				value = Integer.parseInt(pubtime.substring(0, index).trim()) * -1; 
				ca.add(ca.YEAR, value);
			} else {
				return null;
			}
			pubtime = df.format(ca.getTime());
			return pubtime;
		} catch (Exception e) {
			System.out.println("ERROR when parse time!!!");
			return null;
		}
	}

	/**
	 * 构造Bing词组查询 ["query1" "query2"]形式
	 * @param queryWords
	 * @return
	 */
	public String translateBingQueryWords(ArrayList<String> queryWords) {
		StringBuilder sb = new StringBuilder("");
		for (String word : queryWords) {
			sb.append("\"" + word + "\" ");
		}
		String query = sb.toString().trim();
		return query;
	}
	
	public static void main(String[] args) {	
		
		// TODO Auto-generated method stub
		BingExtractor be = new BingExtractor();
		ArrayList<String> query = new ArrayList<String>();
		query.add("四川大学");
		query.add("团契");
		query.add("基督教");
		
		
		int num = 20;
		int start = 0; // start从1开始
		LinkedList<String> urls = new LinkedList<String>();
		LinkedList<String> time = new LinkedList<String>();
		
		try {
			be.extractUrls(query, num, start, urls, time, 2);
			
			System.out.println("URL size:" + urls.size() + "  , TIME size:" + time.size());
			
			for (int j = 0; j < urls.size(); j++) {
				System.out.println(urls.get(j) + "\t" + time.get(j));
			}
			
			//String outputDir = System.getProperty("user.dir") + "/urls";

		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}	
	}
}
