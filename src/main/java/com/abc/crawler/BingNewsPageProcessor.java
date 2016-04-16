package com.abc.crawler;

import java.util.LinkedList;
import java.util.List;

import com.abc.util.TimeUtil;

import us.codecraft.webmagic.Page;

public class BingNewsPageProcessor extends AbstractCrawler {

	@Override
	public void process(Page page) {
		/* 如果是搜索引擎页面 */
		List<String> links = page.getHtml().css("div.newstitle").links().all();
		List<String> pubtimeTexts = page.getHtml().css(".sn_tm","text").all();
		List<String> pubtimes = this.extractPubtime(pubtimeTexts);
		addLinkAndTime(links, pubtimes);
		page.addTargetRequests(links);
//		for (String string : links) {
//			System.out.println(string);
//		}
		/* 如果是新闻页面 */
		String url = page.getUrl().toString();
		if (!url.contains("cn.bing.com/news")) {
			System.out.println((++i)+"."+url);
			this.parseNewsHtml(page);
		}
	}

	@Override
	protected List<String> extractPubtime(List<String> rawTexts) {
		List<String> pubtimes = new LinkedList<>();
		for (String rawText : rawTexts) {
			String pubtime = "";
			pubtime = rawText.replaceAll("/", "-");
			pubtime = TimeUtil.translateTime(pubtime);
			if(pubtime == null)
				pubtimes.add("");
			else
				pubtimes.add(pubtime);
		}
		return pubtimes;
	}
	
	

}