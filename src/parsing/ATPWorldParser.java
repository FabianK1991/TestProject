package parsing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import logic.main;

import org.json.JSONArray;
import org.json.JSONObject;

import database.TennisDBHelper;
import util.Loggar;

/*
 * TABLE TENNIS_PLAYER
 * 
 * ID 						*INT
 * NAME 					STRING
 * ATP_RANKING				INT
 * LAST_PARSED		 		TIME
 * 
 * 
 * TABLE: TENNIS_GAMES
 * 
 * ID						*INT
 * TOURNAMENT_ID			INT
 * FIRST_PLAYER_ID			INT
 * FIRST_PLAYER_RANKING		INT
 * SECOND_PLAYER_ID			INT
 * SECOND_PLAYER_RANKING	INT
 * ROUND					STRING
 * RESULT_STRING			STRING
 * RESULT					BOOLEAN		// true = first wins, false = second wins
 * 
 * TABLE: TENNIS_TOURNAMENT
 * 
 * ID						*INT
 * NAME						STRING
 * TIME						DATE
 * LOCATION					STRING
 * POINTS					STRING
 * COURT_TYPE				STRING
 * DRAW_AMOUNT				INT
 * 
 * http://stackoverflow.com/questions/418898/sqlite-upsert-not-insert-or-replace
 * 
 */

// TODO: Ranking History
public class ATPWorldParser extends WebsiteParser {
	private final static String searchUrl = "http://www.atpworldtour.com/Handlers/AutoComplete.aspx?q=";
	private final static String playerUrlRetrievalUrl = "http://www.atpworldtour.com/Tennis/Players/";
	
	// inclusive current year
	private final static int parseYears = 3; 
	
	/*
	 * DB update player
	 */
	private void upsertPlayerStats(String name, String atp_ranking){
		String sql = "INSERT OR REPLACE INTO Tennis_Player (id, name, atp_ranking, last_parsed) VALUES ((SELECT id FROM Tennis_Player WHERE name = '" + name.replace("'", "") + "'), '" + name.replace("'", "") + "', '" + atp_ranking + "', '" + (int)(new Date().getTime()/1000) + "')";
		
		main.db.exec(sql, null, false);
	}
	
	private void upsertTournament(String name, String location, String time, String points, String court_type, String draw_amount){
		String sql = "INSERT OR REPLACE INTO TENNIS_TOURNAMENT (id, name, time, location, points, court_type, draw_amount) VALUES ((SELECT id FROM TENNIS_TOURNAMENT WHERE name = '" + name.replace("'", "") + "' AND time = '" + time + "'), '" + name.replace("'", "") + "', '" + time + "', '" + location.replace("'", "") + "', '" + points.replace("'", "") + "', '" + court_type.replace("'", "") + "', '" + draw_amount.replace("'", "") + "')";
		
		main.db.exec(sql, null, false);
	}
	
