package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.entity.NewsInfo;

/**
 * 网易新闻解析器
 * @author hjy
 *
 */
public class WangyiParser extends SpecialNewsParser{
	public static final Logger LOG = LoggerFactory.getLogger(WangyiParser.class);
	
	/** Used to extract base information */
	private static final String titleRegex2 = "<h1 id=\"h1title\".*?>(.*?)</h1>";
	private static final String pubtimeRegex2 = "class=\"ep-info cDGray\"><.*?>(\\d{4}-\\d{2}-\\d{2}\\s*?\\d{2}:\\d{2}):"; // 老版
	private static final String pubtimeRegex = "class=\"ep-time-soure cDGray\">\\s*(\\d{4}-\\d{2}-\\d{2}\\s*?\\d{2}:\\d{2})"; // 新版（14-11-24）
	private static final String keywordsRegex = "<meta name=\"keywords\" content=\"(.*?)\"";
	private static final String sourceRegex = "来源: <a.*?>(.*?)</a>"; // 新版（14-11-24）
	private static final String sourceRegex2 = "本文来源：(.*?)\\s?<";
	private static final String sourceRegex3 = "class=\"ep-info cDGray\"><.*?>.*?<.*?>(.*?)<"; // 老版
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pTitle2;
	private static Pattern pPubtime2;
	private static Pattern pSource2;
	private static Pattern pSource3;
	
	protected static String site="网易";
	
	static {
		pTitle = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE);
		pTitle2 = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pPubtime2 = Pattern.compile(pubtimeRegex2, Pattern.CASE_INSENSITIVE);
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
	protected String extractPubTime(String content, Pattern pPubtime) {
		String pubtime = "";
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1);
			pubtime = pubtime.replaceAll("[-:]", "");
		} else {
			matcher = pPubtime2.matcher(content);
			if (matcher.find()) {
				pubtime = matcher.group(1);
				pubtime = pubtime.replaceAll("[-:]", "");
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
				title = matcher.group(1);
	            int index = title.indexOf("_");
	            if (index != -1) title = title.substring(0, index).trim();  
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
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) { 
				source = matcher.group(1).replace((char)12288, ' ').trim();;
			} else {
				matcher = pSource3.matcher(content);
				if (matcher.find()) { 
					source = matcher.group(1);
				}
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
