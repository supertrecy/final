package com.abc.cluster;


import java.util.List;

import com.abc.db.entity.NewsInfo;
import com.abc.source.SourceTreeNode;
import com.abc.source.SourceTreeNode2;

public class SimilarityContext {
	
	private static SimilarityRealTimeMatrix smatrix;
	
	public SimilarityContext(List<NewsInfo> newsList){
		SimilarityContext.smatrix  = new SimilarityRealTimeMatrix(newsList);
	}
	
	public static boolean nearlySameNode(SourceTreeNode parent, SourceTreeNode child) {
		String childSource = child.getElement().getSource();
		String parentSite = parent.getElement().getSite();
		if (childSource == null && "".equals(childSource))
			return false;
		if (parentSite == null && "".equals(parentSite))
			return false;
		if (childSource.equals(parentSite)||childSource.contains(parentSite)||parentSite.contains(childSource)) {
			if (smatrix.get(parent.getElement(), child.getElement()) >= 0.9) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean nearlySameNode(SourceTreeNode2 parent, SourceTreeNode2 child) {
		if(!child.isNews()){
			String site = child.getSite();
			String parentSite = parent.getElement().getSite();
			if(site.equals(parentSite)||site.contains(parentSite)||parentSite.contains(site)){
				if(parentSite.contains(site))
					parent.getElement().setSource(site);
				child.setElement(parent.getElement());
				child.setSite(null);
				child.addChildren(parent.getChildren());
				return true;
			}else
				return false;
		}
		String childSource = child.getElement().getSource();
		String parentSite = parent.getElement().getSite();
		if (childSource == null && "".equals(childSource))
			return false;
		if (parentSite == null && "".equals(parentSite))
			return false;
		if (childSource.equals(parentSite)||childSource.contains(parentSite)||parentSite.contains(childSource)) {
			if (smatrix.get(parent.getElement(), child.getElement()) >= 0.9) {
				return true;
			}
		}

		return false;
	}
	
	public SimilarityRealTimeMatrix matrix(){
		return smatrix;
	}
}
