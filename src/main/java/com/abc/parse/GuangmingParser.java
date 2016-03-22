package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.NewsInfo;

/**
 * 光明网新闻解析
 * 
 * @author hjy
 */
public class GuangmingParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(GuangmingParser.class);

	/** Used to extract base information */
	private static final String pubtimeRegex = "<span id=\"pubTime\">(.*?)</span>";
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "<span id=\"source\">来源：(?:<a.*?>)?(.*?)<";

	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	
	protected static String site = "光明网";

	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
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
	protected String extractSource(String content, Pattern sourcePattern) {
		String source = "";
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) {
			source = matcher.group(1);
			source = source.replace((char) 12288, ' ').trim(); // 特殊空白符
		}
		return source;
	}


}
