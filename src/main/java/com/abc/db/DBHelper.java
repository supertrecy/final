package com.abc.db;

import java.sql.*;

public class DBHelper {

	private static Connection con = null;

	private final static String DB_URL = "jdbc:mysql://localhost:3306/test?user=tester&password=123456&useUnicode=true&characterEncoding=utf-8&useSSL=false";

	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(DB_URL);
		} catch (ClassNotFoundException e) {
			System.out.println("没有com.mysql.jdbc.Driver类");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("获取连接失败，无法连接到数据库");
			e.printStackTrace();
		}
		return con;
	}

}
