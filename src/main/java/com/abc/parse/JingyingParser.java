package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.entity.NewsInfo;

/**
 * 中国经营网新闻解析
 * 注意：该网站的url如果不设置User-Agent会被拒绝
 * @author hjy
 */
public class JingyingParser extends SpecialNewsParser{
	public static final Logger LOG = LoggerFactory.getLogger(JingyingParser.class);
	
	/** Used to extract base information */
	private static final String pubtimeRegex = "<span>(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}):\\d{2}</span>"; 
	private static final String pubtimeRegex2 = "<span id=\'cbdatetime\'>(.*?)</span>"; // 四川地方频道
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "来源：<a.*?>(.*?)</a>";
	private static final String sourceRegex2 = "来源:(.*?)&nbsp"; // 四川地方频道
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pPubtime2;
	private static Pattern pSource2;
	
	protected static String site="中国经营网";
	
	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pPubtime2 = Pattern.compile(pubtimeRegex2, Pattern.CASE_INSENSITIVE);
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
		String plate = "";
		info.setBaseInfo(site, plate, title, pubtime, keywords, source);
	}

	@Override
	protected String extractPubTime(String content, Pattern pPubtime) {
		String pubtime = "";
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014-05-08 06:52
			pubtime = pubtime.replaceAll("-", "").replace(":", "");
		} else {
			matcher = pPubtime2.matcher(content);
			if (matcher.find()) {
				pubtime = matcher.group(1).trim(); // 2014-05-08 06:52
				pubtime = pubtime.replaceAll("-", "").replace(":", "");
			}
		}
		return pubtime;
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
            } else {
            	index = title.indexOf("_");
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
			source = matcher.group(1).trim();
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find())
				source = matcher.group(1).trim();
		}
		return source;
	}

	
}
