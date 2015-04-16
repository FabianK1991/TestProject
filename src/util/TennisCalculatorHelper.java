package util;

import java.util.List;

import database.TennisDBHelper;
import database.TennisGame;

public class TennisCalculatorHelper {
	/*
	 * Lineare Interpolation
	 */
	public static double getLinearValue(double x, double x_1, double y_1, double x_2, double y_2){
		// ax + b = y	
		double a = (y_2 - y_1) / (x_2 - x_1);
		
		return y_2 + a * (x - x_2);
	}
	
	public static double calcRanking(double OpponentRanking) throws Exception{
		if( OpponentRanking == 2500 ){
			throw new Exception("Undefined Ranking!");
		}
		
		// 1 - 1500
		// 0 - 1
		double a = 600;
		double b = 1;		
		
		return Math.max(0.0, Math.min((OpponentRanking - a) / (b - a), 1.0));
	}
	
	/*
	 * Games are ordered from newst to oldest
	 */
	public static TennisPlayerRating getPlayerCustomRating(List<String> games, String playerId, int timestamp){
		// can't rate player with too few games
		if( games.size() < 10 ){
			return null;
		}
		
		
		for(String gameId : games){
			TennisGame tg = TennisDBHelper.getTennisGame(gameId);
			
			//double gameWeight = TennisCalculatorHelper.getLinearValue(gameTime, timestamp - relevantGames, 0.5, timestamp, 1.0); 
		}
		
		
		return null;
	}
	
	public static double getGameWinVariance(List<String> games, String playerId){
		
		
		return 0;
	}
	
	
	/*
	 * Gibt zahl zurück zwischen -1 und 1 wie stark sieg bzw. niederlage ist
	 */
	public static double getGameClearness(String result_string){
		result_string = result_string.substring(7);
		
		if( result_string.length() <= 1 || result_string.indexOf("W/O") >= 0 || result_string.indexOf("RET") >= 0 ){
			return -100000;
		}
		
		String[] splitted = result_string.split(",\\s*");
		double result = 0; 
		
		for (String satz: splitted){			
			String[] values = satz.split("\\-");
			
			int value1 = Integer.parseInt(values[0]);
			int value2 = Integer.parseInt(values[1].replaceAll("\\(.*?\\)", ""));
			
			int modifier = (value1 > value2) ? 1 : -1;
			double c = (Math.abs(value1 - value2)) * (1.0 / 6.0);

			result = result + c * modifier * (1.0/(double)splitted.length);
		}
		
		return result;
	}
}
