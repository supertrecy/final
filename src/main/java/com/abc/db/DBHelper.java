package com.abc.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBHelper {

	private final static String DRIVER_CLASS = "com.mysql.jdbc.Driver";

	private final static String DB_URL = "jdbc:mysql://localhost:3306/test?user=tester&password=123456&useUnicode=true&characterEncoding=utf-8&useSSL=false";

    private static ComboPooledDataSource cpds=new ComboPooledDataSource(true);     
        
    /**  
     * 此处可以不配置，采用默认也行  
     */    
    static{    
        cpds.setJdbcUrl(DB_URL);    
        try {    
            cpds.setDriverClass(DRIVER_CLASS);    
        } catch (PropertyVetoException e) {    
            e.printStackTrace();    
        }    
        cpds.setMaxPoolSize(15);    
        cpds.setMinPoolSize(0);     
        cpds.setMaxIdleTime(60);    
    }    
    
    public static Connection  getConnection(){    
        try {    
            return cpds.getConnection();    
        } catch (SQLException e) {    
        	System.out.println("获取连接失败，无法连接到数据库");
			e.printStackTrace();
        }    
        return null;    
    }    

}
