package probabilityDensityFunction;

import java.util.Arrays;



public class ProbabilityTester {
	public static void main(String args[]){

		ProbabilityDensityFunction myProb = new ProbabilityDensityFunction();
		int size =1000;
		int[] test = null;

		//0 is normal; 1 is Pareto; 2 is Uniform
		int distribution = 1;
                int mean = 23;
                int stddev = 15;
                int max = 100;
                int min = 10;
                double exp = 2;
                String name = "";
                


		switch(distribution){
		case 0:
                    name = "NORMAL";
		for(int ii = 0; ii < size; ii++)
			test = myProb.roundedNormal(size, mean, stddev, max, min);
		break;
		
		case 1:
                    name = "POWER";
		for(int ii = 0; ii < size; ii++)
			test = myProb.roundedPareto(size, exp, max, min);
		break;
		
		case 2:
                    name = "UNIFORM";
			for(int ii = 0; ii < size; ii++)
				test = myProb.roundedUniform(size, max, min);
		}

                //sort array
                Arrays.sort(test);

		System.out.format("OUTPUT FOR %s IS: ", name);
		for(int ii = 0; ii < size; ii++){
			System.out.print(test[ii]); System.out.print(", ");
		}
		
		int sum = 0;
		for(int ii = 0; ii < size; ii++)
			sum += test[ii];
		sum /= size;	          
			System.out.println("\nTHE MEAN IS: " + sum);
	}
};
