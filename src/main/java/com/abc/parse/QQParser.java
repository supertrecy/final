package com.abc.parse;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.entity.NewsInfo;

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
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
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
	protected void getBaseInfo(NewsInfo info, String url, String content) {
		String title = this.extractTitle(content, pTitle);
		String pubtime = this.extractPubTime(content, pPubtime);
		String keywords = this.extractKeywords(content, pKeywords);
		String source = this.extractSource(content, pSource);
		info.setBaseInfo(site, title, pubtime, keywords, source);
	}

	@Override
	protected String extractPubTime(String content, Pattern pPubtime) {
		String pubtime = "";
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1);
			pubtime = pubtime.replaceAll("[-:]", "");
			if (pubtime.indexOf("年") != -1) // 一些图片新闻是2014年05月06日 09:28形式
				pubtime = pubtime.replace("年", "").replace("月", "").replace("日", " ").replace(":", " ");
		}
		return pubtime;
	}

	@Override
	protected String extractTitle(String content, Pattern pTitle) {
		String title = "";
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
		return title;
	}

	@Override
	protected String extractSource(String content, Pattern sourcePattern) {
		String source = "";
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) { 
			source = matcher.group(1);
			originalSourceText = matcher.group(0).trim();
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) { 
				source = matcher.group(1);
				originalSourceText = matcher.group(0).trim();
			}else{
				originalSourceText = "";
			}
		}
		return source;
	}

	@Override
	protected String extractKeywords(String content, Pattern pKeywords) {
		String keywords = "";
		Matcher matcher = pKeywords.matcher(content);
		if (matcher.find()) { 
			keywords = matcher.group(1); 
		}
		return keywords;
	}
	
}
