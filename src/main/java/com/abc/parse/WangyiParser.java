package com.abc.parse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网易新闻解析器
 * @author hjy
 *
 */
public class WangyiParser implements NewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(WangyiParser.class);
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final int maxCommentNum = 2000;
	
	/** Used to extract base information */
	private static final String titleRegex = "<h1 id=\"h1title\".*?>(.*?)</h1>";
	private static final String titleRegex2 = "<title>(.*?)</title>"; // 其实只用这个就可以
	private static final String pubtimeRegex2 = "class=\"ep-info cDGray\"><.*?>(\\d{4}-\\d{2}-\\d{2}\\s*?\\d{2}:\\d{2}):"; // 老版
	private static final String pubtimeRegex = "class=\"ep-time-soure cDGray\">\\s*(\\d{4}-\\d{2}-\\d{2}\\s*?\\d{2}:\\d{2})"; // 新版（14-11-24）
	private static final String keywordsRegex = "<meta name=\"keywords\" content=\"(.*?)\"";
	private static final String sourceRegex = "来源: <a.*?>(.*?)</a>"; // 新版（14-11-24）
	private static final String sourceRegex2 = "本文来源：(.*?)\\s?<";
	private static final String sourceRegex3 = "class=\"ep-info cDGray\"><.*?>.*?<.*?>(.*?)<"; // 老版
	
	private static Pattern pTitle;
	private static Pattern pTitle2;
	private static Pattern pPubtime;
	private static Pattern pPubtime2;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pSource2;
	private static Pattern pSource3;
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pTitle2 = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pPubtime2 = Pattern.compile(pubtimeRegex2, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
		pSource3 = Pattern.compile(sourceRegex3, Pattern.CASE_INSENSITIVE);
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
		
		// % 暂不提取评论，二期来做
		info.setComment("");
		
		return info;
	}
	
	/**
	 * 提取基本信息
	 * 网易新闻的板块比较杂乱，社会板块下有国际的、也有国内的。而且页面源码内没有任何标识。还有的不属于任何一类，页面中只是标识
	 * “滚动新闻”或“热点新闻”
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
			title = matcher.group(1).trim();
		} else {
			matcher = pTitle2.matcher(content);
			if (matcher.find()) {
				title = matcher.group(1);
	            int index = title.indexOf("_");
	            if (index != -1) title = title.substring(0, index).trim();  
			}
		}
				
		/* 提取发布时间信息 */
		matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1);
			pubtime = pubtime.replaceAll("[-:]", "");
		} else {
			matcher = pPubtime2.matcher(content);
			if (matcher.find()) {
				pubtime = matcher.group(1);
				pubtime = pubtime.replaceAll("[-:]", "");
			}
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
				source = matcher.group(1).replace((char)12288, ' ').trim();;
			} else {
				matcher = pSource3.matcher(content);
				if (matcher.find()) { 
					source = matcher.group(1);
				}
			}
		}

		info.setBaseInfo("网易", plate, title, pubtime, keywords, source);
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
