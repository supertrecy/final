package com.abc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.abc.db.entity.NewsInfo;

import net.sf.json.JSONObject;

public class Util {
	/**
	 * 把url写入到一个html文件中，方便点击检查
	 * @param url
	 */
	
	private static String htmlfilename;
	
	static{
		htmlfilename = "E:\\"+System.currentTimeMillis()+".html";
	}
	
	public static void writeToHtmlFile(String url) {
		File f = new File(htmlfilename);
		try {
			if (!f.exists())
				f.createNewFile();
			Writer out = new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8");
			String link = "<a target=\"_blank\" href=\"" + url + "\">" + url + "</a><br>";
			out.write(link);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> normalizeKeyword(String keyword) {
		String[] split = keyword.split("[\\|,; ]");
		List<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(split));
		return list;
	}
	
	public static String glueSearchWords(List<String> search_words){
		StringBuilder sb = new StringBuilder();
		for (String word : search_words) {
			sb.append(word);
			sb.append(";");
		}
		return sb.toString();
	}
	
	public static String normalizeQueryWords(List<String> search_words) {
		StringBuilder sb = new StringBuilder("");
		for (String word : search_words) {
			sb.append(word + "+");
		}
		String query = sb.substring(0, sb.length() - 1);
		return query;
	}
	
	/**
	 * 认定没有任何源的为原创，或者源是本网站官网为原创
	 * @param news
	 * @return
	 */
	public static boolean isOriginal(NewsInfo news) {
		String sourceUrl = news.getSourceUrl();
		String url = news.getUrl();
		if (url.equals(sourceUrl))
			return true;

		String sourcesite = null;
		// source url是新闻页面url，但source url不等于url
		if (URLUtil.isNewsUrl(sourceUrl)) {
			return false;
		}
		// source url是非新闻页面的url
		else if (URLUtil.isOfficialWebsiteUrl(sourceUrl)) {
			sourcesite = URLUtil.getDomainName(sourceUrl);
			if (sourcesite != null&&sourcesite.equals(URLUtil.getDomainName(sourceUrl)))
				return true;
			else
				return false;
		}
		// source url为空
		else {
			String source = news.getSource();
			String site = news.getSite();
			if("".equals(source))
				return true;
			//如果site和source都是中文或者域名
			if(site.contains(source) || source.contains(site) || source.contains("本站")
					|| source.contains("原创"))
				return true;
			//如果source是域名，site是中文
			if(source.equals(URLUtil.getDomainName(url)))
				return true;
		}
		return false;
	}
	
	public static JSONObject wrapJsonObject(JSONObject jsonObj,NewsInfo news){
		jsonObj.put("name", news.getSite()+":"+news.getId());
		jsonObj.put("url", news.getUrl());
		jsonObj.put("title", news.getTitle());
		jsonObj.put("source", news.getSource());
		jsonObj.put("sourceUlr", news.getSourceUrl());
		return jsonObj;
	}
	
}
