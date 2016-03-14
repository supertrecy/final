package com.abc.parse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 搜狐新闻解析器
 * @author hjy
 *
 */
public class SohuParser implements NewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(SohuParser.class);
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final int maxCommentNum = 2000;
	
	/** Used to extract base information */
	private static final String plateRegex = "<div class=\"navigation\".*?><a.*?>.*?</a>.*?<a.*?>(.*?)</a>";
	private static final String titleRegex = "<h1 itemprop=\"headline\">(.*?)</h1>";
	private static final String titleRegex2 = "<title>(.*?)</title>";
	private static final String pubtimeRegex = "<div\\sclass=\"time\".*?>(.*?)<";
	private static final String keywordsRegex = "<meta name=\"keywords\" content=\"(.*?)\"";
	private static final String sourceRegex = "来源：<span id=\"media_span\".*?>(?:<.*?>)?(.*?)<";
	
	private static Pattern pPlate;
	private static Pattern pTitle;
	private static Pattern pTitle2;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	
	static {
		pPlate = Pattern.compile(plateRegex, Pattern.CASE_INSENSITIVE);
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pTitle2 = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
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
		
//		int refetchNum;
//		String refetchNumStr = content.getMetadata().get(Nutch.URL_FETCH_NUM);
//		if (refetchNumStr == null) { // 若未设置，当做第一次抓取
//			refetchNum = 0;
//		} else {
//			refetchNum = Integer.parseInt(refetchNumStr);
//		}	
//		refetchNum++;
//		content.getMetadata().set(Nutch.URL_FETCH_NUM, Integer.toString(refetchNum)); // 更新抓取次数
//		String curTime = df.format(System.currentTimeMillis());
//		if (refetchNum == 1) { // 假如该新闻是第一次抓取
//			getBaseInfo(info, url, contentStr);
//			info.setFetchtime(curTime);
//		} else {
//			info.setUpdatetime(curTime);
//		}
		String curTime = df.format(System.currentTimeMillis());
		info.setFetchtime(curTime);
		getBaseInfo(info, url, contentStr);
		
//		StringBuffer comment = getComment(info, content, url);
//		if (comment == null) {
//			LOG.error("Can not extract the news comments. " + url);
//			info.setComment("");
//		} else {
//			info.setComment(comment.toString());
//		}
		// % 暂时不用评论提取，后期来做
		info.setComment(""); 
		
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
		
        /* 提取板块信息 */
        // 这里是二级标题：”新闻中心“作为一级标题，下面是”社会新闻“、”国内新闻“等几个二级标题。”体育“、”财经“等都是一级标题，和
        // ”新闻中心“对应，和其它网站不太一样
//		Matcher matcher = pPlate.matcher(content);
//		if (matcher.find()) {
//			plate = matcher.group(1);
//		}
        
		/* 提取标题信息 */
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1).trim();
		} else {
			matcher = pTitle2.matcher(content);
			if (matcher.find()) {
				title = matcher.group(1).trim();			
				int index = title.indexOf("-");
		        if (index != -1)
		            title = title.substring(0, index).trim();      	
			}
		}
				
		/* 提取发布时间信息 */
		matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1);
			if (pubtime.length() > 11) {
				pubtime = pubtime.substring(0,4) + pubtime.substring(5,7) + 
						pubtime.substring(8,10) + " " + pubtime.substring(11).replace(":", ""); 
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
		}

		info.setBaseInfo("搜狐网", plate, title, pubtime, keywords, source);
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
