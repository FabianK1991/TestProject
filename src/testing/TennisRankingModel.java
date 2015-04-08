package testing;

import java.sql.ResultSet;
import java.sql.SQLException;

import logic.main;

public class TennisRankingModel extends ModelWrapper {
	private String player;
	
	@Override
	public void init(String player, String tillTime) {
		// TODO Auto-generated method stub
		this.player = player;
	}

	@Override
	public double predictGame(String gameId) {
		String id = main.db.select("tennis_player", new String[]{"id"}, "name = '" + player + "'").get(0)[0];
		String sql = "SELECT first_player_id,first_player_ranking,second_player_id,second_player_ranking,result FROM tennis_GAMES WHERE id = '" + gameId + "'";
		
		ResultSet rs = main.db.exec(sql, null, true);
		
		try {
			while(rs.next()){
				int diff = rs.getInt("first_player_ranking") - rs.getInt("second_player_ranking");
				
				if( id.equals(rs.getString("first_player_id")) ){
					return (diff > 0) ? 0.0 : 1.0;
				}
				else{
					return (diff > 0) ? 1.0 : 0.0;
				}
			}

			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

}
