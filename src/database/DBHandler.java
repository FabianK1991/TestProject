package database;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import logic.main;

public class DBHandler
{
	private static final String DBName = "test.db";
	private Connection con;
	
	public DBHandler(){
	    try {
	      Class.forName("org.sqlite.JDBC");
		  this.con = DriverManager.getConnection("jdbc:sqlite:" + DBHandler.DBName);
		} catch ( Exception e ) {
		  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		  System.exit(0);
		}
	}
	
	public ResultSet exec(String query){
		return this.exec(query, null, true);
	}
	
	/**
	 * Exectues a given query on the database.
	 * @author Christian
	 * @param query The query to be executed.
	 * @param args A list of arguments that replace the place holders in the queryString.
	 * @param read Read something from the database with read = true. Write something with read = false.
	 * @return
	 */
	public ResultSet exec(String query, ArrayList<String> args, boolean read) {
		String secureQuery;
		if(args != null){
			int i = 0;
			while(i < args.size()) {
				args.set(i, this.secure(args.get(i)));
				i++;
			}
			secureQuery = String.format(query, args.toArray());
		}else{
			secureQuery = query;
		}

		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			//System.out.println(secureQuery.toString());
			
			if(read){
				ResultSet rs = stmt.executeQuery(secureQuery.toString());
				return rs;
			} else{
				stmt.execute(secureQuery.toString());
				return null;
			}

		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
	}
	
	public List<String[]> select(String table_name, String[] values, String condition){
		String sql = "SELECT " + String.join(",", values) + " FROM " + table_name;
		
		if( condition != null && condition.length() > 1 ){
			sql += " WHERE " + condition;
		}
		
		ResultSet rs = exec(sql, null, true);
		
		try {
			List<String[]> result = new ArrayList<String[]>();
			
			while(rs.next()){
				String[] item = new String[values.length];
				
				for(int i=0;i<values.length;i++){
					item[i] = rs.getString(values[i]);
				}
				
				result.add(item);
			}
			
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String secure(String o) {
		return o;
	}
	
	/*
	 * Schwankung in bets
	 */
	
	
	/*public static void main( String args[] )
	{
		DBHandler db = new DBHandler();
		
		ResultSet rs = db.exec("SELECT * FROM GameData");
		
		try {
			while(rs.next()){
				System.out.println(rs.getString("game_id") + " - " + rs.getString("player_home_id") + " - " + rs.getString("player_guest_id") + " - " + rs.getString("time") + " - " + rs.getString("outcome"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
}