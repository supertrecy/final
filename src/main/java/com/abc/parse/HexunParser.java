package com.abc.parse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 和讯网新闻解析
 * @author hjy
 */
public class HexunParser implements NewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(HexunParser.class);
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final int maxCommentNum = 2000;
	
	/** Used to extract base information */
	private static final String titleRegex = "<title>(.*?)</title>"; // 标题干净
	private static final String pubtimeRegex = "<span class=\"gray\".*?>(.*?)</span>"; 
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "来源：(?:</span>)?<a.*?>(.*?)</a>";
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	
	private static HashMap<String, String> channelMap = new HashMap<String, String>();
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
	}

	public NewsInfo getParse(String content, String encoding,String url) {
		NewsInfo info = new NewsInfo();
		String contentStr = "";
		try {
			contentStr = new String(content.getBytes(), encoding);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
		
		info.setUrl(url);
		
		/* 去掉refetchnum相关判定 */
//		int refetchNum;
//		String refetchNumStr = content.getMetadata().get(Nutch.URL_FETCH_NUM);
//		if (refetchNumStr == null) { // 若未设置，当做第一次抓取
//			refetchNum = 0;
//		} else {
//			refetchNum = Integer.parseInt(refetchNumStr);
//		}	
//		refetchNum++;
//		content.getMetadata().set(Nutch.URL_FETCH_NUM, Integer.toString(refetchNum)); // 更新抓取次数
//		
//		String curTime = df.format(System.currentTimeMillis());
//		if (refetchNum == 1) {
//			getBaseInfo(info, url, contentStr);
//			info.setFetchtime(curTime);
//		} else {
//			info.setUpdatetime(curTime);
//		}
		String curTime = df.format(System.currentTimeMillis());
		info.setFetchtime(curTime);
		getBaseInfo(info, url, contentStr);

//		StringBuffer comment = getComment(info, content, newsId);
//		if (comment == null) {
//			LOG.error("Can not extract the news comments. " + url);
//			info.setComment("");
//		} else {
//			info.setComment(comment.toString());
//		}
		info.setComment(""); // % 暂不提取评论，二期做
		
		return info;
	}
	
	/**
	 * 提取基本信息
	 * @param info
	 * @param content
	 */
	private void getBaseInfo(NewsInfo info, String url, String content) {
		String title = "";
		String pubtime = "";
		String keywords = "";
        String source = "";
        String plate = "";
        
		/* 提取标题信息 */
        Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1).trim();
            int index = title.indexOf("-");
            if (index != -1) {
            	title = title.substring(0, index);      	
            }
		}
				
		/* 提取发布时间信息 */
		matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014年05月08日15:37
			pubtime = pubtime.replace("年", "").replace("月", "").replace("日", " ").replace(":", "");
			pubtime = pubtime.replaceAll("-", "");
			if (pubtime.length() == 15) {
				pubtime = pubtime.substring(0, 13);
			}
		}
		
		/* 提取关键词信息 */		
		matcher = pKeywords.matcher(content);
		if (matcher.find()) { 
			keywords = matcher.group(1).replace("\n", "");
		} else {
			keywords = "";
		}
		
		/* 提取新闻来源信息 */	
		matcher = pSource.matcher(content);
		if (matcher.find()) { 
			source = matcher.group(1);
		} 
		
		info.setBaseInfo("和讯", plate, title, pubtime, keywords, source);
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
	
	public static String extractInfo2(String content, String regex) {  
		if (content == null)   
			return null;   
		Pattern pattern = Pattern.compile(regex); 
		Matcher m = pattern.matcher(content);   
		if (!m.find()) {    
			return null;   
		}    
		return m.group(2);  
		
	} 
	
}
