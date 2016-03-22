package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.NewsInfo;

/**
 * 搜狐新闻解析器
 * @author hjy
 *
 */
public class SohuParser extends SpecialNewsParser{
	public static final Logger LOG = LoggerFactory.getLogger(SohuParser.class);
	
	/** Used to extract base information */
	private static final String titleRegex2 = "<h1 itemprop=\"headline\">(.*?)</h1>";
	private static final String pubtimeRegex = "<div\\sclass=\"time\".*?>(.*?)<";
	private static final String keywordsRegex = "<meta name=\"keywords\" content=\"(.*?)\"";
	private static final String sourceRegex = "来源：<span id=\"media_span\".*?>(?:<.*?>)?(.*?)<";
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pTitle2;
	
	protected static String site="搜狐网";
	
	static {
		pTitle = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE);
		pTitle2 = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
	}

	@Override
	protected void getBaseInfo(NewsInfo info, String url, String content) {
		String title = this.extractTitle(content, pTitle);
		String pubtime = this.extractPubTime(content, pPubtime);
		String keywords = this.extractKeywords(content, pKeywords);
		String source = this.extractSource(content, pSource);
		String plate = "";
		info.setBaseInfo(site, plate, title, pubtime, keywords, source);
	}

	@Override
	protected String extractPubTime(String content, Pattern pPubtime) {
		String pubtime = "";
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1);
			if (pubtime.length() > 11) {
				pubtime = pubtime.substring(0,4) + pubtime.substring(5,7) + 
						pubtime.substring(8,10) + " " + pubtime.substring(11).replace(":", ""); 
			}	
		}
		return pubtime;
	}

	@Override
	protected String extractTitle(String content, Pattern pTitle) {
		String title = "";
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1).trim();
		} else {
			matcher = pTitle2.matcher(content);
			if (matcher.find()) {
				title = matcher.group(1).trim();			
				int index = title.indexOf("-");
		        if (index != -1)
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
			keywords = matcher.group(1); 
		}	
		return keywords;
	}
	
}
