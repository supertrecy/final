package com.abc.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abc.cluster.AGNEST;
import com.abc.cluster.Cluster;
import com.abc.cluster.ImprovedAGNEST2;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.vsm.Vsm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**@author yh
 * Servlet implementation class TraceToSourceServlet
 */
@WebServlet(description = "no data input, but output json", urlPatterns = { "/TraceToSourceServlet" })
public class TraceToSourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long start = System.currentTimeMillis();
		List<NewsInfo> newsList = null;
		String keyword = request.getParameter("keyword");
		if(keyword == null){
			newsList = NewsInfoDao.getNewsListBySearchWords("");
		}else{
			newsList = NewsInfoDao.getNewsListBySearchWords(keyword);
		}
		List<List<NewsInfo>> newsGroup = Vsm.compareMutiple(newsList);
		
//		JSONObject obj = new JSONObject();
//		JSONArray array = new JSONArray();
//		obj.put("name", keyword);
//		int i = 0;
//		for (Iterator<List<NewsInfo>> iterator = newsGroup.iterator(); iterator.hasNext();) {
//			List<NewsInfo> news =  iterator.next();
//			if (news.size() > 1) {
//				array.add(new ListToTree().listToTree(news));
//				System.out.println("构建第"+(++i)+"棵树");
//			}
//		}
		AGNEST al = new ImprovedAGNEST2(newsList, 0.3, false);
		List<Cluster> clusters = al.clustering();
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		obj.put("name", keyword);
		int i = 0;
		for (Cluster cluster : clusters) {
			List<NewsInfo> news = cluster.getPoints();
			if (news.size() > 1) {
				array.add(new ListToTree().listToTree(news));
				System.out.println("第"+(++i)+"棵树");
			}
		}
		obj.put("children", array);
		System.out.println("写入到json文件中...");
		try {
			String filePath = request.getServletContext().getRealPath("/flare.json");
			PrintWriter out = new PrintWriter(new FileWriter(new File(filePath)));
			out.write(obj.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("写入完毕");
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
		response.sendRedirect("demo1_1.jsp");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
}