	private void upsertGame(String tournament_id, String first_player_id, String first_player_ranking, String second_player_id, String second_player_ranking, String round, String result_string, String result){
		Loggar.logln(tournament_id + " - " + first_player_id + ":" + first_player_ranking + " vs. " + second_player_id + ":" + second_player_ranking + " - " + round + " result: " + result + " d: " + result_string);
	
		// Check if game exists already
		String sql = "SELECT id FROM Tennis_Games WHERE tournament_id = '" + tournament_id + "' AND round = '" + round + "' AND ((first_player_id = '" + first_player_id + "' AND second_player_id = '" + second_player_id + "') OR (first_player_id = '" + second_player_id + "' AND second_player_id = '" + first_player_id + "'))";
		ResultSet rs = main.db.exec(sql, null, true);
		
		try {
			while(rs.next()){
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Insert dat bitch
		sql = "INSERT INTO Tennis_Games (tournament_id, first_player_id, first_player_ranking, second_player_id, second_player_ranking, round, result_string, result) VALUES ('" + tournament_id + "','" + first_player_id + "','" + first_player_ranking + "','" + second_player_id + "','" + second_player_ranking + "','" + round + "','" + result_string + "','" + result + "')";
		
		main.db.exec(sql, null, false);
	}
	
	private String getPlayerIdAndInsertIfNotExist(String name){
		name = name.replace("'", "");
		
		String id = TennisDBHelper.getPlayerId(name);
		
		if( id == null ){
			String sql = "INSERT INTO Tennis_Player (name) VALUES ('" + name + "')";
			
			main.db.exec(sql, null, false);
			
			return TennisDBHelper.getPlayerId(name);
		}
		else{
			return id;
		}
	}
	
	private String buildGetParameters(String year, boolean singles){
		String _singles = "s";
		
		if( !singles ){
			_singles = "d";
		}
		
		return "?t=pa&y=" + year + "&m=" + _singles;
	}
	
	private String formatPlayerRating(String rating){
		if( rating == null ){
			return "2500";
		}
		
		String formatted = rating.replaceAll(",", "");
		
		if( formatted.length() == 0 || formatted.equals("N/A") ){
			return "2500";
		}
		
		return formatted;
	}
	
	private void getPlayerGames(String url, String year, String playerName) throws Exception{
		String response = sendGet( url + buildGetParameters(year, true) );
		
		// split response
		response = response.substring(response.indexOf("<div class=\"genericButton\">"), response.indexOf("<div class=\"genericModuleHeader\">"));
		
		// Patterns
		Pattern pTournamentBox = Pattern.compile("<div class\\=\"commonProfileContainer\">\\s*<p class=\"bioPlayActivityInfo\">(.*?<p class=\"bioPlayActivityInfo\">.*?)</div>");
		Pattern pTournamentInfos = Pattern.compile("<strong>(.*?)<\\/strong>(<.*?>)?\\,\\&nbsp\\;(.*?)\\;\\&nbsp\\;(.*?)\\;\\&nbsp\\;(.*?)\\;\\&nbsp\\;(.*?)\\;\\&nbsp\\;Draw\\:\\&nbsp\\;(.*?)\\s");
		Pattern pTournamentOutcome = Pattern.compile("<p class=\"bioPlayActivityInfo\"><span>(This Event Points\\:\\&nbsp\\;.*?)?(,\\&nbsp\\;)?(ATP Ranking\\:\\&nbsp\\;.*?)?(,\\&nbsp\\;)?(Prize Money\\:\\&nbsp\\;.*?)?</span></p>");
		Pattern pGameRow = Pattern.compile("<tr.*?>\\s*<td>(.*?)<\\/td>\\s*<td>(.*?)<\\/td>\\s*<td>(.*?)<\\/td>\\s*<td>(.*?)<\\/td>\\s*<td>.*?<\\/td>\\s*<\\/tr>");
		
		Matcher m = pTournamentBox.matcher(response);
		
		while(m.find()){
			String tournamentBox = m.group(1);

			// Tournament Infos
			// name location date points type draw
			String[] tournamentInfos = returnMatchedValue(pTournamentInfos, tournamentBox, new int[]{1,3,4,5,6,7});
			// event points atp ranking prize money
			String[] tournamentOutcome = returnMatchedValue(pTournamentOutcome, tournamentBox, new int[]{1,3,5});
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			Date parsedDate = sdf.parse(tournamentInfos[2]);
			String dateString = Integer.toString((int)(parsedDate.getTime()/1000));
			
			upsertTournament(tournamentInfos[0], tournamentInfos[1], dateString, tournamentInfos[3], tournamentInfos[4].replaceAll("\\&nbsp\\;", " "), tournamentInfos[5]);
			//printArray(tournamentInfos);
			//printArray(tournamentOutcome);
			
			// Game rows
			
			// round opponent ranking score
			List<String[]> gameRows = returnMatchedValues(pGameRow, tournamentBox, new int[]{1,2,3,4});
			String tournamentId = TennisDBHelper.getTournamentId(tournamentInfos[0], dateString);
			String playerId = TennisDBHelper.getPlayerId(playerName);
			String playerRanking = formatPlayerRating(tournamentOutcome[1].replaceAll(".*ATP Ranking\\:\\&nbsp\\;(.*)", "$1"));
			
			for(String[] row : gameRows){
				// exclude bye games
				if( row[1].equals("Bye") ){
					continue;
				}
				
				String opponentId = getPlayerIdAndInsertIfNotExist(row[1].replaceAll("<a.*?>(.*?)<\\/a>.*", "$1").replaceAll("\\&nbsp\\;", " "));
				String opponentRanking = formatPlayerRating(row[2]);
				String result = ( row[3].charAt(0) == 'W' ) ? "1" : "0";
				
				upsertGame(tournamentId, playerId, playerRanking, opponentId, opponentRanking, row[0], row[3], result);
				//printArray(row);
			}
		}

	}
	
	private void getPlayerStats(String pid) throws Exception{
		String response = sendGet( playerUrlRetrievalUrl + pid + ".aspx" );
		String playerUrl = lastVisitedUrl;

		Pattern pRating = Pattern.compile("<div id=\"playerBioInfoRank\">\\s*<span>([^<]*)<\\/span>");
		Pattern pName = Pattern.compile("<h1>([^<]*)<\\/h1>");
		
		String rating = returnMatchedValue(pRating, response);
		String name = returnMatchedValue(pName, response);
		
		// write player to database
		upsertPlayerStats(name, rating);
		
		// go get dem!
	    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
	    
	    for(int i=currentYear-parseYears;i<=currentYear;i++){
	    	getPlayerGames(playerUrl, Integer.toString(i), name);
	    }
	}
	
	/*
	 * Player format: R. Federer (Roger Federer)
	 */
	public void searchPlayer(String playerName) throws Exception {
		String response = sendGet( searchUrl + playerName.replaceAll(" ", "%20"));
		String jsonString = "{\"arr\":" + response + "}";
		
		JSONObject obj = new JSONObject(jsonString);
		JSONArray arr = obj.getJSONArray("arr");
		
		for (int i = 0; i < arr.length(); ++i) {
		    JSONObject rec = arr.getJSONObject(i);
		    
		    getPlayerStats(rec.getString("pid"));
		}
	}
}
