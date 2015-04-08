package testing;

import org.neuroph.core.data.DataSet;

import util.Loggar;
import nn.TennisGamePredictionNetwork;
import nn.TennisGameTrainingSetGenerator;

public class TennisNeuralNetworkModel extends ModelWrapper {
	private String player;
	private TennisGamePredictionNetwork tn;

	@Override
	public void init(String player, String tillTime) {
		TennisGameTrainingSetGenerator t = new TennisGameTrainingSetGenerator();
		
		// Get DataSet
		DataSet ds = t.generateDataSet(player, tillTime);
		
		// Train Network
		tn = new TennisGamePredictionNetwork();
		
		Loggar.logln("TennisGamePredictionNetwork start training with " + ds.size() + " rows!");
		
		tn.train(ds);
		
		Loggar.logln("TennisGamePredictionNetwork training finished!!");
	}

	@Override
	public double predictGame(String gameId) {
		double[] input = TennisGameTrainingSetGenerator.generateInputFromGameId(gameId, player);
		
		return tn.calculate(input)[0];
	}

}
