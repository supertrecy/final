package com.abc.crawler;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.util.TimeUtil;

import us.codecraft.webmagic.Page;

public class BaiduNewsPageProcessor extends AbstractCrawler {
	
	private static Pattern pDate = Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日", Pattern.CASE_INSENSITIVE); // 抽取网页时间（普通网页搜索）
	private static Pattern pDate2 = Pattern.compile("\\d+(天|小时|分钟)前", Pattern.CASE_INSENSITIVE); // 抽取网页时间（百度新闻搜索）

	@Override
	public void process(Page page) {
		/* 如果是搜索引擎页面 */
		List<String> links = page.getHtml().css("div.result h3.c-title").links().all();
		List<String> pubtimeTexts = page.getHtml().css("p.c-author","text").all();
		List<String> morelinks = page.getHtml().css(".c-more_link").links().all();
		List<String> pubtimes = this.extractPubtime(pubtimeTexts);
		addLinkAndTime(links, pubtimes);
		morelinks.addAll(links);
		page.addTargetRequests(morelinks);
		
		/* 如果是新闻页面 */
		String title = page.getHtml().$("title", "text").toString();
		if (!title.contains("百度新闻搜索")) {
//			System.out.println((++i)+"."+title);
			this.parseNewsHtml(page);
		}
	}

	@Override
	protected List<String> extractPubtime(List<String> rawTexts) {
		List<String> pubtimes = new LinkedList<>();
		for (String rawText : rawTexts) {
			String text = rawText;
			String pubtime = "";
			
			Matcher m = pDate.matcher(text); // 2015年03月05日 20:00格式
			if (m.find()) {
				pubtime = m.group().trim(); // 只提取日期部分
				pubtime = pubtime.replace("年", "-").replace("月", "-").replace("日", "");
				pubtime = TimeUtil.translateTime(pubtime);
			} else { // X小时前或X分钟前
				m = pDate2.matcher(text);
				if (m.find()) {
					pubtime = m.group().trim(); // 只提取日期部分
					pubtime = TimeUtil.translateTime(pubtime);
				}
			}
			if(pubtime == null)
				pubtimes.add("");
			else
				pubtimes.add(pubtime);
		}
		return pubtimes;
	}
	
	
}