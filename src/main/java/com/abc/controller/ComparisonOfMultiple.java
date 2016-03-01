package com.abc.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.abc.db.News;
import com.abc.db.NewsUtil;
import com.abc.db.TreeNode;
import com.abc.vsm.Vsm;

public class ComparisonOfMultiple {

	public static void main(String[] args) {
		test4();
	}

	/**
	 * divide to mutiple groups according to news' content
	 */
	public static void test1() {
		long start = System.currentTimeMillis();
		List<News> newsList = NewsUtil.getNewsList();
		int size = newsList.size();
		List<Boolean> tagList = new ArrayList<Boolean>();
		for (int i = 0; i < size; i++) {
			tagList.add(new Boolean(true));
		}

		for (int i = 0; i < size - 1; i++) {
			if (tagList.get(i)) {
				int j = i + 1;
				News news1 = newsList.get(i);
				System.out.println(news1.toString());
				for (; j < size; j++) {
					if (tagList.get(j)) {
						News news2 = newsList.get(j);
						if (Vsm.compareTwo(news1.getContent(), news2.getContent()) == 1) {
							System.out.println(news2.toString());
							tagList.set(j, new Boolean(false));
						}
					} else {
						continue;
					}
				}
				System.out.println("----------------------------------------------------------------");
				tagList.set(i, new Boolean(false));
			} else {
				continue;
			}
		}
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
	}

	/**
	 * divide to mutiple groups according to news' title
	 */
	public static void test2() {
		long start = System.currentTimeMillis();
		List<News> newsList = NewsUtil.getNewsList();
		int size = newsList.size();
		List<Boolean> tagList = new ArrayList<Boolean>();
		for (int i = 0; i < size; i++) {
			tagList.add(new Boolean(true));
		}

		for (int i = 0; i < size - 1; i++) {
			if (tagList.get(i)) {
				int j = i + 1;
				News news1 = newsList.get(i);
				System.out.println(news1.toString());
				for (; j < size; j++) {
					if (tagList.get(j)) {
						News news2 = newsList.get(j);
						if (Vsm.compareTwo(news1.getTitle(), news2.getTitle()) == 1) {
							System.out.println(news2.toString());
							tagList.set(j, new Boolean(false));
						}
					} else {
						continue;
					}
				}
				System.out.println("----------------------------------------------------------------");
				tagList.set(i, new Boolean(false));
			} else {
				continue;
			}
		}
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
	}

	/**
	 * test function Vsm.compareMutiple()
	 */
	public static void test3() {
		long start = System.currentTimeMillis();
		List<News> newsList = NewsUtil.getNewsList();
		List<List<News>> newsGroup = Vsm.compareMutiple(newsList);
		for (Iterator iterator = newsGroup.iterator(); iterator.hasNext();) {
			List<News> news = (List<News>) iterator.next();
			for (News news2 : news) {
				System.out.println(news2.toString());
			}
			System.out.println("---------------------------------------------------------------");
		}
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
	}

	/**
	 * only output news' list of which size is more than 1
	 */
	public static void test4() {
		long start = System.currentTimeMillis();
		List<News> newsList = NewsUtil.getNewsList();
		List<List<News>> newsGroup = Vsm.compareMutiple(newsList);
		for (Iterator iterator = newsGroup.iterator(); iterator.hasNext();) {
			List<News> news = (List<News>) iterator.next();
			if (news.size() > 1) {
				for (News news2 : news) {
					System.out.println(news2.toString());
				}
				System.out.println("---------------------------------------------------------------");
			}
		}
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
	}

	/**
	 * exchange those lists of which size is more than 1 to trees and output
	 * tree's json format
	 */
	public static void test5() {
		long start = System.currentTimeMillis();
		List<News> newsList = NewsUtil.getNewsList();
		List<List<News>> newsGroup = Vsm.compareMutiple(newsList);
		for (Iterator iterator = newsGroup.iterator(); iterator.hasNext();) {
			List<News> news = (List<News>) iterator.next();
			if (news.size() > 1) {
				new ComparisonOfMultiple().listToTree(news);
			}
		}
		double time = (double) (System.currentTimeMillis() - start) / 1000;
		System.out.println("总共" + newsList.size() + "篇文章");
		System.out.println("总耗时" + time + "秒");
	}

	private void listToTree(List<News> list) { // TODO change String to other object,such as TempDataStructure
		List<TreeNode<String>> queue = new LinkedList<TreeNode<String>>();
		for (News news : list) {
			String source = news.getSource();
			String site = news.getSite();
			if (source == null) {
				continue; // TODO how to resolve the problem that source is null
			} else if (site == null) {
				continue; // TODO how to resolve the problem that site is null
			} else {
				// insert node to queue's tree
				insertNode(source, site, queue);
			}
		}
	}

	private void insertNode(String source, String site, List<TreeNode<String>> queue) {
		TreeNode<String> parent;
		TreeNode<String> child;
		if ((parent = findNode(source, queue)) != null) {
			parent.addChild(new TreeNode<String>(site));
		} else if ((child = findNode(site, queue)) != null) {
			TreeNode<String> newNode = new TreeNode<String>(site);
			newNode.addChild(child);
			queue.remove(child);
			queue.add(newNode);
		} else {
			TreeNode<String> newNode = new TreeNode<String>(source);
			newNode.addChild(new TreeNode<String>(site));
			queue.add(newNode);
		}
	}

	private TreeNode<String> findNode(String element, List<TreeNode<String>> queue) {
		if (queue.size() != 0) {
			for (TreeNode<String> root : queue) {
				TreeNode<String> node =  root.traverseCompare(element);
				if(node != null)
					return node;
					
			}
		}
		return null;
	}
	/*
	 * private void addNewNodeToQueue(String source, String site,
	 * List<TreeNode<String>> queue) { TreeNode<String> newNode = new
	 * TreeNode<String>(source); newNode.addChild(new TreeNode<String>(site));
	 * queue.add(newNode); }
	 */
}
