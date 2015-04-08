package nn;

import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.learning.BackPropagation;

/**
 * This sample shows how to create, train, save and load simple Perceptron neural network
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class TestNetwork {

    /**
     * Runs this sample
     */
    public static void main(String args[]) {
 
            // create training set (logical AND function)
            DataSet trainingSet = new DataSet(2, 1);
            trainingSet.addRow(new DataSetRow(new double[]{0.85, 1.0}, new double[]{1}));
            trainingSet.addRow(new DataSetRow(new double[]{0.8, 1.0}, new double[]{0}));
            //trainingSet.addRow(new DataSetRow(new double[]{0, 0}, new double[]{1}));
            //trainingSet.addRow(new DataSetRow(new double[]{0, 0}, new double[]{1}));
            trainingSet.addRow(new DataSetRow(new double[]{0.83, 1.0}, new double[]{1}));

            // create perceptron neural network
            //NeuralNetwork myPerceptron = new Perceptron(2, 1);
            MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(2, 4, 1);
            
            BackPropagation bp = neuralNet.getLearningRule();
            
            bp.setLearningRate(0.1);
            bp.setMaxIterations(100000000);
            
            //System.out.println("MaxError: " + bp.get);
            
            // learn the training set
           // myPerceptron.learn(trainingSet);
            
            //neuralNet.learn(trainingSet);
			neuralNet.learn(trainingSet, bp);
            
            // test perceptron
            System.out.println("Testing trained perceptron");
            
           // testNeuralNetwork(myPerceptron, trainingSet);
            testNeuralNetwork(neuralNet, trainingSet);
            printInput(new double[]{0.82, 1.0},neuralNet);
            
            // save trained perceptron
          //  myPerceptron.save("mySamplePerceptron.nnet");
            // load saved neural network
           // NeuralNetwork loadedPerceptron = NeuralNetwork.load("mySamplePerceptron.nnet");
            // test loaded neural network
            //System.out.println("Testing loaded perceptron");
            //testNeuralNetwork(loadedPerceptron, trainingSet);

    }
    
    public static void printInput(double[] input, NeuralNetwork neuralNet){
    	neuralNet.setInput(input);
        neuralNet.calculate();
        double[] networkOutput = neuralNet.getOutput();

        System.out.print("Input: " + Arrays.toString(input) );
        System.out.println(" Output: " + Arrays.toString(networkOutput) );
    }

    /**
     * Prints network output for the each element from the specified training set.
     * @param neuralNet neural network
     * @param testSet data set used for testing
     */
    public static void testNeuralNetwork(NeuralNetwork neuralNet, DataSet testSet) {
        for(DataSetRow trainingElement : testSet.getRows()) {
        	printInput(trainingElement.getInput(), neuralNet);
        }
    }
}
