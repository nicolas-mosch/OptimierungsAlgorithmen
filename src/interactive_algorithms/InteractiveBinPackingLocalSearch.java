package interactive_algorithms;

import interfaces.*;

public class InteractiveBinPackingLocalSearch implements InteractiveBinPackingSolver{
	
	private FeasibleSolution currentSolution;
	private FeasibleSolution nextSolution;
	private Neighborhood n;
	
	public InteractiveBinPackingLocalSearch(FeasibleSolution initialSolution, Neighborhood n){
		currentSolution = initialSolution;
		nextSolution = n.getAugmentingNeighbor(currentSolution);
		this.n = n;
	}
	
	public boolean performNextStep(){
		if(nextSolution != null){
			currentSolution = nextSolution;
			nextSolution = n.getAugmentingNeighbor(currentSolution);
			return true;
		}
			
		return false;
	}
	
	public FeasibleSolution getCurrentSolution(){
		return currentSolution;
	}
	
	public FeasibleSolution getNextSolution(){
		return nextSolution;
	}
}
