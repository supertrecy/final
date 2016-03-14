package com.abc.parse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 新华网新闻解析
 * @author hjy
 */
public class XinhuaParser implements NewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(XinhuaParser.class);
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final int maxCommentNum = 2000;
	
	/** Used to extract base information */
	private static final String titleRegex = "<title>(.*?)</title>"; // 标题干净
	private static final String titleRegex2 = "<td height=\"39\" align=\"center\" valign=\"bottom\" class=\"bt\">(.*?)</td>";
	private static final String pubtimeRegex = "<span id=\"pubtime\">(.*?)</span>";
	private static final String pubtimeRegex2 = "\\d{4}-\\d{2}/\\d{2}"; 
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "<span id=\"source\">\\s*来源：(.*?)</span>";
	private static final String sourceRegex2 = "来源：\\s*(?:<a.*?>)?(.*?)</"; // 地方频道
	
	private static Pattern pTitle;
	private static Pattern pTitle2;
	private static Pattern pPubtime;
	private static Pattern pPubtime2;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pSource2;
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pTitle2 = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pPubtime2 = Pattern.compile(pubtimeRegex2, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
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
		getBaseInfo(content, info, url, contentStr);
		
		info.setComment(""); // % 暂不提取评论，二期做
		
		return info;
	}
	
	/**
	 * 提取基本信息
	 * @param info
	 * @param content
	 */
	private void getBaseInfo(String con, NewsInfo info, String url, String content) {
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
            index = title.indexOf("_");
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
		// 如果是新华网的地方联播，由于标题都是”:: 新华网 :: - 地方联播“形式，以目前的本地搜索标题mapping的方式提取发布时间
		// 结果是错误的，因此需要单独解析，并使用解析后的时间
		if (url.startsWith("http://www.xinhuanet.com/chinanews/")) {
			matcher = pTitle2.matcher(content);
			if (matcher.find()) {
				title = matcher.group(1).trim();
			}
			
			matcher = pPubtime2.matcher(url);
			if (matcher.find()) {
				pubtime = matcher.group().trim();
				pubtime = pubtime.replace("/", "-");
			}
		}
		
		
		/* 提取关键词信息 */		
		matcher = pKeywords.matcher(content);
		if (matcher.find()) { 
			keywords = matcher.group(1).replace("\n", "");
		}
		
		/* 提取新闻来源信息 */	
		matcher = pSource.matcher(content);
		if (matcher.find()) { 
			source = matcher.group(1).trim();
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) {
				source = matcher.group(1).trim();
			}
		}
		source = source.replaceAll("&nbsp;", "").trim();
		
		info.setBaseInfo("新华网", plate, title, pubtime, keywords, source);
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
