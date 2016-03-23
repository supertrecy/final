package com.abc.db.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of methods for extract video informations.
 *
 */
public class NewsInfoDao {
	public static final Logger LOG = LoggerFactory.getLogger(NewsInfoDao.class);
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String getContent(URL url, String encoding) {
		String content = null;
		HttpURLConnection httpConnection = null; 
		InputStream in = null; 
		ByteArrayOutputStream output = null;
		int bufferSize = 8 * 1024;
		byte[] con;
		
		try {
			httpConnection = (HttpURLConnection)url.openConnection(); 
			httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:28.0) Gecko/20100101 Firefox/28.0");
//			httpConnection.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
//			httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//			httpConnection.setRequestProperty("Accept-Encoding", "gzip, deflate"); // 注意！！！
			httpConnection.setConnectTimeout(10000);  
			httpConnection.setReadTimeout(30000);  
			
			httpConnection.connect(); 
			
			if (httpConnection.getResponseCode() >= 400) { 
				throw new RuntimeException("Build connection failed!:  " + httpConnection.getResponseCode() + "" + url);
			}

			in = httpConnection.getInputStream();
			output = new ByteArrayOutputStream(bufferSize);
			byte[] b = new byte[bufferSize];
			int i = 0;
			
			while ((i = in.read(b)) != -1 ) {
				output.write(b, 0, i);
			}
			con = output.toByteArray();
//			con = processDeflateEncoded(con); // 如果设置了Accept-Encoding，此处需要解码
			content = new String(con, encoding);
		} catch (MalformedURLException e) {
			LOG.warn(e.getMessage());
			System.out.println(e.getMessage());
		} catch (IOException e) {
			LOG.warn("io error-----" + e.getMessage());
			System.out.println(e.getMessage());
		} catch (RuntimeException e) {
			LOG.warn(e.getMessage());
			System.out.println(e.getMessage());
		} finally {
			try {
				if (output != null)
					output.close();
				if (in != null)
					in.close(); 
			} catch (Exception e) {}
			if (httpConnection != null)
			    httpConnection.disconnect();
		}   
		return content;
	} 
	
	/**
	 * 解码unicode
	 * @param str
	 * @return
	 */
	public static String decodeUnicode(String str) {  
		StringBuffer sb = new StringBuffer();
		try {
			Charset set = Charset.forName("UTF-16");  
		    Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");  
		    Matcher m = p.matcher( str );  
		    int start = 0 ;  
		    int start2 = 0 ;  
		          
		    while( m.find( start ) ) {  
		        start2 = m.start() ;  
		        if( start2 > start ){  
		            String seg = str.substring(start, start2) ;  
		            sb.append( seg );  
		        }  
		        String code = m.group( 1 );  
		        int i = Integer.valueOf( code , 16 );  
		        byte[] bb = new byte[ 4 ] ;  
		        bb[ 0 ] = (byte) ((i >> 8) & 0xFF );  
		        bb[ 1 ] = (byte) ( i & 0xFF ) ;  
		        ByteBuffer b = ByteBuffer.wrap(bb);  
		        sb.append( String.valueOf( set.decode(b) ).trim() );  
		        start = m.end() ;  
		    }  
		    start2 = str.length() ;  
		    if( start2 > start ){  
		        String seg = str.substring(start, start2) ;  
		        sb.append( seg );  
		    }
		} catch (Exception e) {
			 return null;
		}        
		return sb.toString(); 
	}
	
}
