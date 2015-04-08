package nn;

import org.neuroph.core.Layer;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;


/*
 * 		INPUT: ENEMY RANKING, ROUND (PREFINAL / FINAL), INDOOR / OUTDOOR, HARD / SOFT
 * 
 * 		OUTPUT: WIN / LOSS, DEUTLICH / WENIGER DEUTLICH
 * 
 * 
 * 		ZU PERFORMANCE: PREFINAL, SEMI FINAL, FINAL
 * 		MEHR GEWICHTET: LETZTE SPIELE
 * 		MEHR GEWICHTET: SPIELE GEGEN DEN GEGNER
 * 
 */
public class TennisGamePredictionNetwork {
	private MultiLayerPerceptron neuralNet;
	private BackPropagation learningRule;
	
	public TennisGamePredictionNetwork(){
		neuralNet = new MultiLayerPerceptron(5, 8, 2);
		
		learningRule = neuralNet.getLearningRule();
		learningRule.setLearningRate(0.05);
		learningRule.setMaxIterations(10000000);
	}
	
	public void train(DataSet trainingSet){
		neuralNet.learn(trainingSet, learningRule);
	}
	
	public double[] calculate(double[] input){
		neuralNet.setInput(input);
        neuralNet.calculate();
        
        return neuralNet.getOutput();
	}
}
