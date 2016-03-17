package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	protected void extractSource(String content, String source, Pattern sourcePattern) {
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) {
			source = matcher.group(1).trim();
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) {
				source = matcher.group(1).trim();
			}
		}
	}


}
