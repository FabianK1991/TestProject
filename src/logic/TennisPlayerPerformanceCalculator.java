package logic;

import java.util.List;

import util.TennisCalculatorHelper;
import database.TennisDBHelper;

public class TennisPlayerPerformanceCalculator {
	private static final int relevantGames = 60*60*24*365*3;// letzten 3 Jahre
	
	
	
	/*
	 * PERFORMANCE ZU BESTIMMTEN ZEITPUNKT
	 * 
	 * LETZTEN SPIELE UND GEGNER KLARHEIT DES ERGEBNISSES
	 */
	public static double calculatePlayerPerformance(String playerId, int timestamp){
		List<String> games = TennisDBHelper.getGamesInTimeRange(String.valueOf(timestamp - relevantGames), String.valueOf(timestamp), playerId);
		
		double Performance = 0;
		// jedes spiel gibt + oder -
		
		while (Performance == 0){
			// Auslese Daten
			int gameTime = 50;
			String result_string = "";
			int PR = 1;
			int OR = 10;
			boolean win = true;
			
			double gameWeight = TennisCalculatorHelper.getLinearValue(gameTime, timestamp - relevantGames, 0.5, timestamp, 1.0); 
			double clearness = TennisCalculatorHelper.getGameClearness(result_string);
			
			// correct clearness
			if( clearness < 0 && win || clearness > 0 && !win ){
				clearness *= -1;
			}

			double pRanking = 0;
			double oRanking = 0;
			double diff = 0;
			
			try {
				pRanking = TennisCalculatorHelper.calcRanking(PR);
				oRanking = TennisCalculatorHelper.calcRanking(OR);
				
				diff = pRanking - oRanking;
			} catch (Exception e) {
				continue;
			}
			
			double result = 0;
			// diff > 0 wir sind besser
			
			// d > 0.3 && c > 0 => voll (0.0 - 0.1)
			// d > 0.3 && c < 0 => (>0.5)
			// d > 0 && c  
			
			// 
			//if ( (diff > 0 && clearness < 0) || (diff < 0 && clearness > 0) ){
			//	Math.abs(Math.pow(Math.pow(diff, 1/3) * clearness, 1/3));
			//}
			//else if(  ){
			//	Math.abs(Math.pow(Math.pow(diff, 1/2) * clearness, 1/3));
			//}
			
			//double performanceChange = Math.abs(clearness) + diff;
			
			
			// wie groß ist der anteil an der gesamt performance???
			// letzten spiele größerer anteil?
		
			// 
		}
		
		

		return Performance;
	}
}
