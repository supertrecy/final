package com.abc.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abc.cluster.Cluster;
import com.abc.cluster.ImprovedAGNEST3;
import com.abc.cluster.SimilarityContext;
import com.abc.crawler.SearchHandler;
import com.abc.crawler.extract.MutiplePageNewsCachePool;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.source.SourceTreeNode;
import com.abc.util.Util;

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
		long beginning = System.currentTimeMillis();
		long start = System.currentTimeMillis();
		List<NewsInfo> newsList = null;
		String keyword = request.getParameter("keyword");

		/* 用爬虫抓取本次关键词相关的新闻，并存入数据库 */
		List<String> search_words = Util.normalizeKeyword(keyword);
		new SearchHandler().startNewsSearch(search_words);
		
		System.out.println("*********************************cache整理*********************************");
		MutiplePageNewsCachePool cache = MutiplePageNewsCachePool.getInstance();
		MutiplePageNewsCachePool.setSearchWordsStr(search_words);
		cache.store();
		cache.clear();

		if (keyword == null) {
			newsList = NewsInfoDao.getNewsListBySearchWords("");
		} else {
			newsList = NewsInfoDao.getNewsListBySearchWords(Util.glueSearchWords(Util.normalizeKeyword(keyword)));
		}
		SimilarityContext scontext = new SimilarityContext(newsList);

		/* 初始化树 */
		LinkedList<SourceTreeNode> trees = new LinkedList<>();
		for (NewsInfo newsInfo : newsList) {
			int counter = 0;
			// 如果是原创的
			if(Util.isOriginal(newsInfo)){
				trees.add(new SourceTreeNode(newsInfo));
				continue;
			}
			// 如果不是原创，遍历所有已存在的树
			for (SourceTreeNode root : trees) {
				// 如果这个newsInfo是某棵树的父节点
				SourceTreeNode node = new SourceTreeNode(newsInfo);
				if (root.getElement().getSourceUrl().equals(newsInfo.getUrl())
						|| SimilarityContext.nearlySameNode(node, root)) {
					node.addChild(root);
					trees.remove(root);
					trees.add(node);
					break;
				}
				// 如果这个newsInfo是这棵树的叶子节点
				else if (root.insert(node)) {
					break;
				}
				// 两种情况都不是
				counter++;
			}
			// 遍历所有已存在的树都无法添加进去，用这个newsInfo创建一个新树
			if (counter == trees.size())
				trees.add(new SourceTreeNode(newsInfo));
		}

		/* 把可以连接的树连接起来 */
		List<Boolean> validFlags = new LinkedList<>();
		int size = trees.size();
		for (int i = 0; i < size; i++)
			validFlags.add(true);
		for (int i = 0; i < size - 1 && validFlags.get(i); i++) {
			for (int j = i + 1; j < size && validFlags.get(j); j++) {
				SourceTreeNode nodeI = trees.get(i);
				SourceTreeNode nodeJ = trees.get(j);
				if (nodeI.insert(nodeJ)) {
					validFlags.set(j, false);
					continue;
				}
				if (nodeJ.insert(nodeI)) {
					validFlags.set(i, false);
					break;
				}
			}
		}
		System.out.println("***初始化树耗时" + (double) (System.currentTimeMillis() - start) / 1000 + "秒");
		start = System.currentTimeMillis();

		/* 把树的列表分为有子树和没子树的两类 */
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		obj.put("name", keyword);
		List<NewsInfo> rest = new LinkedList<>();
		Map<String, List<SourceTreeNode>> map = new HashMap<>();
		for (int i = 0; i < size; i++) {
			SourceTreeNode tree = trees.get(i);
			if (validFlags.get(i).booleanValue()) {
				if (tree.getChildrenNum() >= 1) {
					// 有子树
					String site = tree.getElement().getSite();
					List<SourceTreeNode> list = map.get(site);
					if (list == null) {
						list = new LinkedList<>();
					}
					list.add(tree);
					map.put(site, list);
				} else
					// 没子树
					rest.add(tree.getElement());

			}
		}

		/* 有子树的节点聚类，并转化为json */
		Set<String> siteSet = map.keySet();
		for (String site : siteSet) {
			JSONObject treeJsonObj = new JSONObject();
			JSONArray treeJsonArr = new JSONArray();
			List<SourceTreeNode> list = map.get(site);
			if (list.size() > 1 ) {
				treeJsonObj.put("name", site);
				for (SourceTreeNode root : list) {
					treeJsonArr.add(root.wholeTreeToJSON());
				}

				treeJsonObj.put("children", treeJsonArr);
			} else {
				treeJsonObj = list.get(0).wholeTreeToJSON();
			}
			array.add(treeJsonObj);
		}

		/* 没有子树的节点聚类，并转化为json */
		List<Cluster> result = new ImprovedAGNEST3(scontext.matrix(), 0.9).clustering(newsList);
		for (Cluster cluster : result) {
			if (cluster.getPoints().size() > 1)
				array.add(ListToTree.clusterToTree(cluster));
		}
		obj.put("children", array);

		/* 写入到json文件中 */
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
		double time = (double) (System.currentTimeMillis() - beginning) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
		response.sendRedirect("demo1_2.jsp");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
