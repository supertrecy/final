package com.abc.crawler;

import com.abc.db.dao.NewsDao;
import com.abc.db.entity.NewsInfo;

import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

public class NewsDBPipeline implements PageModelPipeline<NewsInfo> {

	@Override
	public void process(NewsInfo news, Task task) {
		NewsDao.addNews(news);
	}

}
