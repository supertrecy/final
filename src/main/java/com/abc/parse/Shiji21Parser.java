package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 21世纪网新闻解析
 * @author hjy
 */
public class Shiji21Parser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(Shiji21Parser.class);
	
	/** Used to extract base information */
	private static final String pubtimeRegex = "<div class=\"articlInfo\">.*?(\\d{4}-\\d{2}-\\d{2}\\s*?\\d{2}:\\d{2}):"; 
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "<div class=\"articlInfo\">(?:<a.*?>)?(.*?)\\s*(<|\\d{4})";
	private static final String sourceRegex2 = "<div class=\"news_tit_oth\">(.*?)\\s";   
	
	private static Pattern pSource2;

	protected static String site="21世纪网";
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
	}

	@Override
	protected void extractSource(String content, String source, Pattern sourcePattern) {
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) { 
			source = matcher.group(1).trim();
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) {
				source = matcher.group(1).trim();
			}
		}
	}

	@Override
	protected void extractTitle(String content, String title, Pattern pTitle) {
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
	}
	
}
