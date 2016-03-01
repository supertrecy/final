package com.abc.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		Map<E, List<TreeNode<E>>> map = new HashMap<E, List<TreeNode<E>>>();
		map.put(element, children);
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
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