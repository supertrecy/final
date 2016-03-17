package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 和讯网新闻解析
 * @author hjy
 */
public class HexunParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(HexunParser.class);
	
	/** Used to extract base information */
	private static final String pubtimeRegex = "<span class=\"gray\".*?>(.*?)</span>"; 
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "来源：(?:</span>)?<a.*?>(.*?)</a>";
	
	protected static String site="和讯";
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
	}

	@Override
	protected void extractPubTime(String content, String pubtime, Pattern pPubtime) {
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014年05月08日15:37
			pubtime = pubtime.replace("年", "").replace("月", "").replace("日", " ").replace(":", "");
			pubtime = pubtime.replaceAll("-", "");
			if (pubtime.length() == 15) {
				pubtime = pubtime.substring(0, 13);
			}
		}
	}


	@Override
	protected void extractTitle(String content, String title, Pattern pTitle) {
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1).trim();
            int index = title.indexOf("-");
            if (index != -1) {
            	title = title.substring(0, index);      	
            }
		}
	}
	
	
}
