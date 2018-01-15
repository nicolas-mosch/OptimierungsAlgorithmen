package algorithms;

import java.util.Random;

import interfaces.FeasibleSolution;
import interfaces.Neighborhood;

public class SimulatedAnnealing {
	public long maxRunTime = 10000;
	
	public FeasibleSolution solve(FeasibleSolution currentSolution, Neighborhood n){
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
}
