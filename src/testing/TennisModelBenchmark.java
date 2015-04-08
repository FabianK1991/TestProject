package testing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.Loggar;
import database.TennisDBHelper;
import logic.main;

public class TennisModelBenchmark {
	List<ModelWrapper> testModels;
	private static final double validateGameRange = 60 * 60 * 24 * 182; // 0.5 years
	
	public TennisModelBenchmark(){
		testModels = new ArrayList<ModelWrapper>();
		
		TennisNeuralNetworkModel nmm = new TennisNeuralNetworkModel();
		
		testModels.add(nmm);
	}
	
	private boolean getActualResult(String gameId, String playerId){
		String[] result = main.db.select("tennis_games", new String[]{"first_player_id", "second_player_id", "result"}, "id = '" + gameId + "'").get(0);
		
		if(result[0].equals(playerId)){
			return ( result[2].equals("0") ) ? false : true;
		}
		else{
			return ( result[2].equals("0") ) ? true : false;
		}
	}
	
	/*
	 * Performs a Benchmark on the player games
	 * 
	 */
	public void peformBenchmark(String name){
		int curTime = (int)(new Date().getTime()/1000);
		curTime -= validateGameRange;
		
		String id = main.db.select("tennis_player", new String[]{"id"}, "name = '" + name + "'").get(0)[0];
		
		Loggar.logln("Performing model benchmark for player: " + name);
		Loggar.logln(TennisDBHelper.getGameCount(id) + " games found!\n\n"); 
		
		List<String> games = TennisDBHelper.getGamesSince(String.valueOf(curTime), id);
		
		for(ModelWrapper mw : testModels ){
			mw.init(name, String.valueOf(curTime));
			
			int rightAnswers = 0;
			
			Loggar.logln("\nStart predicting " + games.size() + " games!");
			
			for(String gameId: games){
				double dprediction = mw.predictGame(gameId);
				
				boolean prediction = (dprediction > 0.5) ? true : false;
				boolean actualResult = getActualResult(gameId, id);
				
				if( prediction == actualResult ){
					rightAnswers++;
				}
				else{
					Loggar.logln("Wrong answer for game: " + gameId + " Prediction was: " + dprediction + " Result was: " + actualResult);
				}
			}
			
			Loggar.logln("Overall prediction was: " + rightAnswers + "/" + games.size() + " ratio: " + (double)(rightAnswers/(double)games.size()));
		}
	}
}
