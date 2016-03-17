package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 人民网新闻解析
 * @author hjy
 */
public class PeopleParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(PeopleParser.class);
	
	/** Used to extract base information */
	private static final String pubtimeRegex = "<span id=\"p_publishtime\">(.*?)</span>"; 
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "<meta name=\"source\" content=\"(?:来源|來源)：(.*?)\"";
	private static final String sourceRegex2 = "<meta name=\"source\" content=\"(.*?)\">";
	private static final String sourceRegex3 = "来源：(.*?)\\s";
	
	private static Pattern pSource2;
	private static Pattern pSource3;
	
	protected static String site="人民网";
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
		pSource3 = Pattern.compile(sourceRegex3, Pattern.CASE_INSENSITIVE);
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
			} else {
				matcher = pSource3.matcher(content);
				if (matcher.find()) { 
					source = matcher.group(1).trim();
				}
			}
		}
		source = source.replace("原创稿", "").trim();
		int index = source.indexOf("作者：");
		if (index != -1) {
			source = source.substring(0, index);
		}
	}

	@Override
	protected void extractPubTime(String content, String pubtime, Pattern pPubtime) {
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014年05月08日13:14
			pubtime = pubtime.replace("年", "").replace("月", "").replace("日", " ").replace(":", "");
		}
	}

	@Override
	protected void extractTitle(String content, String title, Pattern pTitle) {
		 Matcher matcher = pTitle.matcher(content);
			if (matcher.find()) {
				title = matcher.group(1).trim();
				title = title.replaceAll("&nbsp;", "");
	            int index = title.indexOf("--");
	            if (index != -1) {
	            	title = title.substring(0, index);      	
	            }
	            index = title.indexOf("――");
	            if (index != -1) {
	            	title = title.substring(0, index);      	
	            }
			}
	}
	
	
	
}
