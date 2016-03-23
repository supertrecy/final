package com.abc.parse;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.entity.NewsInfo;

/**
 * 基本信息：国内、国际、社会新闻；财经（国内财经、银行）getParse可以解析国际、国内、社会、军事四个板块的新闻
 * 评论信息：国内、国际、社会新闻
 * @author hjy
 *
 * 备注：
 * 体育类新闻source目前无法提取，可以提取到，需要再写正则表达式
 */
public class SinaParser extends SpecialNewsParser{
	public static final Logger LOG = LoggerFactory.getLogger(SinaParser.class);
	
	/** Used to extract base information */
	private static final String titleRegex2 = "<meta property=\"og:title\" content=\"(.*?)\""; // 标题干净
	private static final String pubtimeRegex = "\\d{4}-\\d{2}-\\d{2}/\\d{4}"; // 从url中提取
	private static final String pubtimeRegex2 = "/(\\d{8}/\\d{4})[^/]*?$"; // 财经
	private static final String keywordsRegex = "<meta name=\"?keywords\"? content=\"(.*?)\"";
	private static final String sourceRegex = "<meta name=\"mediaid\" content=\"(.*?)\"";
	private static final String sourceRegex2 = "<span.*?id=\"media_name\".*?>(.*?)</span>"; // 地方，比如新浪四川
	private static final String sourceRegex3 = "<span id=\"art_source\">(.*?)</span>";
	
	protected static String site="新浪网";
	
	private static Pattern pTitle;
	private static Pattern pPubtime;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pTitle2;
	private static Pattern pPubtime2;
	private static Pattern pSource2;
	private static Pattern pSource3;
	
	private static HashMap<String, String> channelMap = new HashMap<String, String>();
	
	static {
		pTitle = Pattern.compile(titleRegex2, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
		pTitle2 = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE);
		pPubtime = Pattern.compile(pubtimeRegex, Pattern.CASE_INSENSITIVE);
		pPubtime2 = Pattern.compile(pubtimeRegex2, Pattern.CASE_INSENSITIVE);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);		
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
		pSource3 = Pattern.compile(sourceRegex3, Pattern.CASE_INSENSITIVE);
		
		channelMap.put("gn", "国内");
		channelMap.put("gj", "国际");
		channelMap.put("sh", "社会");
		channelMap.put("yl", "娱乐");
		channelMap.put("ty", "体育");
		channelMap.put("cj", "财经");
		channelMap.put("kj", "科技");
		channelMap.put("jc", "军事");
	}
	
	@Override
	protected void getBaseInfo(NewsInfo info, String url, String content) {
		String title = "";
		String pubtime = "";
		String keywords = "";
        String source = "";
        String plate = "";
		/*提取新闻时间*/
		Matcher matcher = pPubtime.matcher(url);
		if (matcher.find()) {
			pubtime = matcher.group().trim(); // 2014-04-28/2349格式
			pubtime = pubtime.replace('/', ' ').replaceAll("-", "");
		} else {
			matcher = pPubtime2.matcher(url);
			if (matcher.find()) {
				pubtime = matcher.group(1).trim(); // 20140505/0739格式
				pubtime = pubtime.replace('/', ' ');
			}
		}
		title = this.extractTitle(content, pTitle);
		keywords = this.extractKeywords(content, pKeywords);
		source = this.extractSource(content, pSource);
		info.setBaseInfo(site, plate, title, pubtime, keywords, source);
	}


	@Override
	protected String extractTitle(String content, Pattern pTitle) {
		String title = "";
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			title = matcher.group(1).trim();
		} else {
			matcher = pTitle2.matcher(content);
			if (matcher.find()) {
				title = matcher.group(1).trim();
				int index = title.indexOf("_");
				if (index != -1) {
					title = title.substring(0, index);
				}
			}
		}
		return title;
	}

	@Override
	protected String extractSource(String content, Pattern sourcePattern) {
		String source = "";
		Matcher matcher = sourcePattern.matcher(content);
		if (matcher.find()) { 
			source = matcher.group(1);
		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) {
				source = matcher.group(1);
			} else {
				matcher = pSource3.matcher(content);
				if (matcher.find()) {
					source = matcher.group(1);
				}
			}
		}
		return source;
	}

	
}
