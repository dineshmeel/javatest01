package com.dk.niku.startup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("testing")
public class MyResource {

	@GET
	@Path("phase1")
	public String letsTest() {
		return "Hello";
	}

	@GET
	@Path("getEmps")
	public String getEmps() {
		Statement stmt;
		String result = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mypro", "root", "root");
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("select * from emps");
			while (rs.next()) {
				result = result + "Id:" + rs.getString(1) + " Name:" + rs.getString(2) + " Age:" + rs.getString(3)
						+ "\n";
				System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result = "SQLException";
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result = "ClassNotFoundException";
		}
		return result;
	}

	@POST
	@Path("getEmpsById")
	public String getEmpsPost(@FormParam("id") int id) {
		String result = "";
		try {
			String sql = "select * from emps where id=?";
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mypro", "root", "root");
			PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result = result + "Id:" + rs.getString(1) + " Name:" + rs.getString(2) + " Age:" + rs.getString(3)
						+ "\n";
				System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			result = "SQLException";
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result = "ClassNotFoundException";
		}
		return result;
	}

	@GET
	@Path("addEmps")
	public int addEmps() {
		int result;
		try {
			String sql = "INSERT INTO emps(id,name,age) " + "VALUES(?,?,?)";
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mypro", "root", "root");
			PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, 3);
			pstmt.setString(2, "someone");
			pstmt.setInt(3, 23);
			result = pstmt.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -1;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -2;
		}
		return result;
	}

	@POST
	@Path("addEmps")
	public int addNewEmps(@FormParam("id") int id, @FormParam("name") String name, @FormParam("age") int age) {
		int result;
		try {
			String sql = "INSERT INTO emps(id,name,age) " + "VALUES(?,?,?)";
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mypro", "root", "root");
			PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, id);
			pstmt.setString(2, name);
			pstmt.setInt(3, age);
			result = pstmt.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -1;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -2;
		}
		return result;
	}

	@GET
	@Path("updateEmps")
	public int updateEmps() {
		int result;
		try {
			String sql = "update emps set name=? where id=?";
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mypro", "root", "root");
			PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, "no-one");
			pstmt.setInt(2, 3);
			result = pstmt.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -1;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -2;
		}
		return result;
	}

	@POST
	@Path("updateEmps")
	public int updateEmpsById(@FormParam("id") int id, @FormParam("name") String name, @FormParam("age") int age) {
		int result;
		try {
			String sql = "update emps set name=?,age=? where id=?";
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mypro", "root", "root");
			PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, name);
			pstmt.setInt(2, age);
			pstmt.setInt(3, id);
			result = pstmt.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -1;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -2;
		}
		return result;
	}
}
