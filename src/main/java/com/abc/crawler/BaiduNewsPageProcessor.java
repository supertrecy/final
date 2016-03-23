package com.abc.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.abc.db.dao.NewsDao;
import com.abc.db.entity.NewsInfo;
import com.abc.parse.HtmlParser;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class BaiduNewsPageProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	private List<String> search_words;
	
	@Override
	public void process(Page page) {
		List<String> links = page.getHtml().css("div.result h3.c-title").links().all();
		page.addTargetRequests(links);
		String title = page.getHtml().$("title","text").toString();
		if(!title.contains("百度新闻搜索")){
			HtmlParser parser = new HtmlParser();
			try {
				NewsInfo news = parser.getParse(search_words,page.getRawText(), new URL(page.getUrl().toString()));
				if(news != null)
					NewsDao.addNews(news);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Site getSite() {
		return site;
	}
	
	public PageProcessor setSearchWords(List<String> search_words){
		this.search_words = search_words;
		return this;
	}
}