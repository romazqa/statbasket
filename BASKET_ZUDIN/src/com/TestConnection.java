package com;
import java.sql.*; 
public class TestConnection {
	  public static void main(String[] args) {
	    Connection con = null;
	    Statement stmt = null;
	    ResultSet res = null;
	    String url = 
	       "jdbc:postgresql://localhost:5432/Svoboda";
	    String user = "admin";
	    String password = "parol";
	    try {
	       Class.forName("org.postgresql.Driver");
	    } catch (ClassNotFoundException e) {
	       e.printStackTrace();
	    }
	  try {
	   con = DriverManager.getConnection(url,user,password);
	    stmt = con.createStatement();
	    res = stmt.executeQuery("SELECT VERSION()");
	 //  res = stmt.executeQuery("SELECT svoboda.syrio.name_syr FROM svoboda.syrio");
	    if (res.next()) {
	         System.out.println(res.getString(1));
	    }
	    
	        } catch (SQLException ex) {
	        } finally {
	            try {
	                if (res != null) {
	                    res.close();
	                }
	                if (stmt != null) {
	                    stmt.close();
	                }
	                if (con != null) {
	                    con.close();
	                }
	      } catch (SQLException ex) {
	            }
	        }
	    }
	}
