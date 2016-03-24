package com.abc.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtil {
	
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String translateTime(String time) {
		String transTime = "";
		if (time.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
			transTime = translate(time);
		} else {
			transTime = translate2(time);
		}
		return transTime;
		
		
	}

	/** 将2014-5-7转换成2014-05-07形式 */
	private static String translate(String time) {
		if ("".equals(time)) {
			return time;
		}
		StringBuilder sb = new StringBuilder("");
		String[] splits = time.split("-");
		sb.append(splits[0] + "-");
		if (splits[1].length() == 1) {
			sb.append("0");
		}
		sb.append(splits[1] + "-");
		if (splits[2].length() == 1) {
			sb.append("0");
		}
		sb.append(splits[2]);
		return sb.toString();
		
	}
	
	
	/* 将“x天前、x分钟前”转换为日期形式 */
	private static String translate2(String pubtime) {
		try {
			// 遇到一种情况，有个空格转换为int后是160，通过trim()无法去掉
			pubtime = pubtime.replace((char)160, ' ').trim(); 
			
			Calendar ca = Calendar.getInstance();
			int index, value;
			if ((index = pubtime.indexOf("分钟")) != -1) {
				value = Integer.parseInt(pubtime.substring(0, index).trim()) * -1; // 提取出数字日期
				ca.add(Calendar.MINUTE, value);
			} else if ((index = pubtime.indexOf("小时")) != -1) {
				value = Integer.parseInt(pubtime.substring(0, index).trim()) * -1;
				ca.add(Calendar.HOUR, value);
			} else if ((index = pubtime.indexOf("天")) != -1) {
				value = Integer.parseInt(pubtime.substring(0, index).trim()) * -1; 
				ca.add(Calendar.DATE, value);
			} else if ((index = pubtime.indexOf("月")) != -1) { // 这种是"x个月前"
				value = Integer.parseInt(pubtime.substring(0, index - 1).trim()) * -1; 
				ca.add(Calendar.MONTH, value);
			} else if ((index = pubtime.indexOf("年")) != -1) { 
				value = Integer.parseInt(pubtime.substring(0, index).trim()) * -1; 
				ca.add(Calendar.YEAR, value);
			} else {
				return null;
			}
			pubtime = df.format(ca.getTime());
			return pubtime;
		} catch (Exception e) {
			System.out.println("ERROR when parse time!!!");
			return null;
		}
	}
}
