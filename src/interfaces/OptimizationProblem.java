package interfaces;

public interface OptimizationProblem {
	public Neighborhood getNeighbor(Neighborhood s);
	public double getCost(Neighborhood s);
}
