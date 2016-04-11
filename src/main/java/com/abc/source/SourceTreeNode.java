package com.abc.source;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.abc.cluster.SimilarityRealTimeMatrix;
import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SourceTreeNode {
	private NewsInfo element;
	private List<SourceTreeNode> children;

	public SourceTreeNode(NewsInfo element) {
		this.element = element;
		this.children = new LinkedList<>();
	}

	public SourceTreeNode(NewsInfo element, List<SourceTreeNode> children) {
		this.element = element;
		this.children = children;
	}

	public NewsInfo getElement() {
		return element;
	}

	public List<SourceTreeNode> getChildren() {
		return children;
	}

	public void addChild(SourceTreeNode child) {
		children.add(child);
	}

	public void addChildren(List<SourceTreeNode> children) {
		this.children.addAll(children);
	}

	static int count = 0;

	public void travelPrint(int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append("+");
		}
		String seg = sb.toString();
		String f = "".equals(element.getSourceUrl()) || element.getSourceUrl() == null ? "没有" : "有";

		System.out.println(seg + f + "." + element.getUrl());
		count++;
		int nextLevel = level + 1;
		for (SourceTreeNode child : children) {
			child.travelPrint(nextLevel);
		}
	}

	public boolean insert(SourceTreeNode node) {
		String url = element.getUrl();
		String sourceUrl = node.getElement().getSourceUrl();
		//判断能否加入到children中
		if (sourceUrl == null || "".equals(sourceUrl)) {
			//通过内容判断
			return this.isChild(node, this);
		} else {
			//通过url比对
			if (url.equals(sourceUrl)) {
				this.addChild(node);
				return true;
			}
		}
		//递归到子树中
		for (SourceTreeNode child : children) {
			if (child.insert(node))
				return true;
		}

		return false;
	}

	public JSONObject wholeTreeToJSON() {

		JSONObject root = new JSONObject();
		String tip = "";
		if(element.getSource().equals(""))
			tip += "no source,";
		if(element.getSourceUrl().equals(""))
			tip += "no source url,";
		root.put("name", tip+element.getId());
		if (getChildrenNum() != 0) {
			JSONArray jsonChildren = new JSONArray();
			for (SourceTreeNode child : children) {
				JSONObject jsonChild = child.wholeTreeToJSON();
				jsonChildren.add(jsonChild);
			}
			root.put("children", jsonChildren);
		} else {
			root.put("size", getChildrenNum());
		}
		return root;
	}

	private int getChildrenNum() {
		return children.size();
	}

	private static boolean isNewsUrl(String url) {
		if (!url.contains(".htm") && !url.contains(".shtml")) {
			String temp = url.substring(url.indexOf("://") + 3);
			if (temp.contains("/")) {
				temp = temp.substring(temp.indexOf("/") + 1);
				Pattern pattern = Pattern.compile(".*\\d+.*");
				return pattern.matcher(temp).matches();
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private static SimilarityRealTimeMatrix smatrix;

	/**
	 * for test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		String keyword = "女子酒店遇袭;";
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords(keyword);
		smatrix = new SimilarityRealTimeMatrix(newsList);
		System.out.println("总共耗时" + (double) (System.currentTimeMillis() - start) / 1000 + "秒");
		LinkedList<SourceTreeNode> trees = new LinkedList<>();
		for (NewsInfo newsInfo : newsList) {
			int counter = 0;
			// 遍历所有已存在的树
			for (SourceTreeNode root : trees) {
				// 如果这个newsInfo是某棵树的父节点
				SourceTreeNode node = new SourceTreeNode(newsInfo);
				if (root.getElement().getSourceUrl().equals(newsInfo.getUrl())||isParent(node, root)) {
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
		System.out.println("初始化树");
		List<Boolean> flags = new LinkedList<>();
		int size = trees.size();
		for (int i = 0; i < size; i++)
			flags.add(true);
		for (int i = 0; i < size - 1 && flags.get(i); i++) {
			for (int j = i + 1; j < size && flags.get(j); j++) {
				SourceTreeNode nodeI = trees.get(i);
				SourceTreeNode nodeJ = trees.get(j);
				if (nodeI.insert(nodeJ)) {
					flags.set(j, false);
					continue;
				}
				if (nodeJ.insert(nodeI)) {
					flags.set(i, false);
					break;
				}
			}
		}

		int counter = 0;
		for (int i = 0; i < size; i++) {
			SourceTreeNode tree = trees.get(i);
			if (flags.get(i).booleanValue() && tree.getChildren().size() > 0) {
				tree.travelPrint(0);
				System.out.println("---------------------------------------------------------------------");
				counter++;
			}
		}
		System.out.println("总共" + counter + "棵树");
		System.out.println("总共" + count + "节点");
		System.out.println("总共耗时" + (double) (System.currentTimeMillis() - start) / 1000 + "秒");

		/* 树转化成JSON对象，并写入到文件 */
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		obj.put("name", keyword);
		for (int i = 0; i < size; i++) {
			SourceTreeNode tree = trees.get(i);
			if (flags.get(i).booleanValue() && tree.getChildren().size() >= 0) {
				array.add(tree.wholeTreeToJSON());
			}
		}
		obj.put("children", array);
		System.out.println("写入到json文件中...");
		try {
			Writer out = new PrintWriter(new File("E:/jee_workspace/final/WebContent/flare.json"), "utf-8");
			out.write(obj.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("写入完毕");
	}

	private static boolean isParent(SourceTreeNode parent, SourceTreeNode child) {
		return nearlySame(parent, child);
	}

	private boolean isChild(SourceTreeNode child, SourceTreeNode parent) {
		return nearlySame(parent, child);
	}

	private static boolean nearlySame(SourceTreeNode parent, SourceTreeNode child) {
		String childSource = child.getElement().getSource();
		String parentSite = parent.getElement().getSite();
		if (childSource == null && "".equals(childSource))
			return false;
		if (parentSite == null && "".equals(parentSite))
			return false;
		if (childSource.equals(parentSite)) {
			if (smatrix.get(parent.getElement(), child.getElement()) >= 0.9){
				System.out.println("调用nearlySame"+parent.getElement().getId()+":"+child.getElement().getId());
				return true;
			}
		}
		
		return false;
	}

}
