package com.abc.parse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.abc.db.entity.NewsInfo;

public class NewsParser {
	protected static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public NewsInfo getParse(String content, String encoding, String url) {
		NewsInfo info = new NewsInfo();
		return info;
	}
}
