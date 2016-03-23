package com.abc.db.entity;

public class Site {

	private String domain;
	private String name;

	public Site() {
	}

	public Site(String domain, String name) {
		super();
		this.domain = domain;
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
