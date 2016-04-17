package com.abc.source;

import java.util.LinkedList;
import java.util.List;

import com.abc.cluster.SimilarityContext;
import com.abc.db.entity.NewsInfo;
import com.abc.util.Util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SourceTreeNode2 {
	private NewsInfo element;
	private List<SourceTreeNode2> children;
	private String site;

	public SourceTreeNode2(NewsInfo element) {

		this.site = null;
		this.element = element;
		this.children = new LinkedList<>();
	}

	public SourceTreeNode2(String site) {
		this.site = site;
		this.element = null;
		this.children = new LinkedList<>();
	}

	public SourceTreeNode2(NewsInfo element, List<SourceTreeNode2> children) {
		this.site = null;
		this.element = element;
		this.children = children;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setElement(NewsInfo element) {
		this.element = element;
	}

	public int getChildrenNum() {
		return children.size();
	}
	
	public String getSite() {
		return site;
	}

	public NewsInfo getElement() {
		return element;
	}

	public List<SourceTreeNode2> getChildren() {
		return children;
	}

	public void addChild(SourceTreeNode2 child) {
		children.add(child);
	}

	public void addChildren(List<SourceTreeNode2> children) {
		this.children.addAll(children);
	}

	public boolean isNews() {
		if (site != null)
			return false;
		else
			return true;
	}

	public boolean insert(SourceTreeNode2 node) {
		if(SimilarityContext.sourceMatchAndInsert(this, node))
			return true;
		
		// 递归到子树中
		for (SourceTreeNode2 child : children) {
			if (child.insert(node))
				return true;
		}
		return false;
	}

	public JSONObject wholeTreeToJSON() {
		JSONObject root = new JSONObject();
		if(isNews()){
			root = Util.wrapJsonObject(root, element);
		}else{
			root.put("name", site);
		}
		if (getChildrenNum() != 0) {
			JSONArray jsonChildren = new JSONArray();
			for (SourceTreeNode2 child : children) {
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
