package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import Configuration.Configuration;

public class DBConnection {
	
	public Connection getConnection(){
		String url=Configuration.readValue("mysqlurl");
		//String url="jdbc:mysql://172.20.46.37:3306/test?useUnicode=true&characterEncoding=utf8";
		String dbuser=Configuration.readValue("username");
		String dbpass=Configuration.readValue("password");
		//String dbpass="";
		String name="com.mysql.jdbc.Driver";
		Connection con = null;
		if(con==null){
			try{
				Class.forName(name).newInstance();

			}catch(Exception e){
				System.out.println(e);
			}
			try{
				con=DriverManager.getConnection(url,dbuser,dbpass);
			}catch(Exception e){}
		}
		return con;
	}

	public ResultSet query(String sql){
		ResultSet rs = null;
		try{
			Connection con = getConnection();
			if(con==null){
				throw new Exception("连接西败！");
			}
			Statement stmt = con.createStatement();
			rs=stmt.executeQuery(sql);
		}catch(Exception e){}
		return rs;
	}
	
	
}

