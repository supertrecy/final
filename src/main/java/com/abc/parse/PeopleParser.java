package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.entity.NewsInfo;

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
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pSource2;
	private static Pattern pSource3;
	
	protected String site="人民网";
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);		
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
			pubtime = matcher.group(1).trim(); // 2014年05月08日13:14
			pubtime = pubtime.replace("年", "").replace("月", "").replace("日", " ").replace(":", "");
		}
		return pubtime;
	}

	@Override
	protected String extractTitle(String content, Pattern pTitle) {
		String title = "";
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
		source = source.replace("原创稿", "").trim();
		int index = source.indexOf("作者：");
		if (index != -1) {
			source = source.substring(0, index);
		}
		return source;
	}

}
