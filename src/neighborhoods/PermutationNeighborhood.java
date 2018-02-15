package neighborhoods;

import java.util.Random;
import java.util.Set;

import binpacking_models.PermutationBinPackingSolution;
import binpacking_models.PermutationBinPackingSolutionFeature;
import geometric_models.BinPackingRectangle;
import geometric_models.Box;
import geometric_models.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import interfaces.FeasibleSolution;
import interfaces.Feature;
import interfaces.Neighborhood;

public class PermutationNeighborhood implements Neighborhood {
	private int allowedOverlapping;

	private final int boxLength;

	public int[] freeCells;
	
	public PermutationNeighborhood(int boxLength, int allowedOverlapping) {
		this.boxLength = boxLength;
		this.allowedOverlapping = allowedOverlapping;
		freeCells = new int[this.boxLength];
	}

	@Override
	public FeasibleSolution getBestNeighbor(FeasibleSolution solution, Set<Feature> tabooList) {
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
