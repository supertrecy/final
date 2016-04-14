package com.abc.source;

import java.util.LinkedList;
import java.util.List;

import com.abc.cluster.SimilarityContext;
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

	public int getChildrenNum() {
		return children.size();
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

	public boolean insert(SourceTreeNode node) {
		String url = element.getUrl();
		String sourceUrl = node.getElement().getSourceUrl();
		// 判断能否加入到children中
		if (sourceUrl == null || "".equals(sourceUrl)) {
			// 通过内容判断
			return SimilarityContext.nearlySameNode(this, node);
		} else {
			// 通过url比对
			if (url.equals(sourceUrl)) {
				this.addChild(node);
				return true;
			}
		}
		// 递归到子树中
		for (SourceTreeNode child : children) {
			if (child.insert(node))
				return true;
		}

		return false;
	}

	public JSONObject wholeTreeToJSON() {

		JSONObject root = new JSONObject();
		/*String tip = "";
		if (element.getSource().equals(""))
			tip += "no source,";
		if (element.getSourceUrl().equals(""))
			tip += "no source url,";*/
		root.put("name", element.getSite()+":"+element.getId());
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

}
