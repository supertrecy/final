package com.abc.crawler.extract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class SogouExtractor {

	public static final Log LOG = LogFactory.getLog(SogouExtractor.class);
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	public void extractUrls(ArrayList<String> queryWords, int num, LinkedList<String> urls,
			LinkedList<String> time, int searchType) {
		String query = translateSogouQueryWords(queryWords);
		extractUrls(query, num, urls, time, searchType);
	}
	
	/**
	 * 构造适合sogou的词组查询  ["query1 query2"]形式，如果是单个词则为"query"形式
	 * @param queryWords
	 * @return
	 */
	public String translateSogouQueryWords(ArrayList<String> queryWords) {
		StringBuilder sb = new StringBuilder("\"");
		for (String word : queryWords) {
			sb.append(word + " ");
		}
		String query = sb.toString().trim() + "\"";
		return query;
	}
	
	/**
	 * 提取搜索结果页面的url
	 * @param key：查询词组，query1 query2 ……
	 * @param num：提取搜索结果数
	 * @param urls：存储提取的外链
	 * @param time：存储外链对应的发布时间
	 * @param searchType：搜索类型 （1：网页搜索   2：新闻搜索  3:论坛搜索  4：博客搜索）
	 * @throws Exception
	 */
	public void extractUrls(String query, int num, LinkedList<String> urls, LinkedList<String> time,
			int searchType) {
		
		if(query == null || "".equals(query)){
			return;
		}
		if(searchType == 1){
			extract_webpage(query, num, urls, time);
		}else if(searchType == 2){
			extract_news(query, num, urls, time);
		}else if(searchType == 3){
			extract_forum(query, num, urls, time);
		}else if(searchType == 4){
			extract_blog(query, num, urls, time);
		}
	}
	/**
	 * 搜狗网页搜索
	 * @param query
	 * @param num
	 * @param urls
	 * @param time
	 */
	private void extract_webpage(String query, int num, LinkedList<String> urls, LinkedList<String> time) {

		int page = num / 10;
		if ((num % 10) != 0) page += 1;
		String charset = "UTF-8";
		try {
			String encodeQuery = URLEncoder.encode(query, charset);
			for(int i = 1;i < page;i++){
				Thread.sleep(1000);
				String address = "http://www.sogou.com/web?query="+ encodeQuery +"&page="+i+"&sourceid=inttime_day&tsn=1";//一天内
			
				LOG.info("搜狗网页搜索:"+address);
				System.out.println("搜狗网页搜索:"+address);
				String html = getContent(new URL(address), charset);
				
				Elements result = Jsoup.parse(html).getElementsByClass("results");
				
				//第一种格式
				Elements resultList = result.select(".vrwrap");
				for(Element rs : resultList){
					String link = rs.child(0).child(0).attr("href");
					String s[] = rs.select("cite").first().html().replace("&nbsp;", " ").split("- ");
					String pubtime = null;
					try{
						pubtime = s[2];
					}catch(ArrayIndexOutOfBoundsException e){
						pubtime = s[1];
					}
					if(pubtime.contains("<date>")){
						pubtime = pubtime.replace("<date>", "").replace("</date>", "").trim();
					}
					String title = rs.select(".vrTitle").text();
					
					if (pubtime != null) {
						pubtime = translateTime(pubtime) ;
						LOG.info(title);
						LOG.info(link+"----"+pubtime);
						System.out.println(title);
						System.out.println(link+"----"+pubtime);
						urls.add(link);
						time.add(pubtime);
					} else {
						LOG.warn(link+" 时间格式错误!");
					}
				}
				
				//第二种格式
				Elements resultList2 = result.select(".rb");
				for(Element rs : resultList2){
					String link = rs.select(".pt").first().child(0).attr("href");
					String pubtime = null;
					if(!link.equals("")){
						String s[] = rs.select("cite").first().html().replace("&nbsp;", " ").split("- ");
						try{
							pubtime = s[2];
						}catch(ArrayIndexOutOfBoundsException e){
							pubtime = s[1];
						}
						if(pubtime.contains("<date>")){
							pubtime = pubtime.replace("<date>", "").replace("</date>", "").trim();
						}
					}
					String title = rs.select(".vrTitle").text();
					if(!link.equals("") && pubtime != null){
						pubtime = translateTime(pubtime);
						LOG.info(title);
						LOG.info(link+"----"+pubtime);
						System.out.println(title);
						System.out.println(link+"----"+pubtime);
						urls.add(link);
						time.add(pubtime);						
					}else {
						LOG.warn(link+" 时间格式错误!");
					}
					
				}
				if (!html.contains("下一页"))
					break;
			}
		} catch (Exception e) {
			LOG.error("错误ERROR: 获取sogou网页搜索结果出错! "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 搜狗博客搜索
	 * @param query
	 * @param num
	 * @param urls
	 * @param time
	 */
	private void extract_blog(String query, int num, LinkedList<String> urls,
			LinkedList<String> time) {
		
		int page = num / 10;
		if ((num % 10) != 0) page += 1;
		String charset = "UTF-8";
		try {
			String encodeQuery = URLEncoder.encode(query, charset);
			for(int i =0;i < page;i++){
				Thread.sleep(1000);
				String address = "http://www.sogou.com/web?query="+ encodeQuery +"&page="+i
						+"&interation=196647%2C196669";
				
				LOG.info("搜狗博客搜索:"+address);
				System.out.println("搜狗博客搜索:"+address);
				String html = getContent(new URL(address), charset);
				
				Elements result = Jsoup.parse(html).getElementsByClass("results");
				//第一种格式
				Elements resultList = result.select(".vrwrap");
				for(Element rs : resultList){
					String link = rs.child(0).child(0).attr("href");
					String s[] = rs.select("cite").first().html().replace("&nbsp;", " ").split("- ");
					String pubtime = null;
					try{
						pubtime = s[2];
					}catch(ArrayIndexOutOfBoundsException e){
						pubtime = s[1];
					}
					if(pubtime.contains("<date>")){
						pubtime = pubtime.replace("<date>", "").replace("</date>", "").trim();
					}
					String title = rs.select(".vrTitle").text();
					
					if (pubtime != null) {
						pubtime = translateTime(pubtime) ;
						LOG.info(title);
						LOG.info(link+"----"+pubtime);
						System.out.println(title);
						System.out.println(link+"----"+pubtime);
						urls.add(link);
						time.add(pubtime);
					} else {
						LOG.warn(link+" 时间格式错误!");
					}
				}
				
				//第二种格式
				Elements resultList2 = result.select(".rb");
				for(Element rs : resultList2){
					String link = rs.select(".pt").first().child(0).attr("href");
					String pubtime = null;
					if(!link.equals("")){
						String s[] = rs.select("cite").first().html().replace("&nbsp;", " ").split("- ");
						try{
							pubtime = s[2];
						}catch(ArrayIndexOutOfBoundsException e){
							pubtime = s[1];
						}
						if(pubtime.contains("<date>")){
							pubtime = pubtime.replace("<date>", "").replace("</date>", "").trim();
						}
					}
					String title = rs.select(".vrTitle").text();
					if(!link.equals("") && pubtime != null){
						pubtime = translateTime(pubtime);
						LOG.info(title);
						LOG.info(link+"----"+pubtime);
						System.out.println(title);
						System.out.println(link+"----"+pubtime);
						urls.add(link);
						time.add(pubtime);						
					}else {
						LOG.warn(link+" 时间格式错误!");
					}
					
				}
				if (!html.contains("下一页"))
					break;
			}
		} catch (Exception e) {
			LOG.error("错误ERROR: 获取搜狗博客搜索结果出错! "+e.getMessage());
			e.printStackTrace();
		}
	}
    
	/**
	 * 搜狗论坛搜索 interation=196648
	 * @param query
	 * @param num
	 * @param urls
	 * @param time
	 */
	private void extract_forum(String query, int num, LinkedList<String> urls,
			LinkedList<String> time) {
		int page = num / 10;
		if ((num % 10) != 0) page += 1;
		String charset = "UTF-8";
		try {
			String encodeQuery = URLEncoder.encode(query, charset);
			for(int i =0;i < page;i++){
				Thread.sleep(1000);
				String address = "http://www.sogou.com/web?query="+ encodeQuery +"&page="+i+"&interation=196648"
						+ "&sourceid=inttime_day&tsn=1";//搜索一天内的
				LOG.info("搜狗论坛搜索:"+address);
				System.out.println("搜狗论坛搜索:"+address);
				String html = getContent(new URL(address), charset);
				
				Elements result = Jsoup.parse(html).getElementsByClass("results");
				Elements resultList = result.select(".rb");
				for(Element rs : resultList){
					
					String link = rs.child(0).child(0).attr("href");
					String[] s = rs.select("cite").first().html().replace("&nbsp;", " ").split("- ");
					String pubtime = null;
					
					try{
						pubtime = s[2];
					}catch(ArrayIndexOutOfBoundsException e){
						pubtime = s[1];
					}
					if(pubtime.contains("<date>")){
						pubtime = pubtime.replace("<date>", "").replace("</date>", "").trim();
					}
					if(pubtime != null && link != null){
						pubtime = translateTime(pubtime);
						LOG.info(link+"----"+pubtime);
						System.out.println(link+"----"+pubtime);
						urls.add(link);
						time.add(pubtime);
					} else {
						LOG.warn(link+" 时间格式错误!");
						System.out.println(link+" 时间格式错误!");
					}
				}
				if (!html.contains("下一页"))
					break;
			}
		} catch (Exception e) {
			LOG.error("错误ERROR: 获取sogou论坛搜索结果出错! "+e.getMessage());
			e.printStackTrace();
		}
	}

	private String translateTime(String pubtime) {
		String time = null;
		try{
			if(pubtime.contains("分钟前")){
				time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
			}else if(pubtime.contains("小时前")){
				Calendar ca = Calendar.getInstance();
				String s[] = pubtime.split("小时前");
				int value = Integer.parseInt(s[0].trim()) * -1;
				ca.add(ca.HOUR, value);
				time = df.format(ca.getTime());
			}else if(pubtime.contains("天前")){
				String s[] = pubtime.split("天前");
				time = new SimpleDateFormat("yyyy-MM-dd").format(new Date
						(System.currentTimeMillis()-Integer.parseInt(s[0].trim())*24*60*60*1000));
			}else if(pubtime.matches("\\d{4}-\\d{1,2}-\\d{1,2}")){
				time = translate(pubtime);
			}else if(pubtime.matches("\\d{4}年\\d{1,2}月\\d{1,2}日")){
				time = pubtime.replaceAll("年", "-").replaceAll("月", "-");
				time = time.substring(0, time.length() - 1);
				time = translate(time);
			}
		}catch(Exception e){
			LOG.warn("搜狗搜索日期格式错误！");
			e.printStackTrace();
		}
		return time;
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

	/** sogou新闻搜索 
	 * 
	 * mode为1表示查询新闻全文,mode为2表示查询新闻标题,默认为查新闻全文
	 * sort为0表示按相关性排序,sort为1表示按时间排序，默认为按相关性排序，此方法是按相关性搜索
	 * page为页数
	 * 编码方式为GBK
	 **/
	private void extract_news(String query, int num, LinkedList<String> urls, LinkedList<String> time) {
		int page = num / 10;
		if ((num % 10) != 0) page += 1;
		String charset = "GBK";
		try {
			String encodeQuery = URLEncoder.encode(query, charset);
			for(int i =0;i < page;i++){
				Thread.sleep(1000);
				String address = "http://news.sogou.com/news?query="+ encodeQuery +"&page="+i+"&sort=0";
				LOG.info("搜狗新闻搜索:"+address);
				
				String html = getContent(new URL(address), charset);
				
				Elements result = Jsoup.parse(html).getElementsByClass("results");
				Elements resultList = result.select(".rb");
				for(Element rs : resultList){
					String link = rs.select(".pp").attr("href");
					String[] s = rs.child(0).child(1).html().replace("&nbsp;", " ").split(" ");
					String pubtime = s[1];
					
					if (pubtime != null) {
						LOG.info(link+"----"+pubtime);
						urls.add(link);
						time.add(pubtime);
					} else {
						LOG.warn(link+" 时间格式错误!");
					}
				}
				if (!html.contains("下一页"))
					break;
			}
		} catch (Exception e) {
			LOG.error("错误ERROR: 获取sogou新闻搜索结果出错! "+e.getMessage());
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
				output.write(b, 0, i);
			}
			con = output.toByteArray();	
			content = new String(con, encoding);
		} catch (MalformedURLException e) {
			LOG.error(e.getMessage());  
		} catch (IOException e) {
			LOG.error("io error-----" + e.getMessage());
		} catch (RuntimeException e) {
			LOG.error(e.getMessage());
		} finally {
			try {
				if (output != null)
					output.close();
				if (in != null)
					in.close(); 
			} catch (Exception e) {
				;
			}
			if (httpConnection != null){
				httpConnection.disconnect();
			}
		}   
		return content;
	} 

	public static void main(String args[]){
		SogouExtractor se = new SogouExtractor();
		LinkedList<String> urls = new LinkedList<String>();
		LinkedList<String> time = new LinkedList<String>();
		se.extract_forum("\"云安全\"", 500, urls,time);
		
		
	}
}
