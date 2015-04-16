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
import util.TennisCalculatorHelper;

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
	
	public static void performBenchmark(){
		TennisModelBenchmark tmb = new TennisModelBenchmark();
    	
		double r1 = tmb.performBenchmark("Jeremy Chardy");  //  0.68					0.72
		double r2 = tmb.performBenchmark("Tomas Berdych");	//  0.8780487804878049		0.75
		double r3 = tmb.performBenchmark("Simone Bolelli"); //  0.6666666666666666		0.72
		
		Loggar.logln(r1 + " - " + r2 + " - " + r3);
	}
	
	public static void parsePlayer(String player){
		ATPWorldParser wp = new ATPWorldParser();
    	
    	try {
    		wp.searchPlayer(player);
    	} catch (Exception e){
    		e.printStackTrace();
    	}
	}
	
	
    public static void main(String args[]) {
    	//System.out.println(TennisPlayerPerformanceCalculator.clearness("L&nbsp;0-6, 7-5, 1-6"));
    	System.out.println(TennisCalculatorHelper.getLinearValue(60, 10, 0.3, 100, 1.0));
    	
    	//performBenchmark();
    	
    	//parsePlayer("Tomas BERDYCH");
    	//parsePlayer("Jeremy Chardy");
    	//parsePlayer("Simone Bolelli");
    }
}
