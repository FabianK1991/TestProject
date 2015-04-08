package nn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import util.Loggar;
import logic.main;

/*
 * 		INPUT: ranking diffrence, ROUND (PREFINAL / FINAL), INDOOR / OUTDOOR, HARD / SOFT
 * 
 * 		OUTPUT: WIN / LOSS, DEUTLICH / WENIGER DEUTLICH
 * 
 * 
 * 		ZU PERFORMANCE: PREFINAL, SEMI FINAL, FINAL
 * 		MEHR GEWICHTET: LETZTE SPIELE
 * 		MEHR GEWICHTET: SPIELE GEGEN DEN GEGNER
 * 
 */
public class TennisGameTrainingSetGenerator {
	private final static int gameRange = 60 * 60 * 24 * 365 * 3; // 3 years
	
	private class TennisTrainingDataSet{
		public int time;
		
		public double diffRanking;
		
		public boolean win;
		public double clear_result;
		
		public boolean indoor;
		public boolean hard;
		
		public boolean prefinal;
	}
	
	private boolean isPrefinal(String round){
		switch(round){
			case "S":
			case "Q":
			case "F":
			case "W":
				return false;
		}
		
		return true;
	}
	
	private boolean isIndoorCourt(String court_type){
		return (court_type.indexOf("Outdoor") == -1) ? true : false;
	}
	
	private boolean isHardCourt(String court_type){
		if( court_type.indexOf("Hard") >= -1 ) return true;
		if( court_type.indexOf("Clay") >= -1 ) return true;
		if( court_type.indexOf("Stone") >= -1 ) return true;
		
		return false;
	}
	
	private boolean isWin(String result_string, boolean isPlayerFirst){
		if( isPlayerFirst ){
			return (result_string.charAt(0) == 'W') ? true : false; 
		}
		else{
			return (result_string.charAt(0) == 'W') ? false : true; 
		}
	}
	
	//private double calc
	
	private static double getLinearValue(double x, double x_1, double y_1, double x_2, double y_2){
		// ax + b = y	
		double a = (y_2 - y_1) / (x_2 - x_1);
		double b = y_2 - a * x_2;
		
		return a * x + b;
	}
	
	private static boolean inRange(double value, double min, double max){
		return (value >= min && value <= max);
	}
	
	private static double calcRanking(double OpponentRanking) throws Exception{
		if( OpponentRanking == 2500 ){
			throw new Exception("Undefined Ranking!");
		}
		
		// 1 - 1500
		// 0 - 1
		double a = 600;
		double b = 1;		
		
		return Math.max(0.0, Math.min((OpponentRanking - a) / (b - a), 1.0));
	}
	
	
	private static double calcRanking(int PR, int OpponentRanking) throws Exception{
		if( PR == 2500 || OpponentRanking == 2500 ){
			throw new Exception("Undefined Ranking!");
		}

		// PR			Diff Range (<=0.3)
		// 5: 			3 
		// 15 : 		7
		// 25 : 		11
		// 50 : 		17
		// 100 : 		38
		// 300 : 		75
		// 500 : 		100
		//
		// Need to be calculated from data
		
		double diffRange = 0;
		final double diffRangeTreshold = 0.35;
		
		double result = 0;
		
		if( inRange(PR, 1, 5) ){
			diffRange = getLinearValue(PR, 1, 2, 5, 3);
		}
		else if( inRange(PR, 6, 15) ){
			diffRange = getLinearValue(PR, 6, 3, 15, 7);
		}
		else if( inRange(PR, 16, 25) ){
			diffRange = getLinearValue(PR, 16, 7, 25, 11);
		}
		else if( inRange(PR, 26, 50) ){
			diffRange = getLinearValue(PR, 26, 11, 50, 17);
		}
		else if( inRange(PR, 51, 100) ){
			diffRange = getLinearValue(PR, 51, 17, 100, 38);
		}
		else if( inRange(PR, 101, 300) ){
			diffRange = getLinearValue(PR, 101, 38, 300, 75);
		}
		else if( inRange(PR, 301, 500) ){
			diffRange = getLinearValue(PR, 301, 75, 500, 100);
		}
		else{
			diffRange = 150;
		}
		
		// Lineares Wachstum bis diffRange danach exponential bis 1
		double diff = PR - OpponentRanking;
		
		if( Math.abs(diff) <= diffRange ){
			double factor = 1;
			
			if (diff < 0){
				factor = -1;
			}
			
			return factor * getLinearValue(Math.abs(diff),0,0,diffRange,diffRangeTreshold);
		}
		
		return diff;
	}
	
