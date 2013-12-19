package graphAlgorithms;

import java.util.Random;

import probabilityDensityFunction.ProbabilityDensityFunction;

public class GraphTester {
	public static void main(String args[]){	
		
		
		long startTime = System.currentTimeMillis();

		
		for(int mm = 0; mm < 1000; mm++){
			System.out.printf("ITERATION NUMBER: %d \n", mm);
		
		ProbabilityDensityFunction myFunct = new ProbabilityDensityFunction();
		int popTest = 100;
		int distributionTest[]; 
		distributionTest = myFunct.roundedNormal(popTest, 15, 5, 40, 3);
		System.out.println("Distribution Length is: " + distributionTest.length);
		
		Graph myGraph = new Graph(distributionTest);
		if(myGraph.generateGraph()){
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			System.out.println(elapsedTime);
			break;
		}

		}
		System.out.println("PROGRAM TERMINATED");
	}
};


