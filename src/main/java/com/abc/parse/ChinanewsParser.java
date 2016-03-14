package com.abc.parse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中新网新闻解析
 * @author hjy
 */
public class ChinanewsParser implements NewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(ChinanewsParser.class);
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final int maxCommentNum = 2000;
	
	/** Used to extract base information */
	private static final String titleRegex = "<title>(.*?)</title>"; // 标题干净
	private static final String pubtimeRegex = "<div class=\"left-time\">\\s*<.*?>\\s*(\\d{4}年\\d{2}月\\d{2}日\\s*?\\d{2}:\\d{2})"; 
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "来源：(?:<a.*?>)?([^<\"]*?)[<）]";
	private static final String sourceRegex2 = "来源：</span>(.*?)<";
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pSource2;
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
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
		
		String curTime = df.format(System.currentTimeMillis());
		info.setFetchtime(curTime);
		getBaseInfo(info, url, contentStr);

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
            index = title.indexOf("――");
            if (index != -1) {
            	title = title.substring(0, index);      	
            }
		}
				
		/* 提取发布时间信息 */	
		matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014年05月08日 15:20:51
			pubtime = pubtime.replace("年", "").replace("月", "").replace("日", "").replace(":", "");
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
			source = source.replace((char)12288, ' ').trim(); // 特殊空白符
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) { 
				source = matcher.group(1).trim();
			}
		}
		source = source.replace("]", "").replace(">", "");
		
		info.setBaseInfo("中新网", plate, title, pubtime, keywords, source);
	}
	
	/**
	 * 提取评论信息
	 * @param content
	 * @return
	 */
	private StringBuffer getComment(NewsInfo info, String content, String newsIdStr) {
		StringBuffer result = new StringBuffer("");  
		return result;
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
