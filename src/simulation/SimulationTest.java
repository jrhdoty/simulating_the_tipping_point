package simulation;

import probabilityDensityFunction.ProbabilityDensityFunction;

public class SimulationTest {
	public static void main(String args[]){
		
		//generate probability degree sequences
		ProbabilityDensityFunction myProb = new ProbabilityDensityFunction();
		
		
		/*
	    TPSimulation(int numAgents, int numCarriers, int initialize, 
		int[] c_distribution, int[] s_distribution, int[] m_distribution, 
		int cycles, int percentCarriers, int numRuns)
	 */


                /*int distribution = 1;
                int mean = 50;
                int stddev = 20;
                int max = 300;
                int min = 3;
                double exp = 4; */

                //0: normal; 1: pareto; 2: uniform
		int distribution = 1;
		int numAgents = 1000;
		int numCarriers = 300;
		int initialize = 1;
		int[] c_dist = null;
		int[] s_dist = null;
		int[] m_dist = null;

                //connector
		int c_mean = 50;
		int c_stddev = 20;
		int c_max = 300;
		int c_min = 3;
                int c_exponent = 4;

                //salesman and mavens
		int s_mean = 23;
		int s_stddev = 15;
		int s_max = 100;
		int s_min = 5;
                double s_exponent = 2.5;

                //run specifications
                int cycles = 10000;

		switch(distribution){
		case 0: //normal
			//popSize,  mean,  standardDev,  max,  min
			c_dist = myProb.roundedNormal(numAgents, c_mean, c_stddev, c_max, c_min);
			s_dist = myProb.roundedNormal(numAgents, s_mean, s_stddev, s_max, s_min);
			m_dist = myProb.roundedNormal(numAgents, s_mean, s_stddev, s_max, s_min);
		break;
		
		case 1: //power
			//int popSize, double exponent, double max, double min
			c_dist = myProb.roundedPareto(numAgents, c_exponent, c_max, c_min);
			s_dist = myProb.roundedPareto(numAgents, s_exponent, s_max, s_min);
			m_dist = myProb.roundedPareto(numAgents, s_exponent, s_max, s_min);
		
		break;
		case 2: //uniform
			//int popSize, double max, double min
			c_dist = myProb.roundedUniform(numAgents, c_max, c_min);
			s_dist = myProb.roundedUniform(numAgents, s_max, s_min);
			m_dist = myProb.roundedUniform(numAgents, s_max, s_min);
		break;
		}
		
	
		/*
	    TPSimulation(int numAgents, int numCarriers, int initialize, 
		int[] c_distribution, int[] s_distribution, int[] m_distribution, 
		int cycles, int percentCarriers, int numRuns)
	 */
		TPSimulation mySim = null; 
		int count = 0;
		boolean setup = false;
		while(!setup){
			System.out.println("ITERATION NUMBER: " + count);
			c_dist = myProb.roundedNormal(numAgents, c_mean, c_stddev, c_max, c_min);
			mySim = new TPSimulation(numAgents, numCarriers, initialize, c_dist, s_dist, m_dist, cycles, 95, 1);
			setup = mySim.initializeGraph();
			count++;
		}
		
		System.out.println("INITIALIZE AGENTS");
		mySim.initializeAgents();
		System.out.println("\nRUN SIMULATION");
		mySim.run();
	
	System.out.println("TERMINATION OF TEST");
}
}
