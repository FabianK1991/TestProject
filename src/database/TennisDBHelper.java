package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.main;

public class TennisDBHelper {
	public static String getPlayerId(String name){
		String sql = "SELECT id FROM Tennis_Player WHERE name = '" + name.replace("'", "") + "'";
		
		ResultSet rs = main.db.exec(sql, null, true);
		
		try {
			while(rs.next()){
				return rs.getString("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getTournamentId(String name, String time){
		String sql = "SELECT id FROM TENNIS_TOURNAMENT WHERE name = '" + name.replace("'", "") + "' AND time = '" + time + "'";
		
		ResultSet rs = main.db.exec(sql, null, true);
		
		try {
			while(rs.next()){
				return rs.getString("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static int getGameCount(String playerId){
		String sql = "SELECT COUNT(*) as c FROM tennis_GAMES WHERE first_player_id = '" + playerId + "' OR second_player_id = '" + playerId + "'";
		
		ResultSet rs = main.db.exec(sql, null, true);
		List<String> result = new ArrayList<String>();
		
		try {
			while(rs.next()){
				return rs.getInt("c");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static List<String> getGamesSince(String from, String playerId){
		String sql = "SELECT tg.id as id FROM tennis_GAMES as tg INNER JOIN tenNIS_TOURNAMENT as tt ON tg.tournament_id = tt.id WHERE (first_player_id = '" + playerId + "' OR second_player_id = '" + playerId + "') AND TIME >= '" + from + "' ORDER BY TIME DESC";
		
		ResultSet rs = main.db.exec(sql, null, true);
		List<String> result = new ArrayList<String>();
		
		try {
			while(rs.next()){
				result.add(rs.getString("id"));
			}

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<String> getGamesInTimeRange(String from, String till, String playerId){
		String sql = "SELECT tg.id as id FROM tennis_GAMES as tg INNER JOIN tenNIS_TOURNAMENT as tt ON tg.tournament_id = tt.id WHERE (first_player_id = '" + playerId + "' OR second_player_id = '" + playerId + "') AND TIME >= '" + from + "' AND TIME <= '" + till + "' ORDER BY TIME DESC";
		
		ResultSet rs = main.db.exec(sql, null, true);
		List<String> result = new ArrayList<String>();
		
		try {
			while(rs.next()){
				result.add(rs.getString("id"));
			}

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
