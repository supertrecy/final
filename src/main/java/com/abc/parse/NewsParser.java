package com.abc.parse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abc.db.entity.NewsInfo;

public class NewsParser {
	protected static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public NewsInfo getParse(String content, String encoding, String url) {
		NewsInfo info = new NewsInfo();
		return info;
	}
	
	public String extractSourceUrl(String original){
		String urlRegex = "http[s]?://([\\w|\\-|\\.|//]+)+([\\w-./?%&=]*)";
		Pattern p = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(original);
		if (matcher.find()) {
			return matcher.group(0).trim();
		}else
			return "";
	}
}
