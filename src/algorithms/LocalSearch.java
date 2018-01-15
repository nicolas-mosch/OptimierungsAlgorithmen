package algorithms;

import interfaces.*;

public class LocalSearch {
	
	public FeasibleSolution solve(FeasibleSolution currentSolution, Neighborhood n){
		
		FeasibleSolution nextSolution = n.getAugmentingNeighbor(currentSolution);
		
		while(nextSolution != null){
			currentSolution = nextSolution;
			nextSolution = n.getAugmentingNeighbor(currentSolution);
		}
		
		return currentSolution;
	}
}
