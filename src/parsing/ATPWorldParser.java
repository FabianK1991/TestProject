package parsing;

import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import logic.main;

import org.json.JSONArray;
import org.json.JSONObject;

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
public class ATPWorldParser extends WebsiteParser {
	private final static String searchUrl = "http://www.atpworldtour.com/Handlers/AutoComplete.aspx?q=";
	private final static String playerUrlRetrievalUrl = "http://www.atpworldtour.com/Tennis/Players/";
	
	// inclusive current year
	private final static int parseYears = 1; 
	
	/*
	 * DB update player
	 */
	private void upsertPlayerStats(String name, String atp_ranking){
		String sql = "INSERT OR REPLACE INTO Tennis_Player (id, name, atp_ranking, last_parsed) VALUES ((SELECT id FROM Tennis_Player WHERE name = '" + name + "'), '" + name + "', '" + atp_ranking + "', '" + (int)(new Date().getTime()/1000) + "')";
		
		main.db.exec(sql, null, false);
	}
	
	private void upsertTournament(String name, String location, String time, String points, String court_type, String draw_amount){
		String sql = "INSERT OR REPLACE INTO TENNIS_TOURNAMENT (id, name, time, location, points, court_type, draw_amount) VALUES ((SELECT id FROM TENNIS_TOURNAMENT WHERE name = '" + name + "' AND time = '" + time + "'), '" + name + "', '" + time + "', '" + location + "', '" + points + "', '" + court_type + "', '" + draw_amount + "')";
		
		main.db.exec(sql, null, false);
	}
	
	private String buildGetParameters(String year, boolean singles){
		String _singles = "s";
		
		if( !singles ){
			_singles = "d";
		}
		
		return "?t=pa&y=" + year + "&m=" + _singles;
	}
	
	private void printArray(String[] arr){
		for(int i=0;i<arr.length;i++){
			Loggar.log(arr[i] + " ");
		}
		
		Loggar.log("\n\n");
	}
	
	private void getPlayerGames(String url, String year) throws Exception{
		String response = sendGet( url + buildGetParameters(year, true) );
		
		// Patterns
		Pattern pTournamentBox = Pattern.compile("<div class\\=\"commonProfileContainer\">\\s*<p class=\"bioPlayActivityInfo\">(.*?<p class=\"bioPlayActivityInfo\">.*?)</div>");
		Pattern pTournamentInfos = Pattern.compile("<strong>(.*?)<\\/strong>(<.*?>)?\\,\\&nbsp\\;(.*?)\\;\\&nbsp\\;(.*?)\\;\\&nbsp\\;(.*?)\\;\\&nbsp\\;(.*?)\\;\\&nbsp\\;Draw\\:\\&nbsp\\;(.*?)\\s");
		Pattern pTournamentOutcome = Pattern.compile("<p class=\"bioPlayActivityInfo\"><span>(This Event Points\\:\\&nbsp\\;.*?)?(,\\&nbsp\\;)?(ATP Ranking\\:\\&nbsp\\;.*?)?(,\\&nbsp\\;)?(Prize Money\\:\\&nbsp\\;.*?)?</span></p>");
		Pattern pGameRow = Pattern.compile("<tr.*?>\\s*<td>(.*?)<\\/td>\\s*<td>(.*?)<\\/td>\\s*<td>(.*?)<\\/td>\\s*<td>(.*?)<\\/td>\\s*<td>.*?<\\/td>\\s*<\\/tr>");
		
		Matcher m = pTournamentBox.matcher(response);
		
		while(m.find()){
			String tournamentBox = m.group(1);
			
			//Loggar.log("\n");
			
			// Tournament Infos
			// name location date points type draw
			String[] tournamentInfos = returnMatchedValue(pTournamentInfos, tournamentBox, new int[]{1,3,4,5,6,7});
			
			upsertTournament(tournamentInfos[0], tournamentInfos[1], tournamentInfos[2], tournamentInfos[3], tournamentInfos[4], tournamentInfos[5]);
			//printArray(tournamentInfos);
			
			// Game rows
			
			// round opponent ranking score
			List<String[]> gameRows = returnMatchedValues(pGameRow, tournamentBox, new int[]{1,2,3,4});
			
			//for(String[] row : gameRows){printArray(row);}Loggar.log("\n");
			
			// tournament outcome
			String[] tournamentOutcome = returnMatchedValue(pTournamentOutcome, tournamentBox, new int[]{1,3,5});
			
			//printArray(tournamentOutcome);
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
		
		//getPlayerGames(playerUrl, "2015");
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
