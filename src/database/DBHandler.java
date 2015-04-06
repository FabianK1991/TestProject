package database;

import java.sql.*;

public class DBHandler
{
	private static final String DBName = "test.db";
	
	public DBHandler(){
		
	}
	
	  public static void main( String args[] )
	  {
	    Connection c = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	  c = DriverManager.getConnection("jdbc:sqlite:" + DBHandler.DBName);
	} catch ( Exception e ) {
	  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	  System.exit(0);
	}
	System.out.println("Opened database successfully");
	  }
}