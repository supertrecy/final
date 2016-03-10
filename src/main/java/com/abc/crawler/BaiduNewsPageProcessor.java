package com.abc.crawler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.abc.crawler.extract.SearchUrlExtractor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class BaiduNewsPageProcessor implements PageProcessor {

	private static int i = 0;
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

	@Override
	public void process(Page page) {
		List<String> links = page.getHtml().css("div.result h3.c-title").links().all();
		page.addTargetRequests(links);
		String title = page.getHtml().$("title","text").toString();
		if(!title.contains("百度新闻搜索")){
			System.out.println(++i+":"+title);
			page.putField("title", title);
		}
		
	}

	@Override
	public Site getSite() {
		return site;
	}
	
	
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		BaiduNewsPageProcessor nProcessor = new BaiduNewsPageProcessor();
		List<String> kwords = new ArrayList<>();
		kwords.add("习近平");
		Spider.create(nProcessor)
		.addUrl(new SearchUrlExtractor().getSearchUrl(kwords,SearchUrlExtractor.BAIDU))
        .addPipeline(new JsonFilePipeline("D:\\webmagic\\"))
        //开启5个线程抓取
        .thread(5)
        //启动爬虫
        .run();
	}
}