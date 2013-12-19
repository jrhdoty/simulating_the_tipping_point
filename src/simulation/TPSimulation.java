package simulation;
import graphAlgorithms.Graph;

import java.util.Arrays;
import java.util.Random;

//import cern.jet.random.Normal;

import probabilityDensityFunction.ProbabilityDensityFunction;
import agent.Agent;
import java.util.ArrayList;

public class TPSimulation {
	
	//array of all agents
	Agent[] population;
	//number of agents
	private int numAgents;
	//number of initial carriers
	private int numCarriers;
	//randomize initial carriers or make initial carriers our special few 
	//0 for random; 1 for connectors; 2 for all 
	private int initialize;
	private int[] arrayWithoutReplacement;
	
	
	//arrays containing discrete points from probability distribution
	private int[] c_distribution;
	private int[] sorted_c_distribution;
	private int[] s_distribution;
	private int[] sorted_s_distribution;
	private int[] m_distribution;
	private int[] sorted_m_distribution;
	
	//termination criteria
	//max number of cycles 
	private int cycles;
        private int cyclesPerformed;
	//% saturation before termination
	private double percentCarriers;
	private int saturationNumber;
        private int currentCount;
	//number of runs to conduct with these parameters
	//private int numRuns;
	
	//private int numRunsCompleted;
	
	ProbabilityDensityFunction myProb;
	Graph myGraph;
	Random myRandom;
	
	
	/**Constructor for simulation class assuming external generation of probability distributions
	 * @param number of agents
	 * @param number of initial carriers
	 * @param who to initialize(0 = random; 1 = top connectors;
	 * @param connectedness distribution
	 * @param salesmanship distribution
	 * @param mavenhood distribution
	 * @param terminate with number of cycles
	 * @param terminate with percent carriers
	 * @param conduct number of runs
	 */
	public TPSimulation(int numAgents, int numCarriers, int initialize,
			int[] c_distribution, int[] s_distribution, int[] m_distribution, 
			int cycles, double percentCarriers, int numRuns)
		{
			//assign parameters to members
			this.numAgents = numAgents;
			this.numCarriers = numCarriers;
			this.initialize = initialize;
			
			this.c_distribution = c_distribution;
			this.s_distribution = s_distribution;
			this.m_distribution = m_distribution;
			
			sorted_c_distribution = sortArray(c_distribution);
			sorted_s_distribution = sortArray(s_distribution);
			sorted_m_distribution = sortArray(m_distribution);
			
			this.cycles = cycles;
                        cyclesPerformed = 0;
			this.percentCarriers = percentCarriers;
                        saturationNumber = (int)(numAgents* (percentCarriers/100));
                        currentCount = 0;
			//this.numRuns = numRuns;
			
			//initialize classes
			myRandom = new Random((int)System.currentTimeMillis());
			myProb = new ProbabilityDensityFunction();
			
			//initialize agent array
			population = new Agent[numAgents];
			arrayWithoutReplacement = new int[numAgents];
			
			for(int ii = 0; ii < numAgents; ii++){
				arrayWithoutReplacement[ii] = ii;
			}
		}
	
	/**
	 * function to initialize run of simulation
	 * @param fileName - file for run data output
	 */

        public ArrayList quickRun(){
            ArrayList data = new ArrayList();
            
            System.out.println("NUMBER OF INITIAL CARRIERS IS: " + numCarriers);
		//iterate through list of agents,
		//for each cycle each agent will interact with 1 random neighbor
                int count;
		Agent me, neighbor;
		for(int ii = 0; ii < cycles; ii++){
			for(int kk = 0; kk < numAgents; kk++){
				me = population[kk];
				neighbor = population[myGraph.getRandomNeighbor(kk)];
				me.dynamicInteraction(neighbor);
	}

                count = 0;
		for(int jj = 0; jj < numAgents; jj++){
			if(population[jj].isCarrier())
				count++;
		}
                
                data.add(count);

                //check other termination criteria
                if(count > saturationNumber){
                    System.out.format("\nREACHED SATURATION NUMBER IN %d CYCLES", ii);
                    break;
                }
                
                if(count == 0){
                    System.out.format("\nCHANGE HAS BEEN DEFEATED IN %d CYCLES", ii);
                    break;
                }

            }
		count = 0;
		for(int ii = 0; ii < numAgents; ii++){
			if(population[ii].isCarrier())
				count++;
		}
		System.out.println("\nNUMBER OF FINAL CARRIERS IS: " + count);
                
                return data;

        }

	public void run(){
		System.out.println("NUMBER OF INITIAL CARRIERS IS: " + numCarriers);
		//iterate through list of agents, 
		//for each cycle each agent will interact with 1 random neighbor
                int count;
		Agent me, neighbor;
		for(int ii = 0; ii < cycles; ii++){
			for(int kk = 0; kk < numAgents; kk++){
				me = population[kk];
				neighbor = population[myGraph.getRandomNeighbor(kk)];
				me.dynamicInteraction(neighbor);
	}
                count = 0;
		for(int jj = 0; jj < numAgents; jj++){
			if(population[jj].isCarrier())
				count++;
		}
                if(ii%100 == 0)
                    System.out.format("%d,\n", count);
                else
                    System.out.format("%d,", count);
                
                //check other termination criteria
                if(count > saturationNumber){
                    System.out.format("\nREACHED SATURATION NUMBER IN %d CYCLES", ii);
                    break;
                }
                if(count == 0){
                    System.out.format("\nCHANGE HAS BEEN DEFEATED IN %d CYCLES", ii);
                    break;
                }

            }
		count = 0;
		for(int ii = 0; ii < numAgents; ii++){
			if(population[ii].isCarrier())
				count++;
		}
		System.out.println("\nNUMBER OF FINAL CARRIERS IS: " + count);
}

        //runs one cycle at a time, outputting the number of agents carrying
        //the message after the cycle
        //checks for termination criteria
        //if criteria has been reached
        //outputs -1 if cycle limit
        //outputs -2 if % reached
	public int cycle(){

            //check termination conditions
            //if finished total cycles
            if(cyclesPerformed >= cycles)
                return -1;
            //if no agents are carriers
            if(currentCount <= 0)
                return -2;
            
            //if reached saturation
            if(currentCount >= saturationNumber)
                return -3;

            //perform cycle
            for(int kk = 0; kk < numAgents; kk++){
                Agent me, neighbor;
		me = population[kk];
		neighbor = population[myGraph.getRandomNeighbor(kk)];
		me.dynamicInteraction(neighbor);
	}

            //get count
            int count = 0;
            for(int jj = 0; jj < numAgents; jj++){
		if(population[jj].isCarrier())
                    count++;
            }
            //update cycles and currentCount, then return
            cyclesPerformed++;
            currentCount = count;
            
            return count;
        }
	
	/**
	 * Initialize the agents, assigning them levels of salesmanship and mavenhood
	 * as well as disseminating the message to a specific number of agents
	 */
	public void initializeAgents(){
		//initialize agents with characteristics
		for(int ii = 0; ii < numAgents; ii++){
			population[ii] = new Agent(s_distribution[ii], m_distribution[ii], sorted_c_distribution[ii]); //s, m, c
		}
		
		//initialize first message carriers with message
		switch(initialize){
		case 0:		//random initialization
			shuffleArray();
			for(int ii= 0; ii < numCarriers; ii++)
				population[arrayWithoutReplacement[ii]].takeMessage();
			break;
		
		case 1:		//initialize the connectors
			//initialize
			initializeConnectors(numCarriers);
			break;
		case 2:
			int c = numCarriers/3;
			initializeConnectors(c);
			int s = (numCarriers - c)/2;
			initializeSalesman(c);
			int m = numCarriers - (c + s);
			initializeMavens(m);
			break;
		}
                currentCount = numCarriers;
	}
	
	/**
	 * Generate the Graph - if the graph cannot be realized will return false.
	 * 
	 */
	public boolean initializeGraph(){
		myGraph = new Graph(c_distribution);
		if(!(myGraph.generateGraph())){
			System.out.println("Graph Could Not Be Generated\n");
			return false;
		}
		return true;
	}


	private void shuffleArray(){
		int temp, rand;
		
		for(int ii = 0; ii < numAgents; ii++){
			temp = arrayWithoutReplacement[ii];
			rand = myRandom.nextInt(numAgents);
			arrayWithoutReplacement[ii] = arrayWithoutReplacement[rand];
			arrayWithoutReplacement[rand] = temp;
			
		}
	}
	
	//returns a sorted array (greatest to least) containing the same elements as input array
	private  int[] sortArray(int[] myArray){
		//this sorts it into ascending numerical order
		int length = myArray.length;
		int[] temp = new int[length];
		System.arraycopy(myArray, 0, temp, 0, length);
		Arrays.sort(temp);
		int[] output = new int[length];
		int k = length-1;
		//this swaps it so that it is in descending numerical order
		for(int ii = 0; ii < length; ii++){
			output[ii] = temp[k - ii];
		}
		return output;
	}

	private void initializeConnectors(int num){
		int count = 0;
		int loc = 0;
		while(count < num && count < numAgents){
			if(!population[loc].isCarrier()){
				population[loc].takeMessage();
				count++;
			}
			loc++;
		}
	}

	private void initializeSalesman(int num){
		int count = 0;
		int locValue = 0;
		int loc;
		while(count < num && count < numAgents){
			loc = nextUnitializedSalesman(sorted_s_distribution[locValue]);
			if(!(loc == -1)){
				population[loc].takeMessage();
				count ++;
			}
			locValue++;	
		}
	}
	
	private void initializeMavens(int num){
		int count = 0;
		int locValue = 0;
		int loc;
		while(count < num && count < numAgents){
			loc = nextUnitializedMaven(sorted_m_distribution[locValue]);
			if(!(loc == -1)){
				population[loc].takeMessage();
				count ++;
			}
			locValue++;	
		}
	}


	private int nextUnitializedSalesman(int val){
		boolean found = false;
		int ii;
		for(ii = 0; ii < numAgents; ii++){
			if(population[ii].getSalesmanship() == val)
				if(!population[ii].isCarrier()){
					found = true;
					break;
				}
		}
			if(found)
				return ii;
			return -1;
	}
	
	private int nextUnitializedMaven(int val){
		boolean found = false;
		int ii;
		for(ii = 0; ii < numAgents; ii++){
			if(population[ii].getMavenhood() == val)
				if(!population[ii].isCarrier()){
					found = true;
					break;
				}
		}
			if(found)
				return ii;
			return -1;
	}

}
	 
