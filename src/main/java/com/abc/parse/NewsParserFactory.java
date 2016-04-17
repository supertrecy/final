
package com.abc.parse;

import java.util.HashMap;
import java.util.Map;

import com.abc.util.URLUtil;

public class NewsParserFactory {
	private static Map<String, NewsParser> parserMap = new HashMap<String, NewsParser>();
	private static CommonParser cp = new CommonParser();

	static {
		parserMap.put("sina.com.cn", new SinaParser()); // 新浪
		parserMap.put("163.com", new WangyiParser()); // 网易
		parserMap.put("sohu.com", new SohuParser()); // 搜狐
		parserMap.put("qq.com", new QQParser()); // 腾讯
		parserMap.put("ifeng.com", new IfengParser()); // 凤凰
		parserMap.put("hexun.com", new HexunParser()); // 和讯网
		parserMap.put("people.com.cn", new PeopleParser()); // 人民网
		parserMap.put("xinhuanet.com", new XinhuaParser()); // 新华网
		parserMap.put("chinanews.com", new ChinanewsParser()); // 中新网
		parserMap.put("gmw.cn", new GuangmingParser()); // 光明网
		parserMap.put("ce.cn", new CeParser()); // 中国经济网
		parserMap.put("21cbh.com", new Shiji21Parser()); // 21世纪网
		parserMap.put("cb.com.cn", new JingyingParser()); // 中国经营网
		parserMap.put("scol.com.cn", new SichuanolParser()); // 四川在线
		parserMap.put("newssc.org", new SiChuanNewsParser()); // 四川新闻网
		parserMap.put("wccdaily.com.cn", new HuaxidushiParser()); // 华西都市报
	}

	/* 如果是新闻页，返回对应的解析器，否则返回null */
	public NewsParser getNewsParser(String url) {
		String domain = URLUtil.getDomainName(url);
		if (parserMap.containsKey(domain)) {
			NewsParser parser = (NewsParser) parserMap.get(domain);
			return parser;
		} else {
			return cp;
		}
	}
}
