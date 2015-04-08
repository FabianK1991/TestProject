package nn;

import java.util.Arrays;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;


/*
 * 		INPUT: ENEMY RANKING, ROUND (PREFINAL / FINAL), INDOOR / OUTDOOR, HARD / SOFT
 * 
 * 		OUTPUT: WIN / LOSS //, DEUTLICH / WENIGER DEUTLICH
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
		neuralNet = new MultiLayerPerceptron(4, 5, 1);
		
		adjustLerningRule();
	}
	
	public TennisGamePredictionNetwork(int incoming, int hidden, int out){
		neuralNet = new MultiLayerPerceptron(incoming, hidden, out);
		
		adjustLerningRule();
	}
	
	private void adjustLerningRule(){
		learningRule = neuralNet.getLearningRule();
		learningRule.setLearningRate(0.1);
		learningRule.setMaxError(0.1);
		learningRule.setMaxIterations(100000);
	}
	
	public void train(DataSet trainingSet){
		neuralNet.learn(trainingSet, learningRule);
	}
	
	public double[] calculate(double[] input){
		neuralNet.setInput(input);
        neuralNet.calculate();
        
        return neuralNet.getOutput();
	}
	
	public void printInput(double[] input){
    	neuralNet.setInput(input);
        neuralNet.calculate();
        double[] networkOutput = neuralNet.getOutput();

        System.out.print("Input: " + Arrays.toString(input) );
        System.out.println(" Output: " + Arrays.toString(networkOutput) );
    }
}
