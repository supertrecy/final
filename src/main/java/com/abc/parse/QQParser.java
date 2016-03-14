package com.abc.parse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 腾讯新闻解析器
 * @author hjy
 *
 */
public class QQParser implements NewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(QQParser.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final int maxCommentNum = 2000;
	
	/** Used to extract base information */
	private static final String titleRegex = "<title>.*?</title>";
	private static final String pubtimeRegex = "pubtime:'(.*?)'";
	private static final String keywordsRegex = "<meta name=\"keywords\" content=\"(.*?)\"";
	private static final String sourceRegex = "jgname\"><.*?>(.*?)<";
	private static final String sourceRegex2 = "<span class=\"where\">(.*?)</span>";
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pSource2;
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
	}
	
	/**
	 * 解析网页，返回一个NewsInfo对象
	 */
	public NewsInfo getParse(String content, String encoding,String url) {
		NewsInfo info = new NewsInfo();
		String contentStr = "";
		try {
			contentStr = new String(content.getBytes(), encoding);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
		info.setUrl(url);
		
		String curTime = df.format(System.currentTimeMillis());
		info.setFetchtime(curTime);
		getBaseInfo(info, url, contentStr);	
			
		info.setComment(""); // % 暂不抓取
		
		return info;
	}
	
	/**
	 * 提取基本信息
	 * @param info
	 * @param content
	 */
	private void getBaseInfo(NewsInfo info, String url, String content) {
		String plate = "";
		String title = "";
		String pubtime = "";
		String keywords = "";
        String source = "";
		
		/* 提取标题信息 */
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group();
			title = title.replaceAll("<title>", "");
            title = title.replaceAll("</title>", "");
            int index = title.indexOf("_");
            if (index != -1) {
            	title = title.substring(0, index);      	
            }
		}
				
		/* 提取发布时间信息 */
		matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1);
			pubtime = pubtime.replaceAll("[-:]", "");
			if (pubtime.indexOf("年") != -1) // 一些图片新闻是2014年05月06日 09:28形式
				pubtime = pubtime.replace("年", "").replace("月", "").replace("日", " ").replace(":", " ");
		}
		
		/* 提取关键词信息 */		
		matcher = pKeywords.matcher(content);
		if (matcher.find()) { 
			keywords = matcher.group(1); 
		}
		
		/* 提取新闻来源信息 */	
		matcher = pSource.matcher(content);
		if (matcher.find()) { 
			source = matcher.group(1);
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) { 
				source = matcher.group(1);
			}
		}

		info.setBaseInfo("腾讯网", plate, title, pubtime, keywords, source);
	}
	
	
	/**
	 * 
	 * @param content
	 * @param regex
	 * @return
	 */
	public static String extractInfo(String content, String regex) {  
		if (content == null)   
			return null;   
		Pattern pattern = Pattern.compile(regex);   
		Matcher m = pattern.matcher(content);   
		if (!m.find()) {    
			return null;   
		}           
		return m.group(1);   		
	} 
	
}
