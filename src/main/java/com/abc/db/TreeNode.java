package com.abc.db;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TreeNode<E> {

	private E element;

	private List<TreeNode<E>> children;

	public TreeNode() {
		this.children = new ArrayList<TreeNode<E>>();
	}

	public TreeNode(E element) {
		super();
		this.element = element;
		this.children = new ArrayList<TreeNode<E>>();
	}

	public E getElement() {
		return element;
	}

	public void setElement(E element) {
		this.element = element;
	}

	public void addChild(TreeNode<E> e) {
		children.add(e);
	}

	public int getChildrenNum() {
		return children.size();
	}

	public JSONObject wholeTreeToJSON() {

		JSONObject root = new JSONObject();
		root.put("name", element);
		if(getChildrenNum() != 0){
			JSONArray jsonChildren = new JSONArray();
			for (TreeNode<E> child : children) {
				JSONObject jsonChild = child.wholeTreeToJSON();
				jsonChildren.add(jsonChild);
			}
			root.put("children", jsonChildren);
		}else{
			root.put("size",getChildrenNum());
		}
		return root;
	}

	public TreeNode<E> traverseCompare(E element) {
		if (this.element.equals(element)) {
			return this;
		} else {
			for (TreeNode<E> treeNode : children) {
				TreeNode<E> child;
				if ((child = treeNode.traverseCompare(element)) != null) {
					return child;
				}
			}
		}
		return null;
	}

	/*
	 * private void traverse(){
	 * 
	 * }
	 */

}