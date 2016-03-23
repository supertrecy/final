package com.abc.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.abc.db.dao.NewsDao;
import com.abc.db.entity.NewsInfo;
import com.abc.parse.HtmlParser;

import us.codecraft.webmagic.Page;

public class SogouNewsPageProcessor extends AbstractCrawler{

	@Override
	public void process(Page page) {
		List<String> links = page.getHtml().css("h3.vrTitle").links().all();
		page.addTargetRequests(links);
		String title = page.getHtml().$("title","text").toString();
		if(!title.contains("搜狗新闻搜索")){
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

}