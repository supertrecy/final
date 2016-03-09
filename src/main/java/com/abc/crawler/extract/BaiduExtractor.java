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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 参数与对应搜索格式
 * 1：网页搜索								
 * 2：新闻搜索
 * 
 * 说明：
 * 1. 这里百度网页搜索结果页面风格和平时不太一样，因为后面有“&tn=baidulocal”参数,但返回内容是一致的。
 * 2. 百度新闻搜索存在着聚合现象，即相同新闻会聚合在一起。
 * 
 * @author hjy
 *
 */
public class BaiduExtractor {
	
	public static final Log LOG = LogFactory.getLog(BaiduExtractor.class);
	private static Pattern pDate = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}", Pattern.CASE_INSENSITIVE); // 抽取网页时间（本地搜索、新闻搜索）
	private static Pattern pDate2 = Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日", Pattern.CASE_INSENSITIVE); // 抽取网页时间（普通网页搜索）
	private static Pattern pDate3 = Pattern.compile("\\d+(天|小时|分钟)前", Pattern.CASE_INSENSITIVE); // 抽取网页时间（百度新闻搜索）
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	private static final int period = 7; // 针对新闻搜索设置period天以内的新闻
	private static final int pageNum = 50; // 每页返回的结果数
	
	public void extractUrls(ArrayList<String> queryWords, int num, int startIndex, LinkedList<String> urls,
			LinkedList<String> time, int searchType) throws Exception {
		String query = translateBaiduQueryWords(queryWords);
		extractUrls(query, num, startIndex, urls, time, searchType);
	}
	
	/**
	 * 提取搜索结果页面的url
	 * @param key：查询词组，query1 query2 ……
	 * @param num：提取搜索结果数
	 * @param startIndex：从第几条开始提取
	 * @param urls：存储提取的外链
	 * @param time：存储外链对应的发布时间
	 * @param searchType：搜索类型 （1：网页搜索	 2：新闻搜索）
	 * @throws Exception
	 */
	public void extractUrls(String key, int num, int startIndex, LinkedList<String> urls,
			LinkedList<String> time, int searchType) throws Exception {
		if (startIndex < 0)
			startIndex = 0;
		if (key == null || "".equals(key)) {
			return;
		}
		
		if (searchType == 1) {
			extract_webpage_expe(key, num, startIndex, urls, time);
		} else if (searchType == 2) {
			extract_news(key, num, startIndex, urls, time);
		}
	}
	
	
	
	/**
	 * 百度网页搜索。采用普通搜索提取<标题，日期>信息，采用本地搜索提取<url,标题，日期>信息，
	 * 然后通过标题进行日期的匹配。
	 * 
	 * @param key
	 * @param num
	 * @param startIndex
	 * @param urls
	 * @param time
	 * @throws Exception
	 */
	public void extract_webpage_expe(String key, int num, int start, LinkedList<String> urls, LinkedList<String> time) throws Exception {	
		String encodeQuery = URLEncoder.encode(key.trim(), "UTF-8"); // 编码查询词
	
		int n = num / pageNum; // 页数
		int i;
		int c = start;
		
		for (i = 0; i < n; ++i , c += pageNum) {
			URL url = new URL("http://www.baidu.com/s?ie=utf-8&wd="+ encodeQuery + "&ct=2097152&pn="+c
					+"&rn=50&gpc=stf%3D1441614746%2C1441701146|stftype%3D1");
			
			LOG.info("百度网页搜索:" + url);
			System.out.println("百度网页搜索:" + url);
			Element body = Jsoup.parse(url, 30000);
			Elements es = body.getElementsByClass("result");
			Matcher m = null;
			for (Element e : es) {		
				Element e_title = e.getElementsByClass("t").first(); // 标题、加密url
				String link = e_title.child(0).attr("href");

				String title = e_title.child(0).text();
				title = title.trim();

				
				// 无法提取“百度知道”搜索结果的“最新回答”时间
				String timecon = "";
				String pubtime = "";
				Elements isbbs = e.getElementsByClass("bbs");	
				if (!isbbs.isEmpty()) { // 论坛

					Element e_bbsf13 = isbbs.first();
					timecon = e_bbsf13.ownText();
				} else {
					Elements isblog = e.getElementsByClass("blog"); // 只有部分博客提供了时间
					if (!isblog.isEmpty()) { // 博客
						Element e_blogf13 = isblog.first();
						timecon = e_blogf13.ownText();
					} else { // 其它
						Element e_con = e.getElementsByClass("c-abstract").first();
						try {
							timecon = e_con.getElementsByTag("span").text();

						} catch (Exception ep) {
							timecon = "";
						}	
					}
				}
				
				m = pDate2.matcher(timecon);
				if (m.find()) {
					pubtime = m.group(); // 只提取日期部分
					pubtime = pubtime.replace("年", "-").replace("月", "-").replace("日", "");
					pubtime = translate(pubtime);
				} else {
					m = pDate3.matcher(timecon);
					if (m.find()) {
						pubtime = m.group().trim(); // 只提取日期部分
						pubtime = translateTime(pubtime);
					}
				}
				
				urls.add(link);
				time.add(pubtime);
				
				LOG.info(link + "---" + pubtime);
				LOG.info(title);
				System.out.println(link + "---" + pubtime);
				
			}
			if (!body.outerHtml().contains("下一页")) { // 页面中不包含“下一页”表明已经到了最后一页
				break;
			}
		}
		
	}
	
	/** 百度新闻搜索 (7天以内的新闻)*/
	public void extract_news(String key, int num, int startIndex, LinkedList<String> urls,
			LinkedList<String> time) throws Exception {	
		String encodeQuery = URLEncoder.encode(key.trim(), "UTF-8"); // 编码查询词
	
		int n = num / pageNum; // 页数
		int remainder = num % pageNum;
		int i;
		int c = startIndex;
		
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
		
		for (i = 0; i < n; ++i, c += pageNum) {
			boolean done = extractNewsURLs(createBaiduNewsSearchURL(encodeQuery, c, pageNum, startTime, endTime), urls, time);
			if (done)
				break; 
		}
		if (remainder > 0)
			extractNewsURLs(createBaiduNewsSearchURL(encodeQuery, c, remainder, startTime, endTime), urls, time);
	}
	
	
	/** 解析百度新闻搜索结果页面 */
	private boolean extractNewsURLs(String url, LinkedList<String> urls, LinkedList<String> time) {
		boolean done = false;
		LOG.info("百度新闻搜索:"+url);
		System.out.println("百度新闻搜索:"+url);
		try {
			Element body = Jsoup.parse(new URL(url), 30000);
			if (!body.outerHtml().contains("下一页")) { // 页面中不包含“下一页”表明已经到了最后一页
				done = true;
			}
			
			Elements es = body.getElementsByClass("result");

			Matcher m = null;
			for (Element e : es) {
				Element e_title = e.getElementsByClass("c-title").first(); // 标题、url
				String link = e_title.child(0).attr("href");
				String title = e_title.text();
				Element e_time = e.getElementsByClass("c-author").first(); // 站点名、时间(2008-05-29 10:46:00)格式
				String text = e_time.ownText().trim();
				String pubtime = "";
				
				m = pDate2.matcher(text); // 2015年03月05日 20:00格式
				if (m.find()) {
					pubtime = m.group().trim(); // 只提取日期部分
					pubtime = pubtime.replace("年", "-").replace("月", "-").replace("日", "");
					pubtime = translateTime(pubtime);
				} else { // X小时前或X分钟前
					m = pDate3.matcher(text);
					if (m.find()) {
						pubtime = m.group().trim(); // 只提取日期部分
						pubtime = translateTime(pubtime);
					}
				}
				urls.add(link);
				time.add(pubtime);
				LOG.info(link+"----"+pubtime);
				LOG.info(title);
				System.out.println(link+"----"+pubtime);
				System.out.println(title);
			}
		} catch (MalformedURLException e) {
			LOG.fatal("Extract baidu search result error, " + e.getMessage());
		} catch (IOException e) {
			LOG.fatal("IOException Extract baidu search result error, " + e.getMessage());
		}
		return done;
	}
	
	/**
	 * 构造适合百度的词组查询  ["query1 query2"]形式，如果是单个词则为"query"形式
	 * @param queryWords
	 * @return
	 */
	public String translateBaiduQueryWords(ArrayList<String> queryWords) {
		StringBuilder sb = new StringBuilder("\"");
		for (String word : queryWords) {
			sb.append(word + " ");
		}
		String query = sb.toString().trim() + "\"";
		return query;
	}
    
    /**
     * 将提取的url和pubtime写入文件
     * @param urls
     * @param path
     */
	public void writeRootUrls(List<String> urls, List<String> time, String outputDir) {
		String name = "baidu-" + System.currentTimeMillis() + ".txt";
		File file = new File(outputDir + "/" + name);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter writer = new FileWriter(file, false);
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
	
	private String translateTime(String time) {
		String transTime = "";
		if (time.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
			transTime = translate(time);
		} else {
			transTime = translate2(time);
		}
		return transTime;
	}
	
	/**
	 * 时间格式转换处理：将2014-5-7转换成2014-05-07形式
	 * @param time
	 * @return
	 */
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
			pubtime = sf.format(ca.getTime());
			return pubtime;
		} catch (Exception e) {
			System.out.println("ERROR when parse time!!!");
			return null;
		}
	}
	
	
	private String createBaiduNewsSearchURL(String sQuery, int start, int pageNum, long startTime,
			long endTime) {
		List<NameValuePair> qparams =  new ArrayList<NameValuePair>();
		
		qparams.add(new NameValuePair("pn", String.valueOf(start)));                  
		qparams.add(new NameValuePair("rn", String.valueOf(pageNum)));
		if (startTime == 0 && endTime == 0) { // 表示不限制时间
			
		} else {
			qparams.add(new NameValuePair("bt", String.valueOf(startTime)));                  
			qparams.add(new NameValuePair("et", String.valueOf(endTime)));
		}
		qparams.add(new NameValuePair("cl", "2"));
		
		HttpURL httpURL = null;
		try {
			httpURL = new HttpURL("news.baidu.com", -1, "/ns", EncodingUtil.formUrlEncode(qparams.toArray(new NameValuePair[qparams.size()]), "utf-8"));
			String url = httpURL.getURI().toString() + "&q1=" + sQuery;
			return url;
		} catch (URIException e) {
			LOG.fatal("Get baidu url failed, " + e.getMessage());
		}
		return "";
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
	public static void main(String[] args) throws Exception {
		BaiduExtractor be = new BaiduExtractor();
		
		LinkedList<String> urls = new LinkedList<String>();
		LinkedList<String> time = new LinkedList<String>();
		be.extract_news("\"四川大学\"", 500, 0, urls, time);
		
	}	
	
}
