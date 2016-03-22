package com.abc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {
	/**
	 * 把url写入到一个html文件中，方便点击检查
	 * @param url
	 */
	public static void writeToFile(String url) {
		File f = new File("E:\\1.html");
		if (!f.exists())
			try {
				f = Files.createFile(Paths.get("E:\\1.html")).toFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8");
			String link = "<a href=\"" + url + "\">" + url + "</a><br>";
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
}
