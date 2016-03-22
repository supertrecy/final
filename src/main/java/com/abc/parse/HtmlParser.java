package com.abc.parse;

import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import com.abc.db.NewsInfo;
import com.abc.util.FilterUtil;

import ntci.body.extractor.algorithm.BlockAlgoExtractor;
import ntci.body.extractor.algorithm.DOMExtractor;

public class HtmlParser {

	private NewsParserFactory parserFactory;
	private int minBodyLength; // 正文最小长度阈值
	
	public static Logger LOG = Logger.getLogger("com.abc.parse.HtmlParser");

	public NewsInfo getParse(List<String> search_words, String html, URL url) {
		/*在解析前过滤，剔除无效的新闻*/
		FilterUtil.filterBeforeParse(url.toString(), html, search_words);
		
		/*开始解析,先解析到新闻除了正文的基本信息*/
		String text = "";
		NewsInfo info = new NewsInfo();
		this.parserFactory = new NewsParserFactory();
		boolean normalizeTime = false;
		NewsParser newsParser = parserFactory.getNewsParser(url.toString());
		if (newsParser == null) {
			LOG.info("错误:找不到对应的解析器！" + url.toString());
		} else {
			info = newsParser.getParse(html, null, url.toString());
			String pubtime = null;
			if("".equals(pubtime)||pubtime == null){
				// TODO 如果在解析器中无法提取到新闻的pubtime，则使用搜索引擎中提取到的
				normalizeTime = true;
			}
			info.setRawContent(html);
		}
		
		/*使用第一种正文解析算法，开始解析正文*/
		text = DOMExtractor.getMainContent(html, url.toString());
		if (text == null || text.length() < minBodyLength) {
			text = "";
		} else {
			// 针对新闻某些提取错误的一些妥协处理
			// 1. 新浪. 如 :
			// http://news.sina.com.cn/c/2014-12-01/013931225451.shtml
			String url_str = url.toString();
			if (url_str.contains("sina.com.cn")) {
				int index = text.indexOf("我要反馈 分享");
				if (index != -1)
					text = text.substring(0, index).trim();
			}
			// 2. 搜狐 如：http://news.sohu.com/20141201/n406562468.shtml
			if (url_str.contains("sohu.cn")) {
				int index = text.indexOf("分享： [保存到博客]");
				if (index != -1)
					text = text.substring(0, index).trim();
			}
		}
		
		/*第一种解析算法失败，使用第二种解析算法解析正文*/
		if ("".equals(text)) {
			LOG.info("第一种算法提取正文失败，改用第二种算法！");
			try {
				text = BlockAlgoExtractor.parse(html);
			} catch (Exception e) {
				LOG.info("第二种算法提取失败！");
			}
			if (text == null|| text.equals(""))
				return null;
		}
		info.setContent(text);
		
		/*在解析后过滤，剔除无效的新闻*/
		info = FilterUtil.filterAfterParse(info, normalizeTime, search_words);
		return info;
	}

}
