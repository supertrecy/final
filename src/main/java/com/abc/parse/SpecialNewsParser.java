package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.db.entity.NewsInfo;

public class SpecialNewsParser extends NewsParser {

	protected static final String titleRegex = "<title>(.*?)</title>";
	protected static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";

	public NewsInfo getParse(String content, String encoding, String url) {
		NewsInfo info = new NewsInfo();
		info.setUrl(url);
		String curTime = df.format(System.currentTimeMillis());
		info.setFetchtime(curTime);
		getBaseInfo(info, url, content);
//		if (info.getSource() == null || "".equals(info.getSource())) {
//			System.out.println("提取失败"+"："+info.getUrl());
//		}else{
//			System.out.println(info.getSite()+"："+info.getSource());
//		}
		return info;
	}

	protected void getBaseInfo(NewsInfo info, String url, String content) {

	}

	/*
	 * 提取新闻关键词
	 */
	protected String extractKeywords(String content, Pattern pKeywords) {
		Matcher matcher = pKeywords.matcher(content);
		String keywords = "";
		if (matcher.find()) {
			keywords = matcher.group(1).replace("\n", "");
		} else {
			keywords = "";
		}
		return keywords;
	}

	/*
	 * 提取发布时间
	 */
	protected String extractPubTime(String content, Pattern pPubtime) {
		Matcher matcher = pPubtime.matcher(content);
		String pubtime = "";
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014-05-08 06:52
			pubtime = pubtime.replaceAll("-", "").replace(":", "");
		}
		return pubtime;
	}

	/*
	 * 提取新闻标题
	 */
	protected String extractTitle(String content, Pattern pTitle) {
		Matcher matcher = pTitle.matcher(content);
		String title = "";
		if (matcher.find()) {
			title = matcher.group(1);
			int index = title.indexOf("_");
			if (index != -1) {
				title = title.substring(0, index).trim();
			}
		}
		return title;

	}

	/*
	 * 提取新闻来源信息
	 */
	protected String extractSource(String content, Pattern sourcePattern) {
		Matcher matcher = sourcePattern.matcher(content);
		String source = "";
		if (matcher.find()) {
			source = matcher.group(1);
		}
		return source;
	}
}
