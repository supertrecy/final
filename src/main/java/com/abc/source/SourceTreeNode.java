package com.abc.source;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.abc.db.dao.NewsInfoDao;
import com.abc.db.entity.NewsInfo;

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
	
	public void addChild(SourceTreeNode child){
		children.add(child);
	}
	public void addChildren( List<SourceTreeNode> children){
		this.children.addAll(children);
	}
	
	public void travelPrint(int level){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append(" ");
		}
		String seg = sb.toString();
		System.out.println(seg+element.getUrl());
		int nextLevel = level+1;
		for (SourceTreeNode child : children) {
			child.travelPrint(nextLevel);
		}
	}
	
	public boolean insert(NewsInfo newInfo){
		String url = element.getUrl();
		String sourceUrl = newInfo.getSourceUrl();
		if(url.equals(sourceUrl)){
			SourceTreeNode node = new SourceTreeNode(newInfo);
			this.addChild(node);
			return true;
		}else{
			for (SourceTreeNode child : children) {
				if(child.insert(newInfo))
					return true;
			}
		}
		return false;
	}
	/**
	 * for test
	 * @param args
	 */
	public static void main(String[] args) {
		List<NewsInfo> newsList = NewsInfoDao.getNewsListBySearchWords("女子酒店遇袭;");
		List<NewsInfo> news = new LinkedList<>();
		List<NewsInfo> nonews = new LinkedList<>();
		for (NewsInfo newsInfo : newsList) {
			String source_url = newsInfo.getSourceUrl();
			if (source_url != null && !source_url.equals("")) {
				if (isNewsUrl(source_url))
					news.add(newsInfo);
				else
					nonews.add(newsInfo);
			}

		}

		Map<String, List<NewsInfo>> map = new HashMap<>();
		for (NewsInfo newsInfo : news) {
			String source_url = newsInfo.getSourceUrl();
			List<NewsInfo> list = map.get(source_url);
			if (list == null) {
				list = new LinkedList<>();
			}
			list.add(newsInfo);
			map.put(source_url, list);
		}
		System.out.println("source url相同的新闻有" + map.size() + "簇");
		System.out.println("其他新闻有" + nonews.size() + "簇");
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
}
