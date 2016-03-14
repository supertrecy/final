package com.abc.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.abc.parse.HtmlParser;
import com.abc.parse.NewsInfo;

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
			System.out.println((++i)+"："+title);
			HtmlParser parser = new HtmlParser();
			try {
				NewsInfo news = parser.getParse(page.getRawText(), new URL(page.getUrl().toString()));
				page.putField("title", news.getTitle());
				page.putField("url", news.getUrl());
				page.putField("site", news.getSite());
				page.putField("pubtime", news.getPubtime());
				page.putField("fetchtime", news.getFetchtime());
				page.putField("source", news.getSource());
				page.putField("content", news.getContent());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Site getSite() {
		return site;
	}
}