package interactive_algorithms;

import java.util.Random;

import interfaces.FeasibleSolution;
import interfaces.InteractiveBinPackingSolver;
import interfaces.Neighborhood;

public class SimulatedAnnealing implements InteractiveBinPackingSolver{
	public long maxRunTime = 8000;
	FeasibleSolution currentSolution;
	FeasibleSolution nextSolution;
	private Neighborhood n;
	FeasibleSolution bestSolution;
	
	public SimulatedAnnealing(FeasibleSolution initialSolution, Neighborhood n){
		currentSolution = initialSolution;
		nextSolution = n.getAugmentingNeighbor(currentSolution);
		this.n = n;
	}
	public FeasibleSolution solve(FeasibleSolution currentSolution, Neighborhood n){
		this.currentSolution = currentSolution;
		Random rand = new Random();
		long startTime = System.currentTimeMillis();
		
		FeasibleSolution nextSolution;
		FeasibleSolution bestSolution = currentSolution;
		double temperature = 1.0;
		
		while(System.currentTimeMillis() - startTime < maxRunTime){
			nextSolution = n.getRandomNeighbor(currentSolution);
			if(nextSolution.getCost() < currentSolution.getCost()){
				currentSolution = nextSolution;
				if(currentSolution.getCost() < bestSolution.getCost()){
					bestSolution = currentSolution;
				}
			}else if(rand.nextDouble() < (currentSolution.getCost() - nextSolution.getCost()) / temperature){
				currentSolution = nextSolution;
			}
			temperature *= 0.9;
		}
		
		return currentSolution;
	}

	@Override
	public boolean performNextStep() {
		if(nextSolution != null){
			currentSolution = nextSolution;
			nextSolution = n.getAugmentingNeighbor(currentSolution);
			return true;
		}
			
		return false;
	}

	@Override
	public FeasibleSolution getCurrentSolution() {
		// TODO Auto-generated method stub
		return  currentSolution;
	}

	@Override
	public FeasibleSolution getNextSolution() {
		// TODO Auto-generated method stub
		return null;
	}
}
