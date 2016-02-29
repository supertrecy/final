package com.abc.db;

public class News {

	int id;								// 新闻id
	private String url = ""; 			// 新闻url
	private String site = ""; 			// 新闻站点
	private String source = "";			// 新闻来源
	private String title = ""; 			// 新闻标题
	private String publish_time = "";	// 发布时间,2013-02-27 11:27
	private String fetch_time = "";      // 新闻抓取时间
	private String md5 = "";      		// md5
	private String keywords = "";		// 关键词
	private String public_opinion_machine = "";			
	private String content = ""; 		// 正文
	private String content_html = ""; 	// 原始网页
	private String search_words = ""; 	// 搜索所用关键词
	
	public News() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublish_time() {
		return publish_time;
	}

	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}

	public String getFetch_time() {
		return fetch_time;
	}

	public void setFetch_time(String fetch_time) {
		this.fetch_time = fetch_time;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getPublic_opinion_machine() {
		return public_opinion_machine;
	}

	public void setPublic_opinion_machine(String public_opinion_machine) {
		this.public_opinion_machine = public_opinion_machine;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent_html() {
		return content_html;
	}

	public void setContent_html(String content_html) {
		this.content_html = content_html;
	}

	public String getSearch_words() {
		return search_words;
	}

	public void setSearch_words(String search_words) {
		this.search_words = search_words;
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", site=" + site + ", source=" + source
				+ ", title=" + title + ", publish_time=" + publish_time + "]";
	}

}
