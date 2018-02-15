package algorithms;

import interfaces.*;

public class LocalSearch {
	
	public int iterationCount;
	
	public FeasibleSolution solve(FeasibleSolution currentSolution, Neighborhood n){
		
		iterationCount = 0;
		
		FeasibleSolution nextSolution = n.getAugmentingNeighbor(currentSolution);
		
		while(nextSolution != null){
			++iterationCount;
			currentSolution = nextSolution;
			nextSolution = n.getAugmentingNeighbor(currentSolution);
		}
		
		return currentSolution;
	}
}
