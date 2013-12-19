package probabilityDensityFunction;



import cern.jet.random.Distributions;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;

import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;

public class ProbabilityDensityFunction {
	
	//distribution properties
	//private double min, max, mean, range;
	//private int lastArray[];
	private RandomEngine myRandEngine;
	private Normal myNorm;
	private Uniform myUni;

	
	//constructor
	public ProbabilityDensityFunction(){
		myRandEngine = new DRand((int) System.currentTimeMillis());
		myNorm = new Normal(0, 1, myRandEngine);
		myUni = new Uniform(myRandEngine);
	}
	
	/**
	 * Rounds random doubles produced by a normal distribution to integers.
	 * @param popSize - size of output array
	 * @param mean
	 * @param standardDev
	 * @param max - maximum value allowed in output array (inclusive)
	 * @param min - minimum value allowed in output array (inclusive)
	 * @return an array of random integers based upon a normal distribution
	 */
	public int[] roundedNormal(int popSize, double mean, double standardDev, double max, double min){
		int[] distArray = new int[popSize];
		int count = 0;
		int output;
		while(count < popSize){
			output = Math.round(Math.round(myNorm.nextDouble(mean, standardDev)));
			if(min <= output && output <= max){
				distArray[count] = output;
				//System.out.println("OUTPUT IS: " + output + "\n");
				count ++;
			}
		}
		return distArray;
	}
	
	public int[] roundedPareto(int popSize, double exponent, double max, double min){
		int[] distArray = new int[popSize];
                int range = (int)(max - min);
		for(int ii = 0; ii < popSize; ii++){
			distArray[ii] = (int)max - Math.round(Math.round(Distributions.nextPowLaw(exponent, range, myRandEngine)));
		}
		return distArray;
	}
	
	
	public int[] roundedUniform(int popSize, double max, double min){
		int[] distArray = new int[popSize];
		int thisMax = (int)max;
		int thisMin = (int)min;
		for(int ii = 0; ii < popSize; ii++){
			distArray[ii] = myUni.nextIntFromTo(thisMin, thisMax);
		}
		return distArray;
	}
};
