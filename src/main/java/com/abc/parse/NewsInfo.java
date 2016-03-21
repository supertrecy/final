package com.abc.parse;

public class NewsInfo  {
	public static final String DIR_NAME = "news_info";
	
	private String url = ""; 			// 新闻url
	private String type = "";           // 网页类型(news or forum)
	private String site = ""; 			// 新闻站点
	private String plate = "";			// 所属板块
	private String title = ""; 			// 新闻标题
	private String pubtime = "";		// 发布时间,2013-02-27 11:27
	private String fetchtime = "";      // 新闻抓取时间
	private String updatetime = "";     // 新闻更新时间
	private String keywords = "";		// 关键词
	private String source = "";			// 新闻来源
	private int commentTotal = 0;		// 评论参与人数
	private int commentShow = 0;		// 评论条数
	private String newsId = "";			// 新闻id
	private String lastUpdateTime = ""; // 新闻的最后一次更新时间
	private String content = ""; 		// 正文
	private String rawcontent = ""; 	// 原始网页
	private String comment = ""; 		// 评论信息
	
	public NewsInfo() {}
	
	public String getType() { return type; }
	
	public String getUrl() { return url; }
	
	public String getSite() { return site; }
	
	public String getPlate() { return plate; }
	
	public String getTitle() { return title; }
	
	public String getPubtime() { return pubtime; }
	
	public String getFetchtime() { return fetchtime; }
	
	public String getUpdatetime() { return updatetime; }
	
	public String getKeywords() { return keywords; }
	
	public String getSource() { return source; }
	
	public int getCommentTotal() { return commentTotal; }
	
	public int getCommentShow() { return commentShow; }
	
	public String getContent() { return content; }
	
	public String getRawContent() { return rawcontent; }
	
	public String getComment() { return comment; }
	
	public String getNewsId() { return newsId; }
	
	public void setType(String type) { this.type = type; }
	
	public void setUrl(String url) { this.url = url; }
	
	public void setPubtime(String pubtime) { this.pubtime = pubtime; }
	
	public void setFetchtime(String fetchtime) { this.fetchtime = fetchtime; }
	
	public void setUpdatetime(String updatetime) { this.updatetime = updatetime; }
	
	public void setCommentTotal(int commentTotal) { this.commentTotal = commentTotal; }
	
	public void setCommentShow(int commentShow) { this.commentShow = commentShow; }
	
	public void setContent(String content) { this.content = content; }
	
	public void setRawContent(String rawcontent) { this.rawcontent = rawcontent; }
	
	public void setComment(String comment) { this.comment = comment; }
	
	public void setNewsId(String newsId) { this.newsId = newsId; }
	
	public void setLastUpdateTime(String time) { this.lastUpdateTime = time; }
	
	public void setSource(String source) {this.source = source;}

	public void setBaseInfo(String site, String plate, String title, String pubtime, 
			String keywords, String source) {
		this.site = site;
		this.plate = plate;
		this.title = title;
		this.pubtime = pubtime;
		this.keywords = keywords;
		this.source = source;
	}
	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof NewsInfo))
	      return false;
	    NewsInfo other = (NewsInfo)o;
	    return this.title.equals(other.title) && this.url.equals(other.url);
	}
	
	@Override
	public String toString() {
		return "url: " + this.url + "\n" +
			   "site: " + this.site + "\n" +
			   "plate: " + this.plate + "\n" +
			   "title: " + this.title + "\n" +
			   "type: " + this.type + "\n" +
			   "pubtime: " + this.pubtime + "\n" +
			   "keywords: " + this.keywords + "\n" +
			   "source: " + this.source + "\n" +
			   "commentTotal: " + this.commentTotal + "\n" +
			   "commentShow: " + this.commentShow + "\n" +
			   "content: " + this.content + "\n" +
			   "fetchtime: " + this.fetchtime + "\n";
	}
	
	public static void main(String[] args) {	
	}
}
