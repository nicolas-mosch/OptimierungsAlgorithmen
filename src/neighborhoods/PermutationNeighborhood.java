package neighborhoods;

import java.util.Random;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import interfaces.FeasibleSolution;
import interfaces.Feature;
import interfaces.Neighborhood;
import models.PermutationBinPackingSolution;
import models.PermutationBinPackingSolutionFeature;
import models.Box;
import models.Pair;
import models.BinPackingRectangle;

public class PermutationNeighborhood implements Neighborhood {
	private int allowedOverlapping;
	
	private final int boxLength;
	public PermutationNeighborhood(int boxLength, int allowedOverlapping){
		this.boxLength = boxLength;
		this.allowedOverlapping = allowedOverlapping;
	}
	
	@Override
	public FeasibleSolution getBestNeighbor(FeasibleSolution solution, Set<Feature> tabooList) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public FeasibleSolution getAugmentingNeighbor(FeasibleSolution solution) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public FeasibleSolution getRandomNeighbor(FeasibleSolution solution) {
		// TODO Auto-generated method stub
		return null;
	}
	

}	
