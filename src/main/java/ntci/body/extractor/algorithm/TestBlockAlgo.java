package ntci.body.extractor.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import ntci.body.extractor.utils.*;

public class TestBlockAlgo {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String url = "http://bbs.news.163.com/bbs/photo/169008901.html";
		/* 注意：本处只为展示抽取效果，不处理网页编码问题，getHTML只能接收GBK编码的网页，否则会出现乱码 */
		String content = WebpageFetcher.getHTML(url, "gb2312"); // 对于该新闻，阈值需要设置成116才能提取到正文
		System.out.println(BlockAlgoExtractor.parse(content));
	}
}
