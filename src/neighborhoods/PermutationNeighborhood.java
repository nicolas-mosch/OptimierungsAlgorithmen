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

	int[] freeCells;

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

	private ArrayList<FeasibleSolution> getPossibleNeighbors(FeasibleSolution solution) {
		// Melissa
		ArrayList<FeasibleSolution> possiblePerm = new ArrayList<>();
		ArrayList<Box> boxes = ((PermutationBinPackingSolution) solution).boxes;
		ArrayList<BinPackingRectangle> rectangles = ((PermutationBinPackingSolution) solution).rectangles;
		// create permutation
		int maxSize = boxes.size();
		int boxCount = 0;
		initfreeCells();

		// for one permutation
		for (BinPackingRectangle rectangle : rectangles) {
			if (!setFreeCells(rectangle.getHeight(), rectangle.getWidth())) {
				boxCount++;
				initfreeCells();
				setFreeCells(rectangle.getShortSide(), rectangle.getLongSide());

			}
		}
		if (boxCount <= maxSize) {
			// temp
			possiblePerm.add(new PermutationBinPackingSolution(rectangles, boxes));
		}
		return possiblePerm;
	}

	private void initfreeCells() {
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
							if (k > 0) {
								freeCells[k] = freeCells[k] + width;
							}
							freeCells[j] = 0;
						}
					}
				}
				k = i - height;
				if (k > 0) {
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
