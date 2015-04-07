package parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Loggar;

/*
 * TABLE TENNIS_PLAYER
 * 
 * PLAYER_ID 			INT
 * PLAYER_NAME 			STRING
 * PLAYER_WETTPOINT_ID 	INT
 * LAST_PARSED_GAMES 	TIME
 * WIN_AMOUNT			INT
 * LOSS_AMOUNT			INT
 * 
 * # MAYBE INCLUDE WINS IN EACH TYPE OF COURT
 * 
 * TABLE TENNIS_GAMES
 * 
 * GAME_ID				INT
 * TOURNAMENT_ID		INT
 * FIRST_PLAYER_ID		INT
 * SECOND_PLAYER_ID		INT
 * TIME					TIME
 * STAGE				STRING
 * TYPE					STRING
 * RESULT_STRING		STRING
 * RESULT				BOOLEAN		// true = first wins, false = second wins
 * 
 * TABLE TENNIS_TOURNAMENT
 * 
 * TOURNAMENT_ID		INT
 * TOURNAMENT_NAME		STRING
 * 
 * http://www.itftennis.com/procircuit/players/player/profile.aspx?PlayerID=30019224
 * http://www.daviscup.com/en/players/player/profile.aspx?playerid=30019224
 * http://www.atpworldtour.com/Tennis/Players/Top-Players/Yen-Hsun-Lu.aspx?t=pa
 * 
 */
public class WettpointParser extends WebsiteParser {
	private final static String searchUrl = "http://tennis.wettpoint.com/en/search.html";
	private final static String playerUrl = "http://tennis.wettpoint.com/en/players/";
	
	// inclusive current year
	private final static int parseYears = 1; 
	
	private String createPostData(String playerName){
		return "suche1=&suche2=" + playerName + "&ssuche=Player+Search";
	}
	
	private String getName(String fullName){
		String pattern = "[^\\.]*\\.\\s*(.*)";
		
		return fullName.replaceAll(pattern, "$1");
	}
	
	// Update the players games - only parse games that are not already parsed and update player stats
	private void updatePlayerGames(String playerId){
		
	}
	
	private void parsePlayerGames(String playerId, String year) throws Exception{
		String url = playerUrl + playerId + "-" + year + ".html";
		String response = sendGet( url );
		
		// parse matches
		String curTournamentName = null;
		String curCourtType = null;
		
		Pattern contentLine = Pattern.compile(".*<tr><td[^>]*>([^<]*)<\\/td><td>(.*) \\- (.*)<\\/td><td>([^<]*)<\\/td><td>([^<]*)<\\/td>.*");
		Pattern tournamentLine = Pattern.compile(".*<img[^>]*><b>([^<]*)<\\/b>\\s*\\(\\s([^\\)]*)\\s\\).*");
		Pattern playerWon = Pattern.compile("<b>([^<]*)<\\/b>");
		
		int idx = response.indexOf("<tr><td colspan=\"4\" height=\"25px\">");
		
		if( idx > 0 ){
			response = response.substring(idx);
			
			while( response.indexOf("<tr>") == 0 ){
				
				// Parse new line
				idx = response.indexOf("</tr>");
				
				if( idx >= 0 ){
					idx = idx+5;
					
					String line = response.substring(0, idx);
					
					// check line type is it content line or should we change tournament
					if( line.matches(tournamentLine.pattern()) ){
						String[] results = returnMatchedValue(tournamentLine, line, new int[]{1,2});
						
						curTournamentName = results[0];
						curCourtType = results[1];
						
						Loggar.logln("TournamentLine: " + curTournamentName + " : " + curCourtType);
						
						// TODO: UPDATE OR INSERT TOURNAMENT
					}
					else if( line.matches(contentLine.pattern()) ){
						// time player_1 player_2 result_string stage
						String[] results = returnMatchedValue(contentLine, line, new int[]{1,2,3,4,5});
						
						// Parse Date
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm");
						Date parsedDate = sdf.parse(results[0]);
						
						boolean matchResult = false;
						
						// did player 1 win?
						if( results[1].matches(playerWon.pattern()) ){
							matchResult = true;
							results[1] = results[1].replaceAll(playerWon.pattern(), "$1");
						}
						else{
							results[2] = results[2].replaceAll(playerWon.pattern(), "$1");
						}
						
						Loggar.log(parsedDate.toLocaleString() + ": ");
						Loggar.log(results[1] + " vs. " + results[2] + " " + matchResult + " ");
						
						for(int i=3;i<results.length;i++){
							Loggar.log(results[i] + " ");
						}
						
						Loggar.log("\n");
						
						// TODO: INSERT DATA INTO DATABASE
					}
					
					response = response.substring(idx);
				}
			}
		}
	}
	
	// http://stackoverflow.com/questions/418898/sqlite-upsert-not-insert-or-replace
	private void parsePlayerStats(String playerId) throws Exception{
		 String url = playerUrl + playerId + ".aspx";
		 
		 String response = sendGet( url );
		 
		 // Patterns
		 Pattern pWin = Pattern.compile("<td>Win[^<]*<\\/td><td><b>([^<]*)<\\/b>");
		 Pattern pLoss = Pattern.compile("<td>Loss[^<]*<\\/td><td><b>([^<]*)<\\/b>");
		 Pattern pName = Pattern.compile("<strong>([^<]*)\\s: Statistics all Years</strong>");
		 
		 // Wins
		 String win = returnMatchedValue(pWin, response);
		 String loss = returnMatchedValue(pLoss, response);
		 String name = returnMatchedValue(pName, response);
		 
		 Loggar.logln(name + ": (" + win + "/" + loss + ")");
		 
		 // TODO: WRITE / UPDATE DATABASE
	}
	
	/*
	 * Player format: R. Federer (Roger Federer)
	 */
	public void searchPlayer(String playerName) throws Exception {
		String response = sendPost( searchUrl, createPostData(getName(playerName)));
		
		// search for the player link
		Pattern pattern = Pattern.compile("<a[^>]*href=\"" + playerUrl.replaceAll("\\/", "\\\\/") + "([^\"]*).html\"[^>]*>" + playerName + "</a>");
		Matcher m = pattern.matcher(response);
		
		// could be that we find several
		while (m.find()) {
		    String playerId = m.group(1);
		    
		    // now parse the player stats
		    parsePlayerStats(playerId);
		    
		    // parse the players games
		    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		    
		    for(int i=currentYear-parseYears;i<=currentYear;i++){
		    	parsePlayerGames(playerId, Integer.toString(i));
		    }
		}
	}
}
