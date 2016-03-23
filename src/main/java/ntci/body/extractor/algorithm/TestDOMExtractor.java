package ntci.body.extractor.algorithm;

import java.io.IOException;

import ntci.body.extractor.utils.WebpageFetcher;

public class TestDOMExtractor {

	public static void main(String[] args) throws IOException {
		
		String url = "http://bbs.news.163.com/bbs/photo/169008901.html";
		String content = WebpageFetcher.getHTML(url, "gb2312"); 
		
		String text = DOMExtractor.getMainContent(content, url);
		System.out.println(text);
	}

}
