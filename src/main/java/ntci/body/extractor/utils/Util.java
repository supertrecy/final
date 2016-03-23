package ntci.body.extractor.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Util {
	public static final String FIREFOX_USER_AGENT    =     "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.15) Gecko/20110303 Firefox/3.6.15";
	public final static String[]   cleanRegexs        =    {"<style[^>]*?>[\\s\\S]*?</style>",                 // 
													        "<script[^>]*?>[\\s\\S]*?</script>",               // 
													        "&quot;",                                          // 
													        "&amp;",                                           // 
													        "&lt;",                                            //              
													        "&gt;",                                            // 
													        "&nbsp;", };                                       // 
	public final static String[]   arrayRep           =    {"", 
													        "", 
													        "\"", 
													        "&", 
													        "<", 
													        ">",
													        " " };
	public static final String[]   INVALID_TAGS       =    {"STYLE",  "COMMENT",  "SCRIPT",  "OPTION",
														    "IFRAME", "TEXTAREA", "OBJECT",  "SELECT",
														    "INPUT",  "TEXTAREA"};
	
	private static Element longestClearTextElement = null;
	private static Element mainElement = null;
	
	public static double calElementsLV(Elements es) {
		List<Integer> l = new ArrayList<Integer>();
		int  max   = 0;
		int  len   = 0;
		for (Element e : es) { 			
			if ((len = calClearTextLen(e)) != 0) {
				l.add(len);
			}
			if (len > max || len == max) {
				max = len;
				longestClearTextElement = e;
			}
		}
		return Math.sqrt(calVariance(l));
	}
	
	public static double calVariance(List<Integer> es) {
		if (es != null && es.size() > 0) {
			double ave = average(es);
			double sum = 0.0;
			for (double d : es) {
				sum += square(d - ave);
			}
			return sum / es.size();
		} else {
			return -1;
		}
	}
	
	private static double square(double x) {
		return x*x;
	}
	
	private static double average(List<Integer> es) {
		if (es != null && es.size() > 0) {
			double sum = 0.0;
			for (int i : es) {
				sum += i;
			}
			return sum / es.size();
		} else {
			return -1;
		}
	}
	
	public static String removeALLTag(final Node node) {
		if (node != null) {
			return removeAllTag(node.toString());
		} else {
			return null;
		}
	}
	
	public static String removeAllTag(String html) {
		int cur, len;
		StringBuffer sb = new StringBuffer();

		boolean inTag = false;
		len = html.length();
		for (int index = 0; index < len; index++) {
			cur = html.charAt(index);
			// cur == '<'
			if (cur == 60) {
				inTag = true;
				continue;
				// cur == '>'
			} else if (cur == 62) {
				inTag = false;
				continue;
			} else if (inTag == true) {
				continue;
			} else {
				sb.append((char) cur);
			}
		}
		return sb.toString();
	}
	
	public static String filterHtml(String html) {
		Pattern p;
		Matcher m;
		StringBuffer sb = null;
		for (int i = 0; i < cleanRegexs.length; ++i) {
			sb = new StringBuffer();
			p = Pattern.compile(cleanRegexs[i]);
			m = p.matcher(html);
			while (m.find())
				m.appendReplacement(sb, arrayRep[i]);
			m.appendTail(sb);
			html = sb.toString();
		}
		return sb.toString();
	}
	
	public static double calCoefficient(int ftl, double lv) {
		return (lv / ftl);
	} 
	
	public static String getText(Element e) {
		if (e.hasText()) {
			return e.text();
		}
		return "";
	}
	
	public static int getFTL(Element e) {
		return StringUtil.countWord(getText(e));
	}

	public static void getClearText(StringBuffer content, final Element element) {
		if (element.ownText() != "" && isMainContentElement(element)) {
			content.append(element.ownText()).append("\n");
		} 
		if (element.children().size() > 0){
			Elements children = element.children();
			if (children != null) {
				for (Element e : children) {
					getClearText(content, e);
				}
			}
		}
	}
	
	public static void getLinkText(StringBuffer content, final Element element) {
		if (isLink(element)) {
			content.append(element.text()).append("\n");
		} else if (element.children().size() > 0) {
			Elements children = element.children();
			if (children != null) {
				for (Element e : children) {
					getLinkText(content, e);
				}
			}
		}
	}
	
	public static int calClearTextLen(Element e) {
		StringBuffer sb = new StringBuffer();
		//getClearText(sb, e);
		getLinkText(sb, e);
		int t = StringUtil.countWord(e.text());
		int l = StringUtil.countWord(sb.toString());
		return (StringUtil.countWord(e.text()) - StringUtil.countWord(sb.toString()));
	}
	
	public static void search(Element element) {
		if (element.children().size() == 0) {
			return;
		}
		
		Elements children   = element.children();
		if (children.size() == 1) {
			search(children.first());
		}
		
		int    ftl = getFTL(element);
		double lv = calElementsLV(children);
		double k = calCoefficient(ftl, lv);

		if (k == 0) {
			mainElement = element;
			return;
		}
		
		search(longestClearTextElement);
	}
	
	private static boolean isMainContentElement(final Element e) {
		if (isInvalidElement(e) || isLink(e)) {
			return false;
		}
		return true;
	}
	
	private static boolean isInvalidElement(final Element e) {
		for (String s : INVALID_TAGS) {
			if (e.tagName().equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
	
	private static boolean isLink(final Element e) {
		if (e.tagName().equalsIgnoreCase("A")) {
			if (e.attr("href") != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Document doc = Jsoup.connect("http://www.chinadaily.com.cn/hqcj/gjcj/2011-04-04/content_2206559.html").
		                     userAgent(FIREFOX_USER_AGENT).
		                     timeout(3000).get();
		Elements es = doc.getElementsByAttributeValue("valign", "top");
		
		StringBuffer sb = new StringBuffer();
		for (Element e : es) {
			System.out.println(calClearTextLen(e.parent()));
		}
	}

}







