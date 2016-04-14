package com.abc.parse;

import java.util.List;
import java.util.logging.Logger;

import com.abc.db.entity.NewsInfo;
import com.abc.util.FilterUtil;

public class HtmlParser {

	private NewsParserFactory parserFactory;
	
	public static Logger LOG = Logger.getLogger("com.abc.parse.HtmlParser");

	public NewsInfo getParse(List<String> search_words, String html, String url,String time) {
		/*在解析前过滤，剔除无效的新闻*/
		FilterUtil.filterBeforeParse(url, html, search_words);
		
		/*开始解析,先解析到新闻除了正文的基本信息*/
		NewsInfo info = new NewsInfo();
		this.parserFactory = new NewsParserFactory();
		boolean isStandardTime = true;
		NewsParser newsParser = parserFactory.getNewsParser(url);
		if (newsParser == null) {
			LOG.info("错误:找不到对应的解析器！" + url);
		} else {
			info = newsParser.getParse(html, null, url);
			String pubtime = info.getPubtime();
			if("".equals(pubtime)||pubtime == null){
				info.setPubtime(time);
			}else{
				isStandardTime = false;
			}
			info.setRawContent(html);
		}
		
		String text = new ContentParser().parseContent(html, url);
		info.setContent(text);
		
		/*在解析后过滤，剔除无效的新闻*/
		info = FilterUtil.filterAfterParse(info, isStandardTime, search_words);
		return info;
	}

}
