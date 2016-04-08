package com.abc.crawler;

import java.util.LinkedList;
import java.util.List;

import us.codecraft.webmagic.Page;

public class SogouNewsPageProcessor extends AbstractCrawler {

	@Override
	public void process(Page page) {
		/* 如果是搜索引擎页面 */
		List<String> links = page.getHtml().css("h3.vrTitle").links().all();
		List<String> pubtimeTexts = page.getHtml().css(".news-from", "text").all();
		List<String> pubtimes = this.extractPubtime(pubtimeTexts);
		List<String> morelinks = page.getHtml().css("#news_similar").links().all();
		addLinkAndTime(links, pubtimes);
		morelinks.addAll(links);
		page.addTargetRequests(morelinks);

		/* 如果是新闻页面 */
		String title = page.getHtml().$("title", "text").toString();
		if (!title.contains("搜狗新闻搜索")) {
//			System.out.println((++i) + "." + title);
			this.parseNewsHtml(page);
		}
	}

	@Override
	protected List<String> extractPubtime(List<String> rawTexts) {
		List<String> pubtimes = new LinkedList<>();
		for (String rawText : rawTexts) {
			String pubtime = "";
			String[] s = rawText.replaceAll("\u00a0", " ").split(" ");
			if (s.length > 1)
				pubtime = s[1];
			if (pubtime == null)
				pubtimes.add("");
			else
				pubtimes.add(pubtime);
		}
		return pubtimes;
	}

}