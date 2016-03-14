package ntci.body.extractor.algorithm;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ntci.body.extractor.utils.StringUtil;
import ntci.body.extractor.utils.util;

/**
 * 基于信息量衰减幅度的正文提取算法！！
 * @author hjy
 *
 */
public class DOMExtractor {
	
	private static final int  	 TIME_OUT           = 3000;
	private static final int     MAXDEPTH           = 50;
	
	private static final String  ENCODING           = "utf-8";
	private static int counter = 0;

	public static Element getMainContentElement(File html, String baseUri) {
		try {
			Element body = Jsoup.parse(html, ENCODING, baseUri).body();
			if (!isNavigatePage(body)) {
				counter++;
				return searcher(cutoutDOM(body), 0.2);
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	} 
	
	/**
	 * 入口函数
	 * @param bytes
	 * @param encoding
	 * @param baseUri
	 * @return
	 */
	public static String getMainContent(byte[] bytes, String encoding, String baseUri) {
		try {
			Element body = Jsoup.parse(new String(bytes, encoding), baseUri).body();
			if (!isNavigatePage(body)) {
				Element e = searcher(cutoutDOM(body), 0.34);
				return e.text();
			}
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 入口函数2
	 * @param bytes
	 * @param encoding
	 * @param baseUri
	 * @return
	 */
	public static String getMainContent(String html, String baseUri) {
		Element body = Jsoup.parse(html, baseUri).body();
		if (!isNavigatePage(body)) {
			Element e = searcher(cutoutDOM(body), 0.28);
			return e.text();
		}
		return "";
	}
	
	public static String getMainContentByCertainTag(byte[] bytes, String encoding, String baseUri, String attrName, String attrValue) {
		String content = "";
		Element body = null;
		try {
			body = Jsoup.parse(new String(bytes, encoding), baseUri).body();
			Element contentEle = body.getElementsByAttributeValue(attrName, attrValue).first();
			content = contentEle.text();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NullPointerException ne) {
			System.err.println("No content element.");
		}
		return content;
	}
	
	private static Element cutoutDOM(Element e) {
		deleteElements(e.getElementsByTag("form"));
		deleteElements(e.getElementsByTag("script"));
		deleteElements(e.getElementsByTag("style"));
		return e;
	}
	
	private static void deleteElements(Elements es) {
		for (int i = 0; i < es.size(); ++i) {
			es.get(i).remove();
		}
	}
	
	
	public static Element searcher(Element e, double threshold) {
		int     depth    = 0;
		int     pCTN     = 0;
		double  ratio    = 0.0;
		Element root  = e;
		
		if (root == null) {
			return null;
		}
		
		while(depth < MAXDEPTH && root.children().size() > 0) {
			Element maxCTE = selectMaxClearTextElement(root.children());
			if (maxCTE == null) {
				return root;
			}
			
			int curCTN = util.calClearTextLen(maxCTE);
			ratio = calRatio(pCTN, curCTN);

			if (ratio > threshold) {
				return root;
			} 
			
			pCTN = curCTN;
			root = maxCTE;
			++depth;
		}
		return root;
	}
	
	private static double calRatio(int pCTN, int curCTN) {
		if (pCTN == 0) {
			return 0.0;
		} else {
			return (double)(pCTN - curCTN) / pCTN;
		}
	}
	
	public static Element selectMaxClearTextElement(Elements es) {
		int max = 0;
		Element maxClearTextElement = null;
		for (Element e : es) {
			if (util.calClearTextLen(e) > max) {
				max = util.calClearTextLen(e);
				maxClearTextElement = e;
			}
		}
		return maxClearTextElement;
	}
	
	public static Document getDocument(String sURL) {
		try {
			return Jsoup.connect(sURL).userAgent(util.FIREFOX_USER_AGENT).timeout(TIME_OUT).get();
		} catch (IOException e) {
			System.err.println("Create document failed.");
			return null;
		}
	}
	
	public static String formFilePath(final String base, String index) {
		if (base != null) {
			File dir = new File(base);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return null;
				}
			}
			return new File(dir, index + ".txt").getAbsolutePath();
		} 
		return null;
	}
	
	public static void write(String filePath, String text) {
		try {
			PrintWriter out = new PrintWriter(new File(filePath).getAbsoluteFile());
			try {
				out.print(text);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	public static boolean isNavigatePage(Element body) {
		if (body != null && body.hasText()) {
			int pageLen  = calPageTextLength(body);
			int linksLen = calLinksTextLength(body);
			if (pageLen > 0) {
				double ratio = (double)linksLen / pageLen;
				if (ratio > 0.83) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	public static int calLinksTextLength(Element e) {
		Elements links = e.getElementsByTag("A");
		
		if (links != null && links.hasText()) {
			int links_len  = 0;
			for (Element l : links) {
				links_len += calElementTextLength(l);
			}
			return links_len;
		}
		return 0;
	}
	
	private static int calPageTextLength(Element body) {
		return calElementTextLength(body);
	} 
	
	public static int calElementTextLength(Element e) {
		if (e != null && e.hasText()) {
			return StringUtil.countWord(e.text());
		}
		return 0;
	}	
}










