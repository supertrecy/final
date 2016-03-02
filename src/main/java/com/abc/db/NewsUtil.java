package com.abc.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.PreparedStatement;

public class NewsUtil {
	public static List<News> getNewsList(){
		List<News> newsList = new ArrayList<News>();
		Connection con = null;
		Statement stat = null;
		try {
			con = DBHelper.getConnection();
			stat = con.createStatement();
			String sql = "select * from news_search_data";
			ResultSet rs= stat.executeQuery(sql);
			while(rs.next()){
				News news = new News();
				news.setId(rs.getInt("ID"));
				news.setContent(rs.getString("CONTENT"));
				news.setContent_html(rs.getString("CONTENT_HTML"));
				news.setFetch_time(rs.getString("FETCH_TIME"));
				news.setKeywords(rs.getString("KEYWORDS"));
				news.setMd5(rs.getString("MD5"));
				news.setPublic_opinion_machine(rs.getString("PUBLIC_OPINION_MACHINE"));
				news.setPublish_time(rs.getString("PUBLISH_TIME"));
				news.setSearch_words(rs.getString("SEARCH_WORDS"));
				news.setSite(rs.getString("SITE"));
				news.setSource(rs.getString("SOURCE"));
				news.setTitle(rs.getString("TITLE"));
				news.setUrl(rs.getString("URL"));
				newsList.add(news);
			}
		} catch (SQLException e) {
			System.out.println("无法创建statement");
			e.printStackTrace();
		}finally{
			if(stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					System.out.println("数据库statement无法关闭");
					e.printStackTrace();
				}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					System.out.println("数据库connection无法关闭");
					e.printStackTrace();
				}
			}
		}
		return newsList;
	}
	
	public static Map<News,Boolean> getNewsMap(){
		Map<News,Boolean> newsMapList = new HashMap<News, Boolean>();
		Connection con = null;
		Statement stat = null;
		try {
			con = DBHelper.getConnection();
			stat = con.createStatement();
			String sql = "select * from news_search_data";
			ResultSet rs= stat.executeQuery(sql);
			while(rs.next()){
				News news = new News();
				news.setId(rs.getInt("ID"));
				news.setContent(rs.getString("CONTENT"));
				news.setContent_html(rs.getString("CONTENT_HTML"));
				news.setFetch_time(rs.getString("FETCH_TIME"));
				news.setKeywords(rs.getString("KEYWORDS"));
				news.setMd5(rs.getString("MD5"));
				news.setPublic_opinion_machine(rs.getString("PUBLIC_OPTION_MACHINE"));
				news.setPublish_time(rs.getString("PUBLISH_TIME"));
				news.setSearch_words(rs.getString("SEARCH_WORDS"));
				news.setSite(rs.getString("SITE"));
				news.setSource(rs.getString("SOURCE"));
				news.setTitle(rs.getString("TITLE"));
				news.setUrl(rs.getString("URL"));
				newsMapList.put(news, true);
			}
		} catch (SQLException e) {
			System.out.println("无法创建statement");
			e.printStackTrace();
		}finally{
			if(stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					System.out.println("数据库statement无法关闭");
					e.printStackTrace();
				}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					System.out.println("数据库connection无法关闭");
					e.printStackTrace();
				}
			}
		}
		return newsMapList;
	}

	public static List<News> getNewsListBySearchWords(String keyword) {
		List<News> newsList = new ArrayList<News>();
		Connection con = null;
		PreparedStatement stat = null;
		try {
			con = DBHelper.getConnection();
			stat = (PreparedStatement) con.prepareStatement("select * from news_search_data where search_words = ?");
			stat.setString(1, keyword);
			ResultSet rs= stat.executeQuery();
			while(rs.next()){
				News news = new News();
				news.setId(rs.getInt("ID"));
				news.setContent(rs.getString("CONTENT"));
				news.setContent_html(rs.getString("CONTENT_HTML"));
				news.setFetch_time(rs.getString("FETCH_TIME"));
				news.setKeywords(rs.getString("KEYWORDS"));
				news.setMd5(rs.getString("MD5"));
				news.setPublic_opinion_machine(rs.getString("PUBLIC_OPINION_MACHINE"));
				news.setPublish_time(rs.getString("PUBLISH_TIME"));
				news.setSearch_words(rs.getString("SEARCH_WORDS"));
				news.setSite(rs.getString("SITE"));
				news.setSource(rs.getString("SOURCE"));
				news.setTitle(rs.getString("TITLE"));
				news.setUrl(rs.getString("URL"));
				newsList.add(news);
			}
		} catch (SQLException e) {
			System.out.println("无法创建statement");
			e.printStackTrace();
		}finally{
			if(stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					System.out.println("数据库statement无法关闭");
					e.printStackTrace();
				}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					System.out.println("数据库connection无法关闭");
					e.printStackTrace();
				}
			}
		}
		return newsList;
	}
}
