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

	public int[] freeCells;

	public PermutationNeighborhood(int boxLength, int allowedOverlapping) {
		this.boxLength = boxLength;
		this.allowedOverlapping = allowedOverlapping;
		freeCells = new int[this.boxLength];
	}

	@Override
	public FeasibleSolution getBestNeighbor(FeasibleSolution solution, Set<Feature> tabooList) {
		ArrayList<FeasibleSolution> possibleSolutions = getPossibleNeighbors(solution);
		return null;
	}

	public double getSwapApproxCost(ArrayList<Box> originalBoxes,ArrayList<BinPackingRectangle> permutation , int j) {
		// Melissa

		// for one permutation
		Box currentBox = permutation.get(j).getBox();
		BinPackingRectangle currentRect = currentBox.getFirstInsertedRectangle();
		int boxIndex = originalBoxes.indexOf(currentBox);
		int index = permutation.indexOf(currentRect);

		initfreeCells();
		int boxSurface = 0;
		double cost = 0;
		
		for(int i = 0; i < boxIndex; i++) {
			cost -= Math.pow(originalBoxes.get(i).getOccupiedSurface(), 2) / Math.pow(this.boxLength, 5);
		}

		for (int i = index; i < permutation.size(); i++) {
			currentRect = permutation.get(i);

			if (!setFreeCells(currentRect.getShortSide(), currentRect.getLongSide())
					&& !setFreeCells(currentRect.getLongSide(), currentRect.getShortSide())) {
				cost -= Math.pow(boxSurface, 2) / Math.pow(this.boxLength, 5);
				initfreeCells();
				setFreeCells(currentRect.getShortSide(), currentRect.getLongSide());
				boxSurface = currentRect.getSurface();

			} else {
				boxSurface += currentRect.getSurface();
			}
		}
		
		return cost;
	}

	public void initfreeCells() {
		for (int i = 0; i < this.boxLength - 1; i++) {
			freeCells[i] = 0;
		}
		freeCells[this.boxLength - 1] = this.boxLength;
	}

	public boolean setFreeCells(int height, int width) {
		boolean foundSome = false;
		int[] r = new int[this.boxLength];
		for (int i = 0; i < this.boxLength; i++) {
			r[i] = 0;
		}
		// initialize array
		int k = 0;
		int value;
		for (int i = height - 1; i < this.boxLength; i++) {
			value = freeCells[i];

			if (width > 0 && value >= width) {
				if (foundSome) {
					for (int j = 0; j < i; j++) {
						if (r[j] != 0) {
							k = j - height;
							if (k >= 0) {
								freeCells[k] = freeCells[k] + r[j];
							}
							freeCells[j] = 0;
						}
					}
				}
				k = i - height;
				if (k >= 0) {
					freeCells[k] = freeCells[k] + width;
				}
				freeCells[i] = value - width;
				return true;
			} else if (value < width && value != 0) {
				foundSome = true;
				r[i] = value; // ???store indices to remove
				width = width - value;
			}
		}
		return false;

	}

	public FeasibleSolution createPermutations(FeasibleSolution solution) {
		return solution;

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

	public boolean changeFreeCells(int length, int height, int[] freeCells) {
		int k = 0;
		for (int i = height - 1; i < this.boxLength; i++) {
			if (freeCells[i] >= height) {
				k = i - height;
				freeCells[k] = this.boxLength;
				freeCells[i] = freeCells[i] - length;
				return true;
			}
			;
		}
		return false;
	}

}
