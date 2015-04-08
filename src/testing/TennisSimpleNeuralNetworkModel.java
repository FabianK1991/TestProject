package testing;



import org.neuroph.core.data.DataSet;

import util.Loggar;
import nn.TennisGamePredictionNetwork;
import nn.TennisGameTrainingSetGenerator;

public class TennisSimpleNeuralNetworkModel extends ModelWrapper {
	private String player;
	private TennisGamePredictionNetwork tn;

	@Override
	public void init(String player, String tillTime) {
		this.player = player;
		
		TennisGameTrainingSetGenerator t = new TennisGameTrainingSetGenerator();
		
		// Get DataSet
		DataSet ds = t.generateDataSet(player, tillTime, 1);
		
		// Train Network
		tn = new TennisGamePredictionNetwork(1, 6, 1);
		
		Loggar.logln("TennisGamePredictionNetwork start training with " + ds.size() + " rows!");
		
		tn.train(ds);
		
		Loggar.logln("TennisGamePredictionNetwork training finished!!");
	}

	@Override
	public double predictGame(String gameId) {
		double[] input = TennisGameTrainingSetGenerator.generateInputFromGameId(gameId, player);
		
		double[] newInput = new double[]{input[0]};
		
		tn.printInput(newInput);
		
		return tn.calculate(newInput)[0];
	}

}
