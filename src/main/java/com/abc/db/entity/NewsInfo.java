package com.abc.db.entity;

public class NewsInfo  {
	public static final String DIR_NAME = "news_info";
	
	private String url = ""; 			// 新闻url
	private String site = ""; 			// 新闻站点
	private String title = ""; 			// 新闻标题
	private String pubtime = "";		// 发布时间,2013-02-27 11:27
	private String fetchtime = "";      // 新闻抓取时间
	private String keywords = "";		// 关键词
	private String source = "";			// 新闻来源
	private String content = ""; 		// 正文
	private String rawcontent = ""; 	// 原始网页
	private String searchWords = ""; 		// 评论信息
	
	public NewsInfo() {}
	
	public String getUrl() { return url; }
	
	public String getSite() { return site; }
	
	public String getTitle() { return title; }
	
	public String getPubtime() { return pubtime; }
	
	public String getFetchtime() { return fetchtime; }
	
	public String getKeywords() { return keywords; }
	
	public String getSource() { return source; }

	public String getContent() { return content; }
	
	public String getRawContent() { return rawcontent; }
	
	public void setUrl(String url) { this.url = url; }
	
	public void setPubtime(String pubtime) { this.pubtime = pubtime; }
	
	public void setFetchtime(String fetchtime) { this.fetchtime = fetchtime; }
	
	public void setContent(String content) { this.content = content; }
	
	public void setRawContent(String rawcontent) { this.rawcontent = rawcontent; }
	
	public void setSource(String source) {this.source = source;}
	
	public void setSite(String site) {this.site = site;}

	public void setTitle(String title) {this.title = title;}

	public void setKeywords(String keywords) {this.keywords = keywords;}

	public String getSearchWords() {return searchWords;}

	public void setSearchWords(String searchWords) {this.searchWords = searchWords;}

	public void setBaseInfo(String site, String title, String pubtime, 
			String keywords, String source) {
		this.site = site;
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
			   "title: " + this.title + "\n" +
			   "pubtime: " + this.pubtime + "\n" +
			   "keywords: " + this.keywords + "\n" +
			   "source: " + this.source + "\n" +
			   "content: " + this.content + "\n" +
			   "fetchtime: " + this.fetchtime + "\n";
	}
	
	public static void main(String[] args) {	
	}
}
