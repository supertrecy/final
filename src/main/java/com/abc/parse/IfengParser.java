package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 凤凰网新闻解析
 * 
 * @author hjy
 */
public class IfengParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(IfengParser.class);

	/** Used to extract base information */
	private static final String titleRegex2 = "<h1 itemprop=\"headline\".*?>(.*?)</h1>"; // 标题干净
	private static final String pubtimeRegex = "<span itemprop=\"datePublished\".*?>(.*?)</span>";
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "来源：</?span>.*?<a.*?>(.*?)</a>";
	private static final String sourceRegex2 = "来源：</span><.*?><.*?>(.*?)</span>";

	private static Pattern pTitle2;
	private static Pattern pSource2;

	protected static String site = "凤凰网";

	static {
		pTitle = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pTitle2 = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
	}


	@Override
	protected void extractSource(String content, String source, Pattern sourcePattern) {
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) {
			source = matcher.group(1).trim();
		}
		if ("".equals(source)) {
			matcher = pSource2.matcher(content);
			if (matcher.find())
				source = matcher.group(1);
		}
	}

	@Override
	protected void extractPubTime(String content, String pubtime, Pattern pPubtime) {
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014年05月08日 00:44
			pubtime = pubtime.replace("年", "").replace("月", "").replace("日", "").replace(":", "");
		}
	}

	@Override
	protected void extractTitle(String content, String title, Pattern pTitle) {
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1).trim();
		} else {
			matcher = pTitle2.matcher(content);
			if (matcher.find()) {
				title = matcher.group(1);
				int index = title.indexOf("_");
				if (index != -1) {
					title = title.substring(0, index).trim();
				}
				index = title.indexOf("--");
				if (index != -1) {
					title = title.substring(0, index).trim();
				}
			}
		}
	}

}
