package com.abc.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abc.crawler.SearchHandler;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.util.Util;
import com.abc.vsm.Vsm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author yh Servlet implementation class TraceToSourceRealTimeServlet
 */
@WebServlet(description = "no data input, but output json", urlPatterns = { "/TraceToSourceRealTimeServlet" })
public class TraceToSourceRealTimeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long start = System.currentTimeMillis();
		List<NewsInfo> newsList = null;
		String keyword = request.getParameter("keyword");

		/* 用爬虫抓取本次关键词相关的新闻，并存入数据库 */
		List<String> search_words = Util.normalizeKeyword(keyword);
		new SearchHandler().startNewsSearch(search_words);

		/* 从数据库中获取本次关键词相关的新闻列表 */
		newsList = NewsInfoDao.getNewsListBySearchWords(Util.glueSearchWords(search_words));

//		/* 根据新闻内容对新闻列表进行分类 */
//		List<List<NewsInfo>> newsGroup = Vsm.compareMutiple(newsList);
//		
//		/* 把子列表先转化为树，再把树列表转化为JsonObject */
//		JSONObject obj = new JSONObject();
//		JSONArray array = new JSONArray();
//		obj.put("name", keyword);
//		int i = 0;
//		for (Iterator<List<NewsInfo>> iterator = newsGroup.iterator(); iterator.hasNext();) {
//			List<NewsInfo> news = (List<NewsInfo>) iterator.next();
//			if (news.size() > 1) {
//				array.add(new ListToTree().listToTree(news));
//				System.out.println("构建第" + (++i) + "棵树");
//			}
//		}
//		obj.put("children", array);
//
//		/* 把这个jsonobject写入到flare.json文件中，方便d3.js插件通过使用这个文件内容进行可视化 */
//		System.out.println("写入到json文件中...");
//		try {
//			String filePath = request.getServletContext().getRealPath("/flare.json");
//			PrintWriter out = new PrintWriter(new FileWriter(new File(filePath)));
//			out.write(obj.toString());
//			out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("写入完毕");
//		double time = (double) (System.currentTimeMillis() - start) / 1000;
//		System.out.println("总共" + newsList.size() + "篇文章");
//		System.out.println("总耗时" + time + "秒");
//		response.sendRedirect("demo1_1.jsp");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
