package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.NewsInfo;

/**
 * 中国经济网新闻解析
 * 
 * @author hjy
 */
public class CeParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(CeParser.class);

	/** Used to extract base information */
	private static final String pubtimeRegex = "<date>(.*?)</date>";
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "<source>(.*?)</source>";
	private static final String sourceRegex2 = "来源：(.*?)\\s?<";

	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pSource2;

	protected static String site = "中国经济网";

	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
		
	}

	@Override
	protected String extractSource(String content,  Pattern sourcePattern) {
		Matcher matcher = sourcePattern.matcher(content);
		String source = "";
		if (matcher.find()) {
			source = matcher.group(1).trim();
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) {
				source = matcher.group(1).trim();
			}
		}
		return source;
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

}
