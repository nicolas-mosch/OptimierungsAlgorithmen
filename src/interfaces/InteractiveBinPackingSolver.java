package interfaces;

public interface InteractiveBinPackingSolver {
	public boolean performNextStep();
	public FeasibleSolution getCurrentSolution();
	public FeasibleSolution getNextSolution();
}
