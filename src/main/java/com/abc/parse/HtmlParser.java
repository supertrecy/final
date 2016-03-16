package com.abc.parse;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ntci.body.extractor.algorithm.BlockAlgoExtractor;
import ntci.body.extractor.algorithm.DOMExtractor;

public class HtmlParser {

	// I used 1000 bytes at first, but found that some documents have
	// meta tag well past the first 1000 bytes.
	// (e.g. http://cn.promo.yahoo.com/customcare/music.html)
	private static final int CHUNK_SIZE = 2000;

	// NUTCH-1006 Meta equiv with single quotes not accepted
	private static Pattern metaPattern = Pattern.compile("<meta\\s+([^>]*http-equiv=(\"|')?content-type(\"|')?[^>]*)>",
			Pattern.CASE_INSENSITIVE);
	private static Pattern charsetPattern = Pattern.compile("charset=\\s*([a-z][_\\-0-9a-z]*)",
			Pattern.CASE_INSENSITIVE);
	private static Pattern ntci_charsetPattern = Pattern.compile("<meta charset=\\s?\"(.*?)\"");

	private NewsParserFactory parserFactory;
	private int minBodyLength; // 正文最小长度阈值
	// added end

	public static Logger LOG = Logger.getLogger("com.abc.parse.HtmlParser");

	public NewsInfo getParse(String content, URL url) {
		String text = "";
		NewsInfo news = new NewsInfo();
		String extract_encoding;
		byte[] htmlBytes = content.getBytes();

		String encoding = sniffCharacterEncoding(htmlBytes);
		this.parserFactory = new NewsParserFactory();
		NewsParser newsParser = parserFactory.getNewsParser(url.toString());
		if (newsParser == null) {
			System.out.println("错误:找不到对应的解析器！");
			LOG.info("错误:找不到对应的解析器！" + url.toString());
		} else {
			news = newsParser.getParse(content, encoding,url.toString());
			/* 若时间提取不出(比如可能是15个站点其它板块的页面)，则采用搜索引擎所得结果 */
			/* 发布时间之间采用从搜索引擎提取到的时间 */ // TODO
			news.setPubtime("todo");
			try {
				// 存储网页原始文本
				news.setRawContent(new String(htmlBytes, encoding));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		extract_encoding = encoding; // 提取正文时使用
		/* 若是论坛，只使用BlockAlgorithms */
		text = DOMExtractor.getMainContent(htmlBytes, extract_encoding, url.toString());
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

		if ("".equals(text)) {
			LOG.info("第一种算法提取正文失败，改用第二种算法！");
			try {
				text = BlockAlgoExtractor.parse(new String(content.getBytes(), extract_encoding));
			} catch (Exception e) {
			}
			if (text == null)
				text = "";
		}
		news.setContent(text);
		return news;
	}

	private static String sniffCharacterEncoding(byte[] content) {
		int length = content.length < CHUNK_SIZE ? content.length : CHUNK_SIZE;

		// We don't care about non-ASCII parts so that it's sufficient
		// to just inflate each byte to a 16-bit value by padding.
		// For instance, the sequence {0x41, 0x82, 0xb7} will be turned into
		// {U+0041, U+0082, U+00B7}.
		String str = "";
		try {
			str = new String(content, 0, length, Charset.forName("ASCII").toString());
		} catch (UnsupportedEncodingException e) {
			// code should never come here, but just in case...
			return null;
		}

		Matcher metaMatcher = metaPattern.matcher(str);
		String encoding = null;
		if (metaMatcher.find()) {
			Matcher charsetMatcher = charsetPattern.matcher(metaMatcher.group(1));
			if (charsetMatcher.find())
				encoding = new String(charsetMatcher.group(1));
		}

		// 有些网页没有content-type, 直接从头部信息中提取charset信息,
		// 如http://news.cjn.cn/mtzq/201501/t2594501.htm
		Matcher ntciMatcher = ntci_charsetPattern.matcher(str);
		if (ntciMatcher.find()) {
			encoding = new String(ntciMatcher.group(1).trim());
		}

		return encoding;
	}
}
