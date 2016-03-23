package com.abc.db.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.abc.db.DBHelper;
import com.abc.db.entity.Site;
import com.mysql.jdbc.PreparedStatement;

public class SiteDao {
	public boolean add(Site site) {
		String sql = "insert into news_site(name,domain) values (?,?)";
		PreparedStatement stat = null;
		Connection con = null;
		boolean result = false;
		con = DBHelper.getConnection();
		try {
			stat = (PreparedStatement) con.prepareStatement(sql);
			stat.setString(1, site.getName());
			stat.setString(2, site.getDomain());
			result = stat.executeUpdate() > 0 ? true : false;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return result;
	}

	public boolean isSiteExist(String sitename) {
		String sql = "select * from news_site where name = ?";
		PreparedStatement stat = null;
		Connection con = null;
		boolean result = false;
		try {
			con = DBHelper.getConnection();
			stat = (PreparedStatement) con.prepareStatement(sql);
			stat.setString(1, sitename);
			ResultSet rs = stat.executeQuery();
			if (rs.next()){
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return result;
	}

}
