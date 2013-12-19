package simulation;

import cern.jet.random.Distributions;
import cern.jet.random.Normal;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;
import java.lang.Math;

public class CernTest {
	public static void main(String args[]){
		RandomEngine myRandEngine = new DRand((int) System.currentTimeMillis());
		Normal myNorm = new Normal(30, 10, myRandEngine);
		for(int ii = 0; ii < 100; ii++){
			int output = Math.round(Math.round(myNorm.nextDouble()));
			System.out.println("OUTPUT IS: " + output + "\n");
		}
	
	double cut = 200.0;
	double max = 0;
	double min = cut;
	double test;
	int dist1 = 0;
	int dist2 = 0;
	for(int ii = 0; ii < 10000; ii++){
		test = Distributions.nextPowLaw(2.0, cut, myRandEngine);
		if(test > max) max = test;
		if(test < min) min = test;
		if(test < cut/10) dist1++;
		if(test > cut - cut/10) dist2++;
		System.out.println("Power-Law Output is: " + test + "\n");
	}
	System.out.println("Max Value was: " + max + "\n");
	System.out.println("Min Value was: " + min + "\n");
	System.out.println("Number of nodes < cut/10: " + dist1 + "\n");
	System.out.println("Number of nodes > cut - cut/10: " + dist2 + "\n");
	
	
	System.out.println("CAST NUM TEST");
	int myInt = Math.round(Math.round(199.9));
	System.out.println("INT IS: " + myInt);
}}
