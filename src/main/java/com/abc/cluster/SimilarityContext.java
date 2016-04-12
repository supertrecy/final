package com.abc.cluster;


import java.util.List;

import com.abc.db.entity.NewsInfo;
import com.abc.source.SourceTreeNode;

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
		if (childSource.equals(parentSite)) {
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
