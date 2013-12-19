
/*
 * This graph class is used as an abstract representation of a social network
 * it has to main functions
 * 	1) 	It takes as input an array that contains the degree sequence of a graph
 * 		if it is possible to create a simple connected graph with this degree sequence
 * 		the class will do so otherwise it will report that it is impossible
 * 	2)	The graph class can be queried by the simulation class for information about
 * 			a) the existence of edges between nodes
 * 			b) the degree of a vertex
 * 			c) the shortest path b/w two vertices
 */

package graphAlgorithms;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Graph {
	
	//number of vertices, degree sequence, probability array for selecting random vertex
	private int numV, deg[], Prob[];
	private int totalEdges;
	//set of all vertices
	private HashSet<Integer> vertices = new HashSet<Integer>();
	//list of adjacencyLists for each vertex
	private List<HashSet<Integer>> adjacencyList = new ArrayList<HashSet<Integer>>(); 
	//random number generator
	private Random myRand;
	
	//used for optimized edge swap algorithm
	int T;
	
	//CONSTRUCTOR
	public Graph(int Deg[]){
		
		myRand = new Random();
		//set the number of vertices and copy the degree sequence array
		numV = Deg.length;
		this.deg = new int[numV];
		System.arraycopy(Deg, 0, this.deg, 0,numV);
		
		//set up the probability array based upon degree sequence
		//start by creating array of length = 2* total number of edges
		int sum = 0;
		for(int ii = 0; ii < numV; ii++)
			sum += Deg[ii];  
		totalEdges = sum;
		Prob = new int[sum];
		
		
		//fill in the values for Prob
		int loc = 0;
		for(int ii = 0; ii < numV; ii++){
			int val = Deg[ii];
			for(int kk = 0; kk < val; kk++){
				Prob[loc] = ii;
				loc++;
			}
		}
		
		//set up adjacencyList and list of vertices
		for(int ii = 0; ii < numV; ii++){
			adjacencyList.add(new HashSet<Integer>());
			vertices.add(ii);
		}
		System.out.println("size of adjacencyList is " + adjacencyList.size());
		
		T = 1;	
				
};

/*///////////////////////////////////////////////////////
 * 	This is the set of functions used to generate the 
 * 	simple connected graph given a degree sequence as
 * 	a parameter.  This process has three steps:
 * 		1) 	Realize the Sequence (Havel-Hakimi)
 * 		2) 	Connect the graph without changing its vertex 
 * 			degrees
 * 		3) 	Shuffle the edges to make it random while 
 * 			keeping it connected and simple
 * 
 * (note: the functions are organized by first listing the
 * main function of each of the three steps as well as 
 * functions specific to them, then all of the general 
 * utility functions and public functions)
 *///////////////////////////////////////////////////////


/*///////////////////////////////////////////////////////
 * 
 * 	STEP 1: REALIZE THE SEQUENCE (HAVEL-HAKIMI ALGORITHM)
 * 
 *///////////////////////////////////////////////////////

//realize the sequence using the Havel-Hakimi algorithm
private boolean HavelHakimi(){
	//sort array from largest to smallest
	sortArray();
	//make a copy of the degree array
	int [] degCopy = (int[]) deg.clone();
	
	int end = numV;
	for(int ii = 0; ii < numV; ii++){
		int count = degCopy[ii];
		for(int kk = ii +1; count > 0 && kk < end; kk++){
			if(deg[kk] > 0){
				addEdge(ii, kk);
				count--;
				degCopy[kk]--;
			}}
		if(count != 0){
			System.out.println("COUNT DID NOT = 0 in H-H");
			return false;
		}}
	return true;
};

//return true if a vertex has reached its degree
private boolean reachedDegreeQuota(int v1){
	HashSet<Integer> v1List = adjacencyList.get(v1);
	if(deg[v1] <= v1List.size())
		return true;
	return false;
}



//return true if successfully connected graph
private boolean connectGraphOpt(){
	//get separate components
	List<Integer> trees = new ArrayList<Integer>();
	List<ArrayList<Edge>> cyclicComponents = new ArrayList<ArrayList<Edge>>();
	getComponents(trees, cyclicComponents);
	
	//get total number of components
	int numTrees = trees.size();
	int numCyclicComp = cyclicComponents.size();
	int numComponents = numTrees + numCyclicComp;
	
	//if only trees and no redundant edges, cannot connect
	if(numTrees > 1 && numComponents <= 0) return false;
	
	//first connect all trees to components with redundant edges
	int currentTree; 
	int neighbor;
	ArrayList<Edge> currentComponent;
	Iterator<Edge> myIter;
	currentComponent = cyclicComponents.remove(0);
	myIter = currentComponent.iterator();
	Edge currentEdge;
	while(numTrees > 0){
		//get a vertex of a tree component and one of its neighbors
		currentTree = trees.remove(0);
		neighbor = getRandomNeighbor(currentTree);
		//get a redundant edge
		currentEdge = myIter.next();
		edgeSwap(currentEdge.to, currentEdge.from, currentTree, neighbor);
		//if myIter has more redundant edges, great keep using them
		//otherwise set the component that was just created into the tree category
		//and start using the redundant edges of a new component to connect trees
		//if out of removable components return false
		if(!myIter.hasNext()){
			trees.add(currentTree);
			numCyclicComp--;
			if(numCyclicComp <= 0)
				return false;
			currentComponent = cyclicComponents.remove(0);
			myIter = currentComponent.iterator();
		}
		else{
			if(!currentComponent.remove(currentEdge)){
				System.out.println("ERROR WITH TREE CONNECTIONS"); return false;}
			numTrees--;
		}	
	}
	
	//now we need to connect the remaining components starting where we left off with trees
	//what we have to work with
		//list of lists of redundant edges for components  (cyclicComponents)
		//current component (currentComponent)
		//iterator with currentComponent's edges which must have at least one more edge
	currentEdge = myIter.next();
	ArrayList<Edge> nextComp;
	Edge nextEdge;
	while(numCyclicComp > 1){
		nextComp = cyclicComponents.remove(0);
		nextEdge = nextComp.remove(0);
		edgeSwap(currentEdge.to, currentEdge.from, nextEdge.to, nextEdge.from);
		currentEdge = new Edge(nextEdge.to, currentEdge.from);
		numCyclicComp--;
	}
	
	if(numCyclicComp == 1)
		return true;
	System.out.println("ERROR DID NOT UNIFY GRAPH");
	return false;
}

//used after Havel-Hakimi in order to determine which components need to be connected
//trees are kept track of with one node, non-tree components are labeled by a list of their removable edge
private void getComponents(List<Integer> trees, List<ArrayList<Edge>> removableEdges){
	//make a copy of the set of all vertices
	HashSet<Integer> copyAll = new HashSet<Integer>(vertices);
	HashSet<Integer> visited = new HashSet<Integer>();
	Iterator<Integer> myIter; 
	
	while(!copyAll.isEmpty()){
		ArrayList<Edge> remEdge = new ArrayList<Edge>();
		myIter = copyAll.iterator();
		int v = myIter.next();
		if(!(depth_first_search_rec(visited, v, -1, remEdge)))
			trees.add(v);
		else
			removableEdges.add(remEdge);
		copyAll.removeAll(visited);
	}
	System.out.println("SUCCESSFULLY GOT COMPONENTS");
	
}


//straightforward BFS that returns connected vertices
//wrapper and the recursive function
private  HashSet<Integer> depth_first_search_wrapper(int v){
	HashSet<Integer> visited = new HashSet<Integer>();
	depth_first_search_basic_rec(visited, v, -1);
	return visited;
}

//basic BFS to see if its a tree and return the visited vertices
private boolean depth_first_search_basic_rec(HashSet<Integer> visited, int v, int parent){
	//if v has been visited before, then return
	if(visited.contains(v)) return true;
	
	//else add v to the list
	visited.add(v);
	
	boolean cycle = false;
	//iterate through neighbors of v and make recursive call on each
	HashSet<Integer> neighbors = adjacencyList.get(v);
	Iterator<Integer> myIter = neighbors.iterator();
	while(myIter.hasNext()){
		int next = myIter.next();
		if(next != parent)
			if(depth_first_search_basic_rec(visited, next, v))
				cycle = true;
				
	}
	//return
	return cycle;
}



//for complicated DFS for collecting removable edges for components
//returns true if there are removable edges, false if it is a tree
private boolean depth_first_search_rec(HashSet<Integer> visited, int v, int parent, 
									List<Edge> removeableEdges){
	//if v has been visited before, then return
	if(visited.contains(v)){
		removeableEdges.add(new Edge(v, parent));
		return true;
	}
	
	//else add v to the list
	visited.add(v);
	boolean cycle = false;
	//iterate through neighbors of v and make recursive call on each
	HashSet<Integer> neighbors = adjacencyList.get(v);
	Iterator<Integer> myIter = neighbors.iterator();
	while(myIter.hasNext()){
		int next = myIter.next();
		if(next != parent){
			if(depth_first_search_rec(visited, next, v, removeableEdges))
				cycle = true;
		}		
	}
	//return
	return cycle;
}


//returns true if the component connected to the given vertex is a tree
/*
private boolean isTree(int v){
	HashSet<Integer> visited = new HashSet<Integer>();
	return !(depth_first_search_rec(visited, v, -1));
}
*/

private boolean possibilityOfConnection(){
	//check one, m >= n-1
	if (totalEdges < (numV -1)){
		System.out.println("m < n-1");
		return false;
	}
	
	//check two, no vertex == 0
	for(int item : deg){
		if (item == 0){
			System.out.println("A vertex has degree of 0");
			return false;
		}}
	System.out.println("IT IS POSSIBLE TO CONNECT");
	return true;
}

/*///////////////////////////////////////////////////////
 * 
 * 	STEP 3: SHUFFLE THE EDGES
 * 
 *///////////////////////////////////////////////////////

//in this implementation, this function will attempt to perform m^2 edge swaps
private void swapNaive(){
	int callsToDepth = 0;
	int to1, from1, to2, from2;
	int count = totalEdges^2; 
	for(int ii = 0; ii < count; ii++){
	//select two non-redundant nodes
		to1 = getRandomVertex();
		to2 = getRandomVertex();
		if(to1 != to2){
			//get a random neighbor for each
			//System.out.println(to1);
			from1 = getRandomNeighbor(to1);
			from2 = getRandomNeighbor(to2);
			if(from1 != from2){
				//attempt to perform a swap
				//if the swap was successful
				if(edgeSwap(to1, from1, to2, from2)){
					//check to see that the graph is still connected
					callsToDepth++;
					if(!isConnected(0)){
						
						//if it is not connected
						//reverse the edge swap
						edgeSwap(to1, from2, to2, from1);
					}
				}
			}
		}
	}
	System.out.println("CALLS TO DEPTH: " + callsToDepth);
}

private void GkantsidisOptimization(){
	int callsToDepth = 0;
	Edge[][] steps;
	//Edge one, two;
	int swapNum;
	int to1, from1, to2, from2;
	int count = totalEdges^2; 
	
	for(int ii = 0; ii < count; ii++){
		//setup matrix to store swaps
		steps = new Edge[T][2];
		swapNum = 0;
		for(int kk = 0; kk < T; kk++){
			//select two non-redundant nodes
		to1 = getRandomVertex();
		to2 = getRandomVertex();
		if(to1 != to2){
			//get a random neighbor for each
			//System.out.println(to1);
			from1 = getRandomNeighbor(to1);
			from2 = getRandomNeighbor(to2);
			if(from1 != from2){
				//attempt to perform a swap
				//if the swap was successful
				edgeSwap(to1, from1, to2, from2);
				//store the original edges in backup array
				steps[swapNum][0] = new Edge(to1, from1);
				steps[swapNum][1] = new Edge(to2, from2);
				swapNum++;
			}
		}
		}
		callsToDepth++;
		//check to see that the graph is still connected
		if(!isConnected(0)){
			
			//if it is not connected
			//reverse all of the edge swap and T= T/2
			//System.out.println("BACKTRACKING SWAP");
			for(int kk = 0; kk < swapNum; kk++){
				//System.out.println("BACKTRACK NUMBER: " + kk);
				//System.out.format("VERTICES ARE: %d, %d, %d, %d \n", steps[kk][0].to, steps[kk][1].from, steps[kk][1].to, steps[kk][1].from);
				edgeSwap(steps[kk][0].to, steps[kk][1].from, steps[kk][1].to, steps[kk][1].from);
			}
			T = (T/2) + 1;
		}
		else{
			T +=1;
		}
		//System.out.println("T IS: " + T);
	}
	System.out.println("NUMBER OF CALLS TO DEPTH IS: " + callsToDepth);
}
				
					
						

private void swapKIso(){
	
	//make backup copy of graph
	long startTime = System.currentTimeMillis();
	List<HashSet<Integer>> copy = graphDeepCopy();
	long stopTime = System.currentTimeMillis();
	long elapsedTime = stopTime - startTime;
	System.out.println("TIME FOR DEEP COPY: " + elapsedTime);
	//set K to 1
	int K = 1;
	//number of swaps to perform
	int count = totalEdges^2; 
	int to1, from1, to2, from2;
	
	for(int ii = 0; ii < count; ii++){
	//select two non-redundant nodes
		to1 = getRandomVertex();
		to2 = getRandomVertex();
		if(to1 != to2){
			//get a random neighbor for each
			//System.out.println(to1);
			from1 = getRandomNeighbor(to1);
			from2 = getRandomNeighbor(to2);
			if(from1 != from2){
				//conduct simplicity test to make sure not creating redundant edge
				if(!(isEdge(to1, from2) || isEdge(to2, from1))){
					//attempt to perform a swap
					//if the swap was successful
					if(edgeSwap(to1, from1, to2, from2)){
						//conduct two K Isolation Tests; one on a member of each pair.
						//startTime = System.currentTimeMillis();
						if(!(KIsolationTest(K, to1) && KIsolationTest(K, to2)))
						{
							//if at least one of the tests failed
							//revert to old graph
							adjacencyList = copy;
							//increment K
							if(K < numV/2)
								K *= 2;
						}
						//elapsedTime += System.currentTimeMillis() - startTime;
					}
				}
			}
		}
	}
	System.out.println("TIME IN K TESTS: " + elapsedTime + "\n");
}


private boolean KIsolationTest(int K, int node){
	LinkedList<Integer> visited = new LinkedList<Integer>();
	visited.offer(node);
	int count = 0;
	while(count < K){
		//if there are not K components
		if(visited.isEmpty())
			return false;
		node = visited.remove();
		HashSet<Integer> neighbors = adjacencyList.get(node);
		for(Integer i : neighbors){
			if(!visited.contains(i)){
				visited.offer(i);
				count++;
			}		
		}
	}
	return true;
}



//swap a connection between two pairs of vertices
private boolean edgeSwap(int from1, int to1, int from2, int to2){
	//make sure that the graph will remain simple
	if(from1 == to1 || from1 == from2 || from1 == to2 || to1 == from2 || to1 == to2 || from2 == to2)
		return false;
	
	//get adjacencyLists for each vertex
	HashSet<Integer> f1 = adjacencyList.get(from1);
	HashSet<Integer> t1 = adjacencyList.get(to1);
	HashSet<Integer> f2 = adjacencyList.get(from2);
	HashSet<Integer> t2 = adjacencyList.get(to2);
	
	//remove edges from adjacencyList
	f1.remove(to1);
	t1.remove(from1);
	f2.remove(to2);
	t2.remove(from2);
	
	//add new edges to adjacencyList
	f1.add(to2);
	t1.add(from2);
	f2.add(to1);
	t2.add(from1);
	
	//update adjacencyList in the list
	adjacencyList.set(from1, f1);
	adjacencyList.set(from2, f2);
	adjacencyList.set(to1, t1);
	adjacencyList.set(to2, t2);
	
	return true;
}

/*///////////////////////////////////////////////////////
 * 
 * 	GENERAL UTILITY FUNCTIONS
 * 
 *///////////////////////////////////////////////////////

//	sort the array of degree sequences
private void sortArray(){
	//this sorts it into ascending numerical order
	Arrays.sort(deg);
	int k = numV-1;
	int[] temp = new int[numV];
	//this swaps it so that it is in descending numerical order
	for(int ii = 0; ii < numV; ii++){
		temp[ii] = deg[k - ii];
	}
	deg = temp;
}
	

//add an edge between two vertices
private boolean addEdge(int v1, int v2){
	//check to see that an edge does not aleady exist
	if(isEdge(v1, v2))
		return false;
	
	//check to see that the have not reached degree quota
	if(reachedDegreeQuota(v1) || reachedDegreeQuota(v2))
		return false;
	
	//add edge to both vertices' hash lists
	HashSet<Integer> v1List = adjacencyList.get(v1);
	HashSet<Integer> v2List = adjacencyList.get(v2);
	v1List.add(v2);
	v2List.add(v1);
	return true;	
}

private List<HashSet<Integer>> graphDeepCopy(){
	List<HashSet<Integer>> deepCopy = new ArrayList<HashSet<Integer>>(); 
	//for each element in ArrayList
	for(HashSet<Integer> set : adjacencyList){
		deepCopy.add(new HashSet<Integer>(set));
	}
	return deepCopy;
}

/*///////////////////////////////////////////////////////
 * 
 * 	PUBLIC FUNCTIONS
 * 
 *///////////////////////////////////////////////////////


//returns true if successfully generated a simple connected graph
public boolean generateGraph(){
	long startTime;
	long endTime;
	long elapsedTime;
	
	System.out.println("\nGENERATING GRAPH");
	//test to see if the graph was already generated
	if(isConnected(0)){
		System.out.println("GRAPH WAS PREVIOUSLY SUCCESSFULLY REALIZED");
		return true;
	}
	
	startTime = System.currentTimeMillis();
	if(HavelHakimi()) {
		endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;
		System.out.println("SUCCESSFUL REALIZATION OF SEQUENCE IN : " + elapsedTime);
		//printAdjacencyLists();
		printIsConnected();
		
		
		startTime = System.currentTimeMillis();
		if(connectGraphOpt()){
			endTime = System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			System.out.println("SUCCESSFUL CONNECTION OF GRAPH IN: " + elapsedTime);
			//printAdjacencyLists();
			printIsConnected();
			System.out.println("AttemptingSwaps");
			startTime = System.currentTimeMillis();
			swapKIso();
			//GkantsidisOptimization();
			//swapNaive();
			endTime = System.currentTimeMillis();
			elapsedTime = endTime - startTime;
			System.out.println("Finished Swaps In: " + elapsedTime);
		}
		else{
			System.out.println("FAILURE TO CONNECT GRAPH");
			return false;
		}
	}
	else{
		System.out.println("FAIL TO REALIZE SEQUENCE");
		return false;
	}
	
	return true;
};

//re-shuffle the graph with the same degree sequence
public void reshuffle(){
	if(isConnected(0)){
		swapNaive();
	}
}

//return a random vertex
public int getRandomVertex(){
	int v = myRand.nextInt(numV);
	//System.out.println(v);
	return v;
}


//returns a random neighbor of the parameter vertex
public int getRandomNeighbor(int v){
	HashSet<Integer> neighbors = adjacencyList.get(v);
	int position = myRand.nextInt(neighbors.size());
	Integer[] array = (Integer[])neighbors.toArray(new Integer[neighbors.size()]);
	return array[position];
}

//return true if there exists an edge between two vertices
public boolean isEdge (int v1, int v2){
	HashSet<Integer> v1List = adjacencyList.get(v1);
	HashSet<Integer> v2List = adjacencyList.get(v2);
	if(v1List.contains(v2) || v2List.contains(v1))
		return true;
	return false;
};

//returns true if every vertex in the graph is connected
public boolean isConnected(int v){
	//get all component vertices
	HashSet<Integer> componentVertices = depth_first_search_wrapper(v);
	//check if every vertex in the graph was a component
	if(componentVertices.containsAll(vertices))
		return true;
	return false;
}

/*///////////////////////////////////////////////////////
 * 
 * 	TEST FUNCTIONS
 * 
 *///////////////////////////////////////////////////////

//test functions that print results of some methods
//print the graph in form of vertices with their adjacency lists
public void printAdjacencyLists(){
	for(int ii = 0; ii < adjacencyList.size(); ii++){
		HashSet<Integer> iList = adjacencyList.get(ii);
		Iterator<Integer> myIter = iList.iterator();
		System.out.println("Vertex number " + ii + ": ");
		while(myIter.hasNext())
			System.out.println(myIter.next());
		}
}

public void printIsConnected(){
	if(isConnected(0))
		System.out.println("Graph is Connected");
	else
		System.out.println("Error: Graph is not Connected");
}


public void testHakimi(){
	
}

public void testConnectGraph(){
	
}
};
