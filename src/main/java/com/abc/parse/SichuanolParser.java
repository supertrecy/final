package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 四川在线新闻解析
 * @author hjy
 */
public class SichuanolParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(SichuanolParser.class);
	
	/** Used to extract base information */
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"?(.*?)\"?\\s*/>";
	private static final String timeSource = "<div id=\"scol_time\">http://www.scol.com.cn\\((.*?)\\)&nbsp;&nbsp;<a.*?>(.*?)</a>"; // 财富
	private static final String timeSource2 = "<div id=\"scol_time\"><a.*?>http://www.scol.com.cn</a>&nbsp;&nbsp;" +
			"\\((.*?)\\)&nbsp;&nbsp;来源：(?:<a.*?>)?(.*?)(?:</a>)?&"; // 新闻
	private static final String timeSource3 = "<span id=\"pubtime_baidu\">(\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:" +
			"\\d{1,2})</span>.*?<span.*?>(?:<a.*?>)?(.*?)<";
	
	private static Pattern pTitle;
	private static Pattern pKeywords;
	private static Pattern pTimeSource;
	private static Pattern pTimeSource2;
	private static Pattern pTimeSource3;
	
	protected static String site = "四川在线";

	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);		
		pTimeSource = Pattern.compile(timeSource, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pTimeSource2 = Pattern.compile(timeSource2, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pTimeSource3 = Pattern.compile(timeSource3, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
	}

	protected void getBaseInfo(NewsInfo info, String url, String content) {
		String title = "";
		String pubtime = "";
		String keywords = "";
        String source = "";
        String plate = "";
        
				
		/* 提取发布时间和新闻来源信息 */	
		Matcher matcher = pTimeSource.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014-5-8 6:52:55格式
			pubtime = translate(pubtime);
			pubtime = pubtime.replaceAll("-", "").replace(":", "");
			
			source = matcher.group(2).trim();
		} else {
			matcher = pTimeSource2.matcher(content);
			if (matcher.find()) {
				pubtime = matcher.group(1).trim(); // 2014-5-8 6:52:55格式
				pubtime = translate(pubtime);
				pubtime = pubtime.replaceAll("-", "").replace(":", "");
				
				source = matcher.group(2).trim();
			} else {
				matcher = pTimeSource3.matcher(content);
				if (matcher.find()) {
					pubtime = matcher.group(1).trim(); // 2014-5-8 6:52:55格式
					pubtime = translate(pubtime);
					pubtime = pubtime.replaceAll("-", "").replace(":", "");
					
					source = matcher.group(2).trim();
				}
			}
		}
		source = source.replace("来源：", "");
		title = this.extractTitle(content, pTitle);
		keywords = this.extractKeywords(content, pKeywords);
		info.setBaseInfo(site, plate, title, pubtime, keywords, source);
		info.setBaseInfo(site, plate, title, pubtime, keywords, source);
	}
	
	private String translate(String pubtime) {
		StringBuilder sb = new StringBuilder("");
		int index = pubtime.lastIndexOf(":");
		pubtime = pubtime.substring(0, index);
		
		try {
			String[] split = pubtime.split(" ");
			if (split.length != 2) return "";
			
			String[] date = split[0].split("-");
			if (date.length != 3) return "";
			
			sb.append(date[0]);
			if (date[1].length() == 1) {
				sb.append("0");
			}
			sb.append(date[1]);
			
			if (date[2].length() == 1) {
				sb.append("0");
			}
			sb.append(date[2]);
			
			sb.append(" ");
			
			String[] time = split[1].split(":");
			if (time.length != 2) return "";
			
			if (time[0].length() == 1) {
				sb.append("0");
			}
			sb.append(time[0]);
			
			if (time[1].length() == 1) {
				sb.append("0");
			}
			sb.append(time[1]);

			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}


	@Override
	protected String extractTitle(String content, Pattern pTitle) {
		String title = "";
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1);
            int index = title.indexOf("-");
            if (index != -1) {
            	title = title.substring(0, index).trim();      	
            }
            index = title.indexOf("_");
            if (index != -1) {
                title = title.substring(0, index).trim();      	
            }
            index = title.indexOf("―");
            if (index != -1) {
            	title = title.substring(0, index).trim(); 
            }
		}
		return title;
	}


	@Override
	protected String extractKeywords(String content, Pattern pKeywords) {
		String keywords = "";
		Matcher matcher = pKeywords.matcher(content);
		if (matcher.find()) { 
			keywords = matcher.group(1).replace("\n", "").trim();
		} else {
			keywords = "";
		}
		return keywords;
	}
	
	
	
}
