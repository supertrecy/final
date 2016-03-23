package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.entity.NewsInfo;

/**
 * 中新网新闻解析
 * 
 * @author hjy
 */
public class ChinanewsParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(ChinanewsParser.class);

	/** Used to extract base information */
	private static final String pubtimeRegex = "<div class=\"left-time\">\\s*<.*?>\\s*(\\d{4}年\\d{2}月\\d{2}日\\s*?\\d{2}:\\d{2})";
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "来源：(?:<a.*?>)?([^<\"]*?)[<）]";
	private static final String sourceRegex2 = "来源：</span>(.*?)<";

	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pSource2;

	protected static String site = "中新网";

	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
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
			pubtime = matcher.group(1).trim(); // 2014年05月08日 15:20:51
			pubtime = pubtime.replace("年", "").replace("月", "").replace("日", "").replace(":", "");
			if (pubtime.length() == 15) {
				pubtime = pubtime.substring(0, 13);
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
			int index = title.indexOf("-");
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
			source = matcher.group(1);
			source = source.replace((char) 12288, ' ').trim(); // 特殊空白符
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) {
				source = matcher.group(1).trim();
			}
		}
		source = source.replace("]", "").replace(">", "");
		return source;
	}


}
