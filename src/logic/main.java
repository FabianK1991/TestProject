package logic;

import nn.TennisGamePredictionNetwork;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

public class main {
    /**
     * Runs this sample
     */
    public static void main(String args[]) {
    	// INPUT: ENEMY PERFORMANCE LAST 5 GAMES, ENEMY RATING, PERFORMANCE LAST 5 GAMES, HARD COURT, CLAY COURT
    	DataSet trainingSet = new DataSet(5, 2);
    	
        trainingSet.addRow(new DataSetRow(new double[]{0.9, 0.9, 0.1, 0, 1}, new double[]{0, 1}));
        trainingSet.addRow(new DataSetRow(new double[]{0.2, 0.2, 0.6, 0, 1}, new double[]{1, 0.8}));
        trainingSet.addRow(new DataSetRow(new double[]{0.6, 0.9, 0.8, 1, 0}, new double[]{0, 0.2}));
        trainingSet.addRow(new DataSetRow(new double[]{0.4, 0.9, 0.4, 1, 0}, new double[]{0, 0.2}));
        trainingSet.addRow(new DataSetRow(new double[]{0.4, 0.1, 0.4, 1, 0}, new double[]{1, 0.2}));
        
        TennisGamePredictionNetwork net = new TennisGamePredictionNetwork();
        
        net.train(trainingSet);
        
        double[] out = net.calculate(new double[]{0.2, 0.1, 0.4, 1, 0});
        
        System.out.println("Prediction(Win/Loss): " + out[0] + " Clear: " + out[1]);
    }
}
