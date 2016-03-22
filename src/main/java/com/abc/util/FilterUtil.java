package com.abc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.NewsInfo;

public class FilterUtil {

	private static final Logger LOG = LoggerFactory.getLogger(FilterUtil.class);
	private static final Pattern checkPubtime = Pattern.compile("^[0-9\\s:-]+$");
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat df_prev = new SimpleDateFormat("yyyyMMdd");

	public static NewsInfo filterAfterParse(NewsInfo info, boolean normalizeTime, List<String> filterWords) {
		 /*过滤不含关键词的内容*/
		String content = info.getContent();
		if (!containKeywords(content, wordSegmentation(filterWords))) {
			//System.out.println(content);  //TODO 还是会漏掉相关的，可以和标题一起筛选查看是否是标题党
			//System.out.println("--------------------------------------------------");
			return null;
		}

		/*过滤发布时间，如果需要的话格式化*/
		if (normalizeTime) {
			String pubtime = info.getPubtime(); // 2014-05-06形式或20140506 2211形式
			pubtime = pubtime.replaceAll("-", "");
			Matcher m = checkPubtime.matcher(pubtime);
			String day = "";
			if (!m.find()) {
				LOG.warn("警告：日期不合法" + info.getUrl() + ",pubtime:" + info.getPubtime());
			} else if (info.getPubtime().length() < 8) {
				LOG.warn("警告：日期不合法" + info.getUrl() + ",pubtime:" + info.getPubtime());
			} else {
				day = info.getPubtime().substring(0, 8); // 20130414形式
				try {
					info.setPubtime(df.format(df_prev.parse(day)));
				} catch (ParseException e) {
					LOG.error("");
				}
			}
		}
		 /*过滤标题*/
		String title = info.getTitle();
		title = title.replaceAll("：", ":"); // 某些标题标点符号不同，统一一下
		if (title.length() > 100)
			title = title.substring(0, 100);
		info.setTitle(title);
		// TODO 检查是否是无效的标题

		/*没有site，则提取url中的域名*/
		String site = info.getSite();
		if ("".equals(site)) {
			try {
				site = URLUtil.getDomainName(info.getUrl());
			} catch (Exception e) {
				LOG.error("不合法的url：" + info.getUrl());
			}
		}
		/* 过滤长度过长的source*/
		String source = info.getSource();
		if (info.getSource() == null || "".equals(info.getSource())) {
			source = ParseUtil.parseNewsSource(info.getRawContent());
			System.out.println(info.getSite()+":"+source+" 提取失败" + "：" + info.getUrl());
			info.setSource(source);
		} 
		
		source = source.replaceAll("[\\\\,;>： :<\"“”]", "");
		info.setSource(source);
		if (source.length() > 10)
			info.setSource(""); // source长度过长，肯定是提取失败
		

		/*过滤关键词keywords*/
		String webword = info.getKeywords();
		if (webword.length() > 100)
			info.setKeywords(webword);

		return info;
	}

	public static boolean filterBeforeParse(String url, String html, List<String> filterWords) {
		/*过滤URL, 如果是这样包含这些前缀的url则不是新闻*/
		if (url.contains("tieba.") || url.contains("bbs.") || url.contains("club.") || url.contains("forum.")
				|| url.contains("blog.")) {
			return false;
		}
		/*过滤不包含关键词的新闻*/
		if (!containKeywords(html, filterWords))
			return false;

		return true;
	}

	private static boolean containKeywords(String text, List<String> filterWords) {
		for (String word : filterWords) {
			if (!text.contains(word)) {
				return false;
			}
		}
		return true;
	}
	
	private static List<String> wordSegmentation(List<String> filterWords){
		List<String> words = new LinkedList<>();
		for (String string : filterWords) {
			words.addAll(WordSegUtil.participle(string));
		}
		return words;
		
	}

}