	public static void main( String args[] )
	{
		try {
			/*System.out.println(calcRanking(3, 5));
			System.out.println(calcRanking(15, 50));
			System.out.println(calcRanking(30, 1));
			System.out.println(calcRanking(50, 1));
			System.out.println(calcRanking(80, 1));
			System.out.println(calcRanking(120, 1));
			System.out.println(calcRanking(300, 1));
			System.out.println(calcRanking(498, 1));
			System.out.println(calcRanking(1000, 1));*/
			//System.out.println(calcRanking(30));
			//System.out.println(calcRanking(100));
			//System.out.println(calcRanking(1000));
			TennisGameTrainingSetGenerator t = new TennisGameTrainingSetGenerator();
			
			t.generateDataSet("Roger Federer");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(calcRanking(5));
		//System.out.println(calcRanking(100));
		//System.out.println(calcRanking(70));
	}
	
	private void calcWinLoss(TennisTrainingDataSet ds, String result_string){
		
	}
	
	private List<TennisTrainingDataSet> getDataSets(String name, int time) throws Exception{
		String id = main.db.select("tennis_player", new String[]{"id"}, "name = '" + name + "'").get(0)[0];
		String sql = "SELECT first_player_id,first_player_ranking,second_player_id,second_player_ranking,round,time,result_string,court_type FROM tennis_GAMES as tg INNER JOIN tenNIS_TOURNAMENT as tt ON tg.tournament_id = tt.id WHERE first_player_id = '" + id + "' OR second_player_id = '" + id + "' AND TIME >= '" + time + "'";
	
		ResultSet rs = main.db.exec(sql, null, true);
		List<TennisTrainingDataSet> result = new ArrayList<TennisTrainingDataSet>();
		
		try {
			while(rs.next()){
				try{
					TennisTrainingDataSet ds = new TennisTrainingDataSet();
					ds.time = rs.getInt("time");
					ds.prefinal = isPrefinal(rs.getString("round"));
					
					ds.indoor = isIndoorCourt(rs.getString("court_type"));
					ds.hard = isHardCourt(rs.getString("court_type"));
					
					if( id.equals(rs.getString("first_player_id")) ){
						//ds.diffRanking = calcRanking(rs.getInt("first_player_ranking"), rs.getInt("second_player_ranking"));
						ds.diffRanking = calcRanking(rs.getInt("second_player_ranking"));
						ds.win = isWin(rs.getString("result_string"), true);
					}
					else{
						//ds.diffRanking = calcRanking(rs.getInt("second_player_ranking"), rs.getInt("first_player_ranking"));
						ds.diffRanking = calcRanking(rs.getInt("first_player_ranking"));
						ds.win = isWin(rs.getString("result_string"), false);
					}
					
					result.add(ds);
				}catch( Exception e){
					continue;
				}
			}

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public DataSet generateDataSet(String Player){
		// 
		int curTime = (int)(new Date().getTime()/1000);
		curTime -= gameRange;
		
		try {
			List<TennisTrainingDataSet> l = getDataSets(Player, curTime);
			DataSet ds = new DataSet(4, 1);
			
			for( TennisTrainingDataSet tt : l ){
				//Loggar.logln(tt.diffRanking + " - " + tt.time + " - " + tt.hard + " - " + tt.indoor + " - " + tt.prefinal);
				
				ds.addRow(new DataSetRow(new double[]{tt.diffRanking, (tt.prefinal) ? 1 : 0, (tt.indoor) ? 1 : 0, (tt.hard) ? 1 : 0}, new double[]{(tt.win) ? 1 : 0}));
			}
			
			return ds;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
