package agent;

//import java.util.ArrayList;
//import java.util.List;
import java.util.Random;

public class Agent {
	//private variables
	
	//tipping point properties
	private int salesmanship;
	private int mavenhood;
	//private int connectedness;
	
	//agents state properties
	private boolean messageCarrier;
	//private int numberGaveMessage;
	
	private static Random myRand;
	
	//agent constructor
	public Agent(int s, int m, int c){
		salesmanship = s;
		mavenhood = m;
	//	connectedness = c;
		myRand = new Random();
	//	numberGaveMessage = 0;
		messageCarrier = false;
	}
	
	
	/*///////////////////////////////////////////////////////
	 * 
	 * 	PUBLIC METHODS TO BE USED BY SIMULATION CLASS
	 * 
	 *///////////////////////////////////////////////////////
	/**
	 * Two agents interact to see if they will pass the message to each other
	 * @param neighboring agent is passed as a parameter
	 * @return an integer indicating the result of the interaction 
	 * (0 neither has message; 1 successful transference of message; 
	 * 2 attempt but failure to pass message; 4 both already had message)
	 * 
	 */
	
	public int interact(Agent neighbor){
		//test if both have or don't have message
		boolean neighborCarrier = neighbor.isCarrier();
		if(!neighborCarrier && !messageCarrier)
			return 0;
		
		else if (neighborCarrier && messageCarrier)
			return 4;
		
		//if this is the message carrier see if passes message to neighbor
		if(messageCarrier){
			if(neighbor.acceptMessage(salesmanship)){
				return 1;
			}
		}
		//else if neighbor is the carrier see if it passes it to us
		else{
			if(acceptMessage(neighbor.getSalesmanship())){
				return 1;
			}
		}
		//return 2 if attempted but failed to pass message
		return 2;
}

        public int dynamicInteraction(Agent neighbor){
            boolean neighborCarrier = neighbor.isCarrier();
            //unintersted if neighther or both are carrier
            if((!neighborCarrier && !messageCarrier) ||
                    neighborCarrier && messageCarrier)
			return 0;
            
            //otherwise we try to convince eachother, I begin because was instigator of interaction
                if(neighbor.dynamicAcceptMessage(salesmanship, messageCarrier) ||
                dynamicAcceptMessage(neighbor.getSalesmanship(), neighbor.isCarrier()))
                       return 1;    //someone was convinced 
            
            return 0;
        }

        //they are trying to convince me of their stance
        //return true if influenced and changed, false otherwise
        private boolean dynamicAcceptMessage(int sales, boolean carrier){
            int prob = mavenhood + sales;
            //if they convince me I change my carrier status
            if(prob > myRand.nextInt(200)){
                if(carrier){
                    messageCarrier = true;
                }
                else{
                    messageCarrier = false;
                }
                return true;
            }
            return false;
        }

	/**
	 * Performs probabilistic test to see if this will become a message carrier based upon this mavenhood and neighbors salesmanship
	 * @param salesmanship of neighbor
	 * @return true if this accepts the message
	 */
	private boolean acceptMessage(int sales){
		int prob = mavenhood + sales;
		if(prob > myRand.nextInt(200)){
			messageCarrier = true;
			return true;
		}
		return false;	                 
	}
	

	/**
	 * Query if it is a carrier of message
	 * @return return true if carrier
	 */
	public boolean isCarrier(){return messageCarrier;}
	
	/**
	 * Query for the level of Salesmanship of Agent
	 * @return an integer representing sales ability
	 */
	public int getSalesmanship(){return salesmanship;}
	
	public int getMavenhood(){return mavenhood;}
	
	/**
	 * Automatically endow agent with the message
	 */
	public void takeMessage(){
		messageCarrier = true;
	}
	
		
}


