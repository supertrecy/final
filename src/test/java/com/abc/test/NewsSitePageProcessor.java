package com.abc.test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.abc.db.dao.SiteDao;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class NewsSitePageProcessor implements PageProcessor{
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	private int i = 0;
	
	private static SiteDao dao;
	
	static{
		dao = new SiteDao();
	}

	@Override
	public void process(Page page) {
		System.out.println((++i)+".");
		List<String> sites = page.getHtml().css("h3.rightTxtHead a","text").all();
		List<String> domains = page.getHtml().css("h3.rightTxtHead span","text").all();
		Iterator it1 = domains.iterator();
		Iterator it2 = sites.iterator();
		for (; it1.hasNext()&&it2.hasNext();) {
			String domain = (String) it1.next();
			String site = (String) it2.next();
			dao.add(new com.abc.db.entity.Site(domain, site));
		}
	}

	@Override
	public Site getSite() {
		return site;
	}
	
	public static void main(String[] args) {
		List<String> links = new LinkedList<>();
		links.add("http://top.chinaz.com/hangye/index_news.html");
		for (int i = 2; i < 58; i++) {
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("http://top.chinaz.com/hangye/index_news_");
			strBuilder.append(i);
			strBuilder.append(".html");
			links.add(strBuilder.toString());
		}
		System.out.println(links.toString());
		String[] linksArray = new String[links.size()];
		linksArray = links.toArray(linksArray);
		Spider.create(new NewsSitePageProcessor())
		.addUrl(linksArray)
		.addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(1).run();
	}
	
}
