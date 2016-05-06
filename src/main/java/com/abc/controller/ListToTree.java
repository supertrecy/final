package com.abc.controller;

import java.util.LinkedList;
import java.util.List;

import com.abc.cluster.Cluster;
import com.abc.cluster.SimilarityContext;
import com.abc.db.entity.NewsInfo;
import com.abc.source.SourceTreeNode2;
import com.abc.util.Util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ListToTree {

	public static JSONObject clusterToTree(Cluster cluster) {
		List<SourceTreeNode2> queue = new LinkedList<>();
		List<NewsInfo> list = cluster.getPoints();
		for (NewsInfo newsInfo : list) {
			String source = newsInfo.getSource();
			SourceTreeNode2 node = new SourceTreeNode2(newsInfo);
			// 如果新闻没有source，只能作为一棵树的根节点
			if (source == null || "".equals(source) || "网络".equals(source)) {
				queue.add(node);
				continue;
			}

			boolean isHandled = false;
			for (SourceTreeNode2 tree : queue) {
				// 如果文章为某棵树的根节点
				if (SimilarityContext.sourceMatchAndInsert(node, tree)) {
					isHandled = true;
					break;
				}
				// 如果文章能插入到某棵树
				else if (tree.insert(node)) {
					isHandled = true;
					break;
				}
			}

			// 如果没能放入已存在的树中，就新建一棵
			boolean isOriginal = Util.isOriginal(newsInfo);
			if (!isHandled) {
				if (isOriginal) {
//					node.getElement().setSource(newsInfo.getSite());
					queue.add(node);
					continue;
				} else {
					SourceTreeNode2 root = new SourceTreeNode2(source);
					root.addChild(node);
					queue.add(root);
				}
			}

		}
		
		
		/* 把可以连接的树连接起来 */
		List<Boolean> validFlags = new LinkedList<>();
		int size = queue.size();
		for (int i = 0; i < size; i++)
			validFlags.add(true);
		List<SourceTreeNode2> newQueue = new LinkedList<>(queue);
		for (int i = 0; i < size - 1 && validFlags.get(i); i++) {
			for (int j = i + 1; j < size && validFlags.get(j); j++) {
				SourceTreeNode2 nodeI = queue.get(i);
				SourceTreeNode2 nodeJ = queue.get(j);
				if (nodeI.insert(nodeJ)) {
					validFlags.set(j, false);
					newQueue.remove(nodeJ);
					continue;
				}
				if (nodeJ.insert(nodeI)) {
					validFlags.set(i, false);
					newQueue.remove(nodeI);
					break;
				}
			}
		}
		queue = newQueue;

		// tree to json format
		if (queue.size() > 1) {
			JSONObject obj = new JSONObject();
			JSONArray array = new JSONArray();
			obj.put("name", "");
			for (SourceTreeNode2 tree : queue) {
				array.add(tree.wholeTreeToJSON());
			}
			obj.put("children", array);
			return obj;
		} else {
			return queue.get(0).wholeTreeToJSON();
		}
	}

}
