package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialNewsParser extends NewsParser {

	protected static final String titleRegex = "<title>(.*?)</title>";
	protected static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	protected static String site;
	protected static Pattern pTitle;
	protected static Pattern pPubtime;
	protected static Pattern pKeywords;
	protected static Pattern pSource;
	
	public NewsInfo getParse(String content, String encoding, String url) {
		NewsInfo info = new NewsInfo();
		String contentStr = "";
		try {
			contentStr = new String(content.getBytes(), encoding);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		info.setUrl(url);

		String curTime = df.format(System.currentTimeMillis());
		info.setFetchtime(curTime);
		getBaseInfo(info, url, contentStr);

		return info;
	}

	protected void getBaseInfo(NewsInfo info, String url, String content) {
		String title = "";
		String pubtime = "";
		String keywords = "";
        String source = "";
        String plate = "";
		this.extractTitle(content, pubtime, pTitle);
		this.extractPubTime(content, pubtime, pPubtime);
		this.extractKeywords(content, keywords, pKeywords);
		this.extractSource(content, source, pSource);
		info.setBaseInfo(site, plate, title, pubtime, keywords, source);

	}

	/*
	 * 提取新闻关键词
	 */
	protected void extractKeywords(String content, String keywords, Pattern pKeywords) {
		Matcher matcher = pKeywords.matcher(content);
		if (matcher.find()) {
			keywords = matcher.group(1).replace("\n", "");
		} else {
			keywords = "";
		}
	}

	/*
	 * 提取发布时间
	 */
	protected void extractPubTime(String content, String pubtime, Pattern pPubtime) {
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014-05-08 06:52
			pubtime = pubtime.replaceAll("-", "").replace(":", "");
		}
	}
	
	/*
	 * 提取新闻标题
	 */
	protected void extractTitle(String content, String title, Pattern pTitle) {
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1);
			int index = title.indexOf("_");
			if (index != -1) {
				title = title.substring(0, index).trim();
			}
		}
	}

	/*
	 * 提取新闻来源信息
	 */
	protected void extractSource(String content, String source, Pattern sourcePattern) {
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) {
			source = matcher.group(1);
		}
	}
}
