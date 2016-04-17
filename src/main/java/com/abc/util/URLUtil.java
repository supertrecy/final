package com.abc.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtil {
	public static String getDomainName(String url) {
		if (url == null || "".equals(url))
			return null;
		String host;
		try {
			host = new URL(url).getHost().toLowerCase();
			Pattern pattern = Pattern.compile(
					"[^\\.]+(\\.com\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.com|\\.net|\\.cn|\\.org|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)");
			Matcher matcher = pattern.matcher(host);
			while (matcher.find()) {
				return matcher.group();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public static boolean isNewsUrl(String url) {
		if (url == null || "".equals(url))
			return false;
		if (url.contains("index."))
			return false;
		if (!url.contains(".htm") && !url.contains(".shtml")) {
			String temp = url.substring(url.indexOf("://") + 3);
			if (temp.contains("/")) {
				temp = temp.substring(temp.indexOf("/") + 1);
				Pattern pattern = Pattern.compile(".*\\d+.*");
				return pattern.matcher(temp).matches();

			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public static boolean isOfficialWebsiteUrl(String url) {
		if (url == null || "".equals(url))
			return false;
		if (url.equals("http://schema.org/Organization"))
			return false;
		return !isNewsUrl(url);
	}
}
