package com.abc.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.abc.db.NewsUtil;
import com.abc.parse.HtmlParser;
import com.abc.parse.NewsInfo;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class BingNewsPageProcessor implements PageProcessor {

	private static int i = 0;
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

	@Override
	public void process(Page page) {
		List<String> links = page.getHtml().css("div.newstitle").links().all();
		page.addTargetRequests(links);
		String title = page.getHtml().$("title","text").toString();
		if(!page.getUrl().toString().contains("cn.bing.com/news")){
			System.out.println(++i+":"+title);
			HtmlParser parser = new HtmlParser();
			try {
				NewsInfo news = parser.getParse(page.getRawText(), new URL(page.getUrl().toString()));
				NewsUtil.addNews(news);
				System.out.println(news.getTitle());
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