package com.abc.db.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.DBHelper;
import com.abc.db.entity.NewsInfo;
import com.mysql.jdbc.PreparedStatement;

/**
 * A collection of methods for extract video informations.
 *
 */
public class NewsInfoDao {
	public static final Logger LOG = LoggerFactory.getLogger(NewsInfoDao.class);
	
	public static List<NewsInfo> getNewsList() {
		List<NewsInfo> newsList = new ArrayList<NewsInfo>();
		Connection con = null;
		Statement stat = null;
		try {
			con = DBHelper.getConnection();
			stat = con.createStatement();
			String sql = "select * from news_search_data";
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				NewsInfo news = new NewsInfo();
				news.setContent(rs.getString("CONTENT"));
				news.setRawContent(rs.getString("CONTENT_HTML"));
				news.setFetchtime(rs.getString("FETCH_TIME"));
				news.setKeywords(rs.getString("KEYWORDS"));
				news.setPubtime(rs.getString("PUBLISH_TIME"));
				news.setSearchWords(rs.getString("SEARCH_WORDS"));
				news.setSite(rs.getString("SITE"));
				news.setSource(rs.getString("SOURCE"));
				news.setTitle(rs.getString("TITLE"));
				news.setUrl(rs.getString("URL"));
				newsList.add(news);
			}
		} catch (SQLException e) {
			System.out.println("无法创建statement");
			e.printStackTrace();
		} finally {
			if (stat != null)
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


	public static List<NewsInfo> getNewsListBySearchWords(String keyword) {
		List<NewsInfo> newsList = new ArrayList<NewsInfo>();
		Connection con = null;
		PreparedStatement stat = null;
		try {
			con = DBHelper.getConnection();
			stat = (PreparedStatement) con.prepareStatement("select * from news_search_data where search_words = ?");
			stat.setString(1, keyword);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				NewsInfo news = new NewsInfo();
				news.setContent(rs.getString("CONTENT"));
				news.setRawContent(rs.getString("CONTENT_HTML"));
				news.setFetchtime(rs.getString("FETCH_TIME"));
				news.setKeywords(rs.getString("KEYWORDS"));
				news.setPubtime(rs.getString("PUBLISH_TIME"));
				news.setSearchWords(rs.getString("SEARCH_WORDS"));
				news.setSite(rs.getString("SITE"));
				news.setSource(rs.getString("SOURCE"));
				news.setTitle(rs.getString("TITLE"));
				news.setUrl(rs.getString("URL"));
				newsList.add(news);
			}
		} catch (SQLException e) {
			System.out.println("无法创建statement");
			e.printStackTrace();
		} finally {
			if (stat != null)
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

	public static boolean addNews(NewsInfo news) {
		String sql = "insert into  news_search_data(URL,SITE,SOURCE,TITLE,PUBLISH_TIME,CONTENT,KEYWORDS,FETCH_TIME,SEARCH_WORDS) values (?,?,?,?,?,?,?,?,?)";
		PreparedStatement stat = null;
		Connection con = null;
		boolean result = false;
		con = DBHelper.getConnection();
		try {
			stat = (PreparedStatement) con.prepareStatement(sql);
			stat.setString(1, news.getUrl());
			stat.setString(2, news.getSite());
			stat.setString(3, news.getSource());
			stat.setString(4, news.getTitle());
			stat.setString(5, news.getPubtime());
			stat.setString(6, news.getContent());
			stat.setString(7, news.getKeywords());
			stat.setString(8, news.getFetchtime());
			stat.setString(9, news.getSearchWords());
			result = stat.executeUpdate()>0?true:false;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
	
	public static boolean isExist(String url){
		String sql = "select * from news_search_data where URL = ?";
		PreparedStatement stat = null;
		Connection con = null;
		boolean result = false;
		con = DBHelper.getConnection();
		try {
			stat = (PreparedStatement) con.prepareStatement(sql);
			stat.setString(1, url);
			if(stat.executeQuery().next())
				result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
	
}
