package interfaces;

import java.util.Set;

public interface Neighborhood {
	public FeasibleSolution getBestNeighbor(FeasibleSolution s, Set<Feature> tabooList);
	public FeasibleSolution getAugmentingNeighbor(FeasibleSolution s);
	public FeasibleSolution getRandomNeighbor(FeasibleSolution s);
}
