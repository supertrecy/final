package ntci.body.extractor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	public final static String    chineseRegex        =       "([\\u4e00-\\u9fa5])";
	public final static String    EnglishRegex        =       "[a-zA-Z]+";
	public final static String    digitRegex          =       "^(-?\\d+)(\\.\\d+)?$";
	
	/**
	 * 
	 * @param s
	 * @param regex
	 * @return
	 */
	public static String removeStringByRegex(String s, String regex) {
		StringBuffer sb;
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(s);
		sb = new StringBuffer();
		while(m.find()) 
			m.appendReplacement(sb, "");
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param filePath
	 * @param text
	 */
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
	
	/**
	 * 
	 * @param sFileName
	 * @param sEncode
	 * @return
	 */
	public static String readTextFile(String sFileName, String sEncode) {
		BufferedReader br = null; 
		try {
			br = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(new File(sFileName)), sEncode));
			String line = "";
			StringBuffer   sb = new StringBuffer();
			while (null != (line = br.readLine())) {
				sb.append(line).append("\r\n");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getLine(String sFileName) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(new File(sFileName))));
			return br.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void getLineFromTextFile(String sFileName, Vector<String> v) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(new File(sFileName))));
			String line = "";
			while (null != (line = br.readLine())) {
				if(!line.equals(""))
					v.add(line);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNumber(String s) {
		if (counter(s, digitRegex) == 0) {
			return false;
		}
		return true;
	}
	
	public static boolean isEnglishWord(String word) {
		if (countEnglish(word) == 0) {
			return false;
		}
		return true;
	}

	public static int countWord(String s) {
		return countChinese(textFilter(s)) +  countEnglish(s) + countNum(s);
	}
	
	public static int countChinese(String s) {
		return counter(s, chineseRegex);
	}
	
	public static int countNum(String s) {
		return counter(s, digitRegex);
	}
	
	public static int countEnglish(String s) {
		return counter(s, EnglishRegex);
	}
	
	private static int counter(String s, String regx) {
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(s);
		int c = 0;
		while(m.find()) {
			c++;
		}
		return c;
	}
	
	public static String textFilter(String text) {
		text = text.replaceAll("[【】]", "");
		text = text.replaceAll("\\|", "");
		text = text.replaceAll("\\s+", "");
		text = text.replaceAll("\"", "");
		text = text.trim();
		return text;
	}
	
	
	public static void main(String[] args) {

	}
	
	
	
	
}









