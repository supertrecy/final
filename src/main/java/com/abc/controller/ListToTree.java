package com.abc.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.abc.cluster.AGNEST;
import com.abc.cluster.Cluster;
import com.abc.cluster.ImprovedAGNEST2;
import com.abc.db.TreeNode;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;
import com.abc.vsm.Vsm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ListToTree {
	public static void main(String[] args) {
		test3("二胎生下三胞胎;");
	}
	/**
	 * 
	 * @param keyword
	 */
	private static void test3(String keyword) {
		long start = System.currentTimeMillis();
		List<NewsInfo> newsList = null;
		if(keyword == null || "".equals(keyword)){
			newsList = NewsInfoDao.getNewsList();
		}else{
			newsList = NewsInfoDao.getNewsListBySearchWords(keyword);
		}
		AGNEST al = new ImprovedAGNEST2(newsList, 0.7, false);
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
		//-------------------------------------//
		System.out.println("写入到json文件中...");
		try {
			Writer out = new PrintWriter(new File("E:/jee_workspace/final/WebContent/flare.json"),"utf-8");
			out.write(obj.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("写入完毕");
		//-------------------------------------//
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
	}
	
	/**
	 * 比test1多一个搜索关键词
	 * @param keyword
	 */
	private static void test2(String keyword) {
		long start = System.currentTimeMillis();
		List<NewsInfo> newsList = null;
		if(keyword == null || "".equals(keyword)){
			newsList = NewsInfoDao.getNewsList();
		}else{
			newsList = NewsInfoDao.getNewsListBySearchWords(keyword);
		}
		List<List<NewsInfo>> newsGroup = Vsm.compareMutiple(newsList);
		
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		obj.put("name", "本次搜索");
		int i = 0;
		for (Iterator iterator = newsGroup.iterator(); iterator.hasNext();) {
			List<NewsInfo> news = (List<NewsInfo>) iterator.next();
			if (news.size() > 1) {
				array.add(new ListToTree().listToTree(news));
				System.out.println("第"+(++i)+"棵树");
			}
		}
		obj.put("children", array);
		//-------------------------------------//
		System.out.println("写入到json文件中...");
		try {
			Writer out = new PrintWriter(new File("E:/jee_workspace/final/WebContent/flare.json"));
			out.write(obj.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("写入完毕");
		//-------------------------------------//
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
	}

	/**
	 * exchange those lists of which size is more than 1 to trees and output
	 * tree's json format
	 */
	public static void test1() {
		long start = System.currentTimeMillis();
		List<NewsInfo> newsList = NewsInfoDao.getNewsList();
		List<List<NewsInfo>> newsGroup = Vsm.compareMutiple(newsList);
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		obj.put("name", "本次搜索");
		for (Iterator<List<NewsInfo>> iterator = newsGroup.iterator(); iterator.hasNext();) {
			List<NewsInfo> news = (List<NewsInfo>) iterator.next();
			if (news.size() > 1) {
				array.add(new ListToTree().listToTree(news));
			}
		}
		obj.put("children", array);
		//-------------------------------------//
		System.out.println("写入到json文件中...");
		try {
			Writer out = new PrintWriter(new File("E:/jee_workspace/final/WebContent/flare.json"));
			out.write(obj.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("写入完毕");
		//-------------------------------------//
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
	}
	
	public JSONObject listToTree(List<NewsInfo> list) { // TODO change String to other object,such as TempDataStructure
		List<TreeNode<String>> queue = new LinkedList<TreeNode<String>>();
		String title = list.get(0).getTitle();
		int i=0;
		for (NewsInfo news : list) {
			String source = news.getSource();
			String site = news.getSite();
			if (source == null||"".equals(source)) {
				continue; // TODO how to resolve the problem that source is null
			} else if (site == null||"".equals(site)) {
				continue; // TODO how to resolve the problem that site is null
			}else if (site.equals(source)) {
				continue; // TODO how to resolve the problem that site = source
			}else{
				// insert node to queue's tree
				insertNode(source, site, queue);
				System.out.println((++i)+"."+news.getContent());
				System.out.println("------------------------------------------------------");
			}
		}
		//change tree to json format
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		obj.put("name", title);
		for (TreeNode<String> treeNode : queue) {
			array.add(treeNode.wholeTreeToJSON());
		}
		obj.put("children", array);
		return obj;
	}

	private void insertNode(String source, String site, List<TreeNode<String>> queue) {
		TreeNode<String> parent;
		TreeNode<String> child;
		if ((parent = findParent(source, queue)) != null) {
			parent.addChild(new TreeNode<String>(site));
		} else if ((child = findChild(site, queue)) != null) {
			//如果在队列里某棵树的根节点和site一样，那么这棵树是source的子树，移除旧树添加新树
			TreeNode<String> newNode = new TreeNode<String>(source);
			newNode.addChild(child);             
			queue.remove(child);
			queue.add(newNode);
		} else {
			TreeNode<String> newNode = new TreeNode<String>(source);
			newNode.addChild(new TreeNode<String>(site));
			queue.add(newNode);
		}
	}

	private TreeNode<String> findParent(String element, List<TreeNode<String>> queue) {
		if (queue.size() != 0) {
			for (TreeNode<String> root : queue) {
				TreeNode<String> node =  root.traverseCompare(element);
				if(node != null)
					return node;
			}
		}
		return null;
	}
	private TreeNode<String> findChild(String element, List<TreeNode<String>> queue) {
		if (queue.size() != 0) {
			for (TreeNode<String> root : queue) {
				if(root.getElement().equals(element))
					return root;
			}
		}
		return null;
	}

}
