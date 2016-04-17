package com.abc.cluster;

import java.util.List;

import com.abc.db.entity.NewsInfo;
import com.abc.source.SourceTreeNode;
import com.abc.source.SourceTreeNode2;
import com.abc.util.URLUtil;

public class SimilarityContext {

	private static SimilarityRealTimeMatrix smatrix;

	public SimilarityContext(List<NewsInfo> newsList) {
		SimilarityContext.smatrix = new SimilarityRealTimeMatrix(newsList);
	}

	public static boolean nearlySameNode(SourceTreeNode parent, SourceTreeNode child) {
		return isSourceMatch(parent, child);
	}

	public static boolean sourceMatchAndInsert(SourceTreeNode2 parent, SourceTreeNode2 child) {
		boolean childIsNews = child.isNews();
		boolean parentIsNews = parent.isNews();
		boolean result = false;

		if (!childIsNews && parentIsNews) {
			/* 只有一种可能,parent是child的新闻实体，两者不可能是父子节点关系 */
			String site = child.getSite();
			if (isSameNode(site, parent.getElement())) {
				child.setSite(null);
				child.setElement(parent.getElement());
				child.addChildren(parent.getChildren());
				return true;
			}

		} else if (childIsNews && !parentIsNews) {
			String childSource = child.getElement().getSource();
			String parentSite = parent.getSite();
			String childSourceDomain = URLUtil.getDomainName(child.getElement().getSourceUrl());
			
			/** child是parent的新闻实体*/
			if (isSameNode(parentSite, child.getElement())) {
				parent.setSite(null);
				parent.setElement(child.getElement());
				parent.addChildren(child.getChildren());
				return true;
			}
			
			/**child是parent可能是父子节点关系 */
			/* parentSite和childSource都是中文或者域名 */
			if(childSource.equals(""))
				return false;
			if (childSource.equals(parentSite) || childSource.contains(parentSite)
					|| parentSite.contains(childSource)) {
				result = true;
			}
			/** parentSite是域名，childSource是中文 */
			else if (parentSite.equals(childSourceDomain))
				result = true;

			if (result) {
				parent.addChild(child);
				return true;
			}

		} else if (childIsNews && parentIsNews) {
			return isSourceMatch(parent, child);
		}

		return false;
	}
	
	private static boolean isSameNode(String site,NewsInfo news){
		String newsSite = news.getSite();
		String nodeSiteDomain = URLUtil.getDomainName(news.getUrl());
		/* site和newsSite都是中文或者域名 */
		if (newsSite.equals(site) || newsSite.contains(site) || site.contains(newsSite)) {
			return true;
		}
		/* site是域名，nodeSite是中文 */
		else if (site.equals(nodeSiteDomain)) {
			return true;
		}

		return false;
	}

	public SimilarityRealTimeMatrix matrix() {
		return smatrix;
	}

	public static boolean isSourceMatch(SourceTreeNode parent, SourceTreeNode child) {
		String childSource = child.getElement().getSource();
		String parentSite = parent.getElement().getSite();
		if (childSource == null || "".equals(childSource))
			return false;
		if (parentSite == null || "".equals(parentSite))
			return false;

		if (smatrix.get(parent.getElement(), child.getElement()) >= 0.9) {
			/* parentSite和childSource都是中文或者域名 */
			if (childSource.equals(parentSite) || childSource.contains(parentSite)
					|| parentSite.contains(childSource)) {
				return true;
			}
			/* childSource是域名，parentSite是中文 */
			String parentSiteDomain = URLUtil.getDomainName(parent.getElement().getUrl());
			if (childSource.equals(parentSiteDomain))
				return true;
			/* childSource是中文，parentSite是域名 */
			String childSourceDomain = URLUtil.getDomainName(child.getElement().getSourceUrl());
			if (parentSite.equals(childSourceDomain))
				return true;
		}
		return false;
	}

	private static boolean isSourceMatch(SourceTreeNode2 parent, SourceTreeNode2 child) {
		String childSource = child.getElement().getSource();
		String parentSite = parent.getElement().getSite();
		boolean result = false;

		if (childSource == null || "".equals(childSource))
			return false;
		if (parentSite == null || "".equals(parentSite))
			return false;

		String parentSiteDomain = URLUtil.getDomainName(parent.getElement().getUrl());
		String childSourceDomain = URLUtil.getDomainName(child.getElement().getSourceUrl());
		/* parentSite和childSource都是中文或者域名 */
		if (childSource.equals(parentSite) || childSource.contains(parentSite) || parentSite.contains(childSource))
			result = true;
		/* childSource是域名，parentSite是中文 */
		else if (childSource.equals(parentSiteDomain))
			result = true;
		/* childSource是中文，parentSite是域名 */
		else if (parentSite.equals(childSourceDomain))
			result = true;

		if (result) {
			parent.addChild(child);
			return true;
		}

		return false;
	}

}
