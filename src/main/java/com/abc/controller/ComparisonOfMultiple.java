package com.abc.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.abc.db.dao.NewsDao;
import com.abc.db.entity.News;
import com.abc.vsm.Vsm;

public class ComparisonOfMultiple {

	public static void main(String[] args) {
		test5("傅艺伟");
	}

	/**
	 * divide to mutiple groups according to news' content
	 */
	public static void test1() {
		long start = System.currentTimeMillis();
		List<News> newsList = NewsDao.getNewsList();
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
		List<News> newsList = NewsDao.getNewsList();
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
		List<News> newsList = NewsDao.getNewsList();
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
		List<News> newsList = NewsDao.getNewsList();
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
	 * only output news' list of which search word is appointed and size is more than 1
	 */
	public static void test5(String keyword) {
		long start = System.currentTimeMillis();
		List<News> newsList = NewsDao.getNewsListBySearchWords(keyword);
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
	 * only output news' list of which search word is appointed
	 */
	public static void test6(String keyword) {
		long start = System.currentTimeMillis();
		List<News> newsList = NewsDao.getNewsListBySearchWords(keyword);
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

}
