package com.abc.crawler;

import com.abc.db.NewsInfo;
import com.abc.db.NewsUtil;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

public class NewsDBPipeline implements PageModelPipeline<NewsInfo> {

	@Override
	public void process(NewsInfo news, Task task) {
		NewsUtil.addNews(news);
	}

}
