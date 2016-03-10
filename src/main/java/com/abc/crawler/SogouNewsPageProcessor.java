package com.abc.crawler;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class SogouNewsPageProcessor implements PageProcessor {

	private static int i = 0;
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

	@Override
	public void process(Page page) {
		List<String> links = page.getHtml().css("h3.vrTitle").links().all();
		page.addTargetRequests(links);
		String title = page.getHtml().$("title","text").toString();
		if(!title.contains("搜狗新闻搜索")){
			System.out.println(++i+":"+title);
			page.putField("title", title);
		}
	}

	@Override
	public Site getSite() {
		return site;
	}
}