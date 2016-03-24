package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.entity.NewsInfo;

/**
 * 四川新闻网新闻解析
 * @author hjy
 */
public class SiChuanNewsParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(Shiji21Parser.class);
	
	/** Used to extract base information */
	private static final String titleRegex2 = "<title>(.*?\\s*?(_|-)?\\s*?.*?)</title>"; // 标题干净
	private static final String pubtimeRegex = "【\\s*(\\d{4}-\\d{2}-\\d{2}\\s*?\\d{2}:\\d{2})\\s*】"; 
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\\s*\"\\s*";
	private static final String sourceRegex = "来源：(?:<a.*?>)?(.*?)(?:<.*?>)?\\s*】";
	private static final String sourceRegex2 = "来源[：:]([^<>\\s]+)\\s"; 
	private static final String sourceRegex3 = "来源：</span><font.*?><span.*?><a.*?>(.*?)</a>"; 
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pSource2;
	private static Pattern pSource3;
	
	protected static String site="四川新闻网";
	
	static {
		pTitle = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
		pSource3 = Pattern.compile(sourceRegex3, Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	protected void getBaseInfo(NewsInfo info, String url, String content) {
		String title = this.extractTitle(content, pTitle);
		String pubtime = this.extractPubTime(content, pPubtime);
		String keywords = this.extractKeywords(content, pKeywords);
		String source = this.extractSource(content, pSource);
		info.setBaseInfo(site, title, pubtime, keywords, source);
	}


	@Override
	protected String extractTitle(String content, Pattern pTitle) {
		String title = "";
		 Matcher matcher = pTitle.matcher(content);
			if (matcher.find()) {
				title = matcher.group(1);
	            int index = title.indexOf("_");
	            if (index != -1) {
	            	title = title.substring(0, index).trim();      	
	            } else {
	            	index = title.indexOf("-");
	                if (index != -1) {
	                	title = title.substring(0, index).trim();      	
	                }
	            }
			}
		return title;
	}

	@Override
	protected String extractSource(String content, Pattern sourcePattern) {
		String source = "";
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) { 
			source = matcher.group(1).trim();
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) { 
				source = matcher.group(1).trim();
			} else {
				matcher = pSource3.matcher(content);
				if (matcher.find()) {
					source = matcher.group(1).trim();
				}
			}
		}
		return source;
	}
}

