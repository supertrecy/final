package com.abc.parse;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 腾讯新闻解析器
 * @author hjy
 *
 */
public class QQParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(QQParser.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	
	/** Used to extract base information */
	private static final String titleRegex2 = "<title>.*?</title>";
	private static final String pubtimeRegex = "pubtime:'(.*?)'";
	private static final String keywordsRegex = "<meta name=\"keywords\" content=\"(.*?)\"";
	private static final String sourceRegex = "jgname\"><.*?>(.*?)<";
	private static final String sourceRegex2 = "<span class=\"where\">(.*?)</span>";
	
	private static Pattern pSource2;
	
	protected static String site="腾讯网";
	
	static {
		pTitle = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	protected void extractSource(String content, String source, Pattern sourcePattern) {
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) { 
			source = matcher.group(1);
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) { 
				source = matcher.group(1);
			}
		}
	}

	@Override
	protected void extractKeywords(String content, String keywords, Pattern pKeywords) {
		Matcher matcher = pKeywords.matcher(content);
		if (matcher.find()) { 
			keywords = matcher.group(1); 
		}
	}

	@Override
	protected void extractPubTime(String content, String pubtime, Pattern pPubtime) {
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1);
			pubtime = pubtime.replaceAll("[-:]", "");
			if (pubtime.indexOf("年") != -1) // 一些图片新闻是2014年05月06日 09:28形式
				pubtime = pubtime.replace("年", "").replace("月", "").replace("日", " ").replace(":", " ");
		}
	}

	@Override
	protected void extractTitle(String content, String title, Pattern pTitle) {
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group();
			title = title.replaceAll("<title>", "");
            title = title.replaceAll("</title>", "");
            int index = title.indexOf("_");
            if (index != -1) {
            	title = title.substring(0, index);      	
            }
		}
	}
	
	
	
}
