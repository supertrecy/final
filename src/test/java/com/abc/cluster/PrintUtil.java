package com.abc.cluster;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;

import com.abc.db.entity.NewsInfo;

/**
 * 打印簇的类，并且只打印大小大于1的簇
 * 
 * @author w_w
 *
 */
public class PrintUtil {
	/**
	 * 打印簇
	 * 
	 * @param al
	 */
	public static void printCluster(AGNEST al) {
		List<Cluster> clusters = al.clustering();
		int i = 1;
		for (Cluster cluster : clusters) {
			List<NewsInfo> newslist = cluster.getPoints();
			if (newslist.size() != 1) {
				System.out.println((i++) + ":");
				for (NewsInfo newsInfo : newslist) {
					System.out.println(newsInfo.getId() + ":" + newsInfo.getTitle());
				}
				System.out.println();
			}
		}
	}

	/**
	 * 打印簇，及每个新闻map形式的标签
	 * 
	 * @param al
	 */
	public static void printClusterAndTagMap(AGNEST al) {

		List<Cluster> clusters = al.clustering();
		int i = 1;
		for (Cluster cluster : clusters) {
			List<NewsInfo> newslist = cluster.getPoints();
			if (newslist.size() != 1) {
				System.out.println((i++) + ":");
				for (NewsInfo newsInfo : newslist) {
					System.out.println(newsInfo.getId() + ":" + al.getNewsTagMap(newsInfo));
				}
				System.out.println();
			}
		}
	}

	/**
	 * 打印簇，及每个新闻list形式的标签
	 * 
	 * @param al
	 */
	public static void printClusterAndTagList(AGNEST al) {
		List<Cluster> clusters = al.clustering();
		int i = 1;
		for (Cluster cluster : clusters) {
			List<NewsInfo> newslist = cluster.getPoints();
			if (newslist.size() != 1) {
				System.out.println((i++) + ":");
				for (NewsInfo newsInfo : newslist) {
					System.out.println(newsInfo.getId() + ":" + al.getNewsTagList(newsInfo));
				}
				System.out.println();
			}
		}
	}

	/**
	 * 把簇及每个新闻list形式的标签写入到文件中，每个相似度一个文件，专门针对ImprovedAGNEST2
	 * 
	 * @param al
	 */
	public static void writeClusterAndTagListToFile(String keyword,ImprovedAGNEST2 al, long startTime, double similarity)
			throws IOException {
		DecimalFormat df = new DecimalFormat("#0.00");
		File file = new File("E:\\testfiles\\ImprovedAGNEST2Test\\"+ keyword +"\\" + df.format(similarity) + ".txt");
		if (!file.exists())
			file.createNewFile();
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		List<Cluster> clusters = al.clustering();
		int i = 1;
		int counter = 0;
		for (Cluster cluster : clusters) {
			List<NewsInfo> newslist = cluster.getPoints();
			if (newslist.size() != 1) {
				out.write((i++) + ":");
				out.newLine();
				for (NewsInfo newsInfo : newslist) {
					out.write(newsInfo.getId() + ":" + al.getNewsTagList(newsInfo));
					out.newLine();
				}
				out.newLine();
				counter++;
			}
		}
		out.write("总用时：" + (double) (System.currentTimeMillis() - startTime) / 1000 + "秒，共" + counter + "个簇");
		out.close();
	}

	/**
	 * 把簇及每个新闻list形式的标签写入到文件中，不同簇数目对应不同文件，专门针对ImprovedAGNEST和AGNEST
	 * 
	 * @param al
	 * @param classname
	 */
	public static void writeClusterAndTagListToFile(String keyword,AGNEST al, long startTime, int clusterNum, String classname)
			throws IOException {
		File file = new File("E:\\testfiles\\" + classname + "\\"+ keyword +"\\" + clusterNum + ".txt");
		if (!file.exists())
			file.createNewFile();
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		List<Cluster> clusters = al.clustering();
		int i = 1;
		int counter = 0;
		for (Cluster cluster : clusters) {
			List<NewsInfo> newslist = cluster.getPoints();
			if (newslist.size() != 1) {
				out.write((i++) + ":");
				out.newLine();
				for (NewsInfo newsInfo : newslist) {
					out.write(newsInfo.getId() + ":" + al.getNewsTagList(newsInfo));
					out.newLine();
				}
				out.newLine();
				counter++;
			}
		}
		out.write("总用时：" + (double) (System.currentTimeMillis() - startTime) / 1000 + "秒，共" + counter + "个簇");
		out.close();
	}

}
