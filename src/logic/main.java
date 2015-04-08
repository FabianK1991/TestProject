package logic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nn.TennisGamePredictionNetwork;
import nn.TennisGameTrainingSetGenerator;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import database.DBHandler;
import parsing.ATPWorldParser;
import parsing.WettpointParser;
import testing.TennisModelBenchmark;
import util.Loggar;

public class main {
    /**
     * PREDICTION WIRD BERECHNET AUS:
     * 
     * 		SPIELER/SPIELER WIN RATIO
     * 
     * 		VERSCHIEDENE NETWORKS F�R:
     *
     * 			SPIELER LETZTEN 20-25 SPIELE INKL. SPIELER/SPIELER ( ENEMY PERFORMANCE, COURT TYPE, FINAL ) (WIN/LOSS, CLEAR/NOT CLEAR) 
     * 			SPIELER LETZTEN 1,5 JAHRE ( ENEMY PERFORMANCE, COURT TYPE, FINAL ) (WIN/LOSS, CLEAR/NOT CLEAR) 
     * 
     *			SPIELER LETZTEN 20-25 SPIELE INKL. SPIELER/SPIELER ( ENEMY PERFORMANCE ) (WIN/LOSS, CLEAR/NOT CLEAR) 
     * 			SPIELER LETZTEN 1,5 JAHRE ( ENEMY PERFORMANCE ) (WIN/LOSS, CLEAR/NOT CLEAR) 
     * 
     * 		PERFORMANCE CALCULATOR:
     * 
     * 			LETZTEN SPIELE RESULT GEWICHTET MIT GEGNER ST�RKE / FINAL / COURT TYPE (WELCHES MITTEL? http://de.wikipedia.org/wiki/Quadratisches_Mittel)
     * 
     */
	public static DBHandler db = new DBHandler();
	
	
    public static void main(String args[]) {
    	TennisModelBenchmark tmb = new TennisModelBenchmark();
    	
    	tmb.peformBenchmark("Nicolas Almagro");
    	
    	// INPUT: ENEMY PERFORMANCE LAST 5 GAMES, ENEMY RATING, PERFORMANCE LAST 5 GAMES, HARD COURT, CLAY COURT
    	
    	/*ResultSet rs = db.exec("SELECT * FROM GameData");
		
		try {
			while(rs.next()){
				System.out.println(rs.getString("game_id") + " - " + rs.getString("player_home_id") + " - " + rs.getString("player_guest_id") + " - " + rs.getString("time") + " - " + rs.getString("outcome"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
    	
    	
    	/*DataSet trainingSet = new DataSet(5, 2);
    	
        trainingSet.addRow(new DataSetRow(new double[]{0.9, 0.9, 0.1, 0, 1}, new double[]{0, 1}));
        trainingSet.addRow(new DataSetRow(new double[]{0.2, 0.2, 0.6, 0, 1}, new double[]{1, 0.8}));
        trainingSet.addRow(new DataSetRow(new double[]{0.6, 0.9, 0.8, 1, 0}, new double[]{0, 0.2}));
        trainingSet.addRow(new DataSetRow(new double[]{0.4, 0.9, 0.4, 1, 0}, new double[]{0, 0.2}));
        trainingSet.addRow(new DataSetRow(new double[]{0.4, 0.1, 0.4, 1, 0}, new double[]{1, 0.2}));
        
        TennisGamePredictionNetwork net = new TennisGamePredictionNetwork();
        
        net.train(trainingSet);
        
        double[] out = net.calculate(new double[]{0.2, 0.1, 0.4, 1, 0});
        
        System.out.println("Prediction(Win/Loss): " + out[0] + " Clear: " + out[1]);*/
    	
    	/*String test = "abasdoiajsdjsad";
    	
    	Pattern p = Pattern.compile("(ba)?(sd)");
    	
    	Matcher m = p.matcher(test);
		
		while(m.find()){
			Loggar.logln(m.group(1));
			Loggar.logln(m.group(2));
		}*/
    	
    	
    	/*
    	ATPWorldParser wp = new ATPWorldParser();
    	
    	// HARDCORE TEST: RAFAEL ANTONIO COUTINHO
    	
    	try {
			//wp.searchPlayer("Rafael Nadal");
    		wp.searchPlayer("Nicolas Almagro");
    		
    		TennisGameTrainingSetGenerator t = new TennisGameTrainingSetGenerator();
			
    		// Get DataSet
    		DataSet ds = t.generateDataSet("Nicolas Almagro");
			
			// Train Network
    		TennisGamePredictionNetwork tn = new TennisGamePredictionNetwork();
    		
    		Loggar.logln("Start training!");
    		
    		tn.train(ds);
    		
    		Loggar.logln("Training finished!!");
    		
    		for(double i=0.5;i<1.0;i=i+0.05){
    			tn.printInput(new double[]{i, 1, 1, 1});
    		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
    	/*WettpointParser wp = new WettpointParser();
        
    	try {
			wp.searchPlayer("Y. Lu");
    		//wp.searchPlayer("P. Pimmel");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
    }
}
