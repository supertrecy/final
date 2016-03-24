package com.abc.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.entity.NewsInfo;

/**
 * 新华网新闻解析
 * 
 * @author hjy
 */
public class XinhuaParser extends SpecialNewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(XinhuaParser.class);

	/** Used to extract base information */
	private static final String titleRegex2 = "<td height=\"39\" align=\"center\" valign=\"bottom\" class=\"bt\">(.*?)</td>";
	private static final String pubtimeRegex = "<span id=\"pubtime\">(.*?)</span>";
	private static final String pubtimeRegex2 = "\\d{4}-\\d{2}/\\d{2}";
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "<span id=\"source\">\\s*来源：(.*?)</span>";
	private static final String sourceRegex2 = "来源：\\s*(?:<a.*?>)?(.*?)</"; // 地方频道

	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pTitle2;
	private static Pattern pPubtime2;
	private static Pattern pSource2;

	protected String site = "新华网";

	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pTitle2 = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pPubtime2 = Pattern.compile(pubtimeRegex2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	}

	@Override
	protected void getBaseInfo(NewsInfo info, String url, String content) {
		String title = "";
		String pubtime = "";
		String keywords = "";
		String source = "";

		/* 提取发布时间信息 */
		Matcher matcher = pPubtime.matcher(content);
		if (matcher.find()) {
			pubtime = matcher.group(1).trim(); // 2014年05月08日 15:20:51
			pubtime = pubtime.replace("年", "").replace("月", "").replace("日", "").replace(":", "");
			if (pubtime.length() == 15) {
				pubtime = pubtime.substring(0, 13);
			}
		}
		// 如果是新华网的地方联播，由于标题都是”:: 新华网 :: - 地方联播“形式，以目前的本地搜索标题mapping的方式提取发布时间
		// 结果是错误的，因此需要单独解析，并使用解析后的时间
		if (url.startsWith("http://www.xinhuanet.com/chinanews/")) {
			matcher = pTitle2.matcher(content);
			if (matcher.find()) {
				title = matcher.group(1).trim();
			}

			matcher = pPubtime2.matcher(url);
			if (matcher.find()) {
				pubtime = matcher.group().trim();
				pubtime = pubtime.replace("/", "-");
			}
		}

		title = this.extractTitle(content, pTitle);
		keywords = this.extractKeywords(content, pKeywords);
		source = this.extractSource(content, pSource);
		info.setBaseInfo(site, title, pubtime, keywords, source);
	}

	@Override
	protected String extractTitle(String content, Pattern pTitle) {
		String title = "";
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1).trim();
			int index = title.indexOf("-");
			if (index != -1) {
				title = title.substring(0, index);
			}
			index = title.indexOf("_");
			if (index != -1) {
				title = title.substring(0, index);
			}
		}
		return title;
	}

	@Override
	protected String extractSource(String content, Pattern sourcePattern) {
		String source = "";
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) {
			source = matcher.group(1).trim();
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) {
				source = matcher.group(1).trim();
			}
		}
		source = source.replaceAll("&nbsp;", "").trim();
		return source;
	}

}
