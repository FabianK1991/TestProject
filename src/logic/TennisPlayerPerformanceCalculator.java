package logic;

import java.util.List;

import database.TennisDBHelper;

public class TennisPlayerPerformanceCalculator {
	private static final int relevantGames = 60*60*24*365*3;// letzten 3 Jahre
	
	/*
	 * Gibt zahl zurück zwischen -1 und 1 wie stark sieg bzw. niederlage ist
	 */
	public static double clearness(String result_string){
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
	
	/*
	 * PERFORMANCE ZU BESTIMMTEN ZEITPUNKT
	 * 
	 * LETZTEN SPIELE UND GEGNER KLARHEIT DES ERGEBNISSES
	 * HÄUFIGKEIT
	 */
	public static double calculatePlayerPerformance(String playerId, int timestamp){
		List<String> games = TennisDBHelper.getGamesInTimeRange(String.valueOf(timestamp - relevantGames), String.valueOf(timestamp), playerId);
		
		
		
		return 0;
	}
}
