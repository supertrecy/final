package com.abc.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abc.db.News;
import com.abc.db.NewsUtil;
import com.abc.vsm.Vsm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class TreeToJson
 */
@WebServlet("/TreeToJsonServlet")
public class TreeToJsonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		List<News> newsList = null;
		String keyword = request.getParameter("keyword");
		if(keyword == null){
			newsList = NewsUtil.getNewsListBySearchWords("");
		}else{
			newsList = NewsUtil.getNewsListBySearchWords(keyword);
		}
		List<List<News>> newsGroup = Vsm.compareMutiple(newsList);
		
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		obj.put("name", "本次搜索");
		int i = 0;
		for (Iterator iterator = newsGroup.iterator(); iterator.hasNext();) {
			List<News> news = (List<News>) iterator.next();
			if (news.size() > 1) {
				array.add(new ListToTree().listToTree(news));
				System.out.println("第"+(++i)+"棵树");
			}
		}
		obj.put("children", array);
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(obj.toString());
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
