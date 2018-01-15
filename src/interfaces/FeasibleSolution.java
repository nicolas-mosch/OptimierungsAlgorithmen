package interfaces;

import java.util.HashSet;

public interface FeasibleSolution {
	public double getCost();
	public HashSet<Feature> getFeatures();
}
