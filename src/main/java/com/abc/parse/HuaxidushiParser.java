package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.NewsInfo;

/**
 * 华西都市报新闻解析
 * 
 * @author hjy
 */
public class HuaxidushiParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(HuaxidushiParser.class);

	/** Used to extract base information */
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";

	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	
	protected static String site = "华西都市报";

	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	protected void getBaseInfo(NewsInfo info, String url, String content) {
		String title = this.extractTitle(content, pTitle);
		String pubtime ="";
		String keywords = this.extractKeywords(content, pKeywords);
		String source = "";
		String plate = "";
		// LOOKAT 没有来源和时间，提取不到？
		info.setBaseInfo(site, plate, title, pubtime, keywords, source);
	}


	@Override
	protected String extractTitle(String content, Pattern pTitle) {
		String title = "";
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1).trim();
			int index = title.indexOf("·");
			if (index != -1) {
				title = title.substring(0, index);
			}
		}
		return title;
	}


}
