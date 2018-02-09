package tests;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import generators.RectangleGenerator;
import interfaces.FeasibleSolution;
import models.BinPackingRectangle;
import models.Box;
import models.PermutationBinPackingSolution;
import neighborhoods.PermutationNeighborhood;

public class TestSwap {

	
	private static final int INSTANCE_COUNT = 1;
	private static final int RECTANGLE_COUNT = 1000;
	private static final int MIN_SIDE_LENGTH = 1;
	private static final int MAX_SIDE_LENGTH = 4;
	private static final int BOX_LENGTH = 5;
	
	private static BinPackingRectangle[][] rectanglesGroups;
	private static String[] rectanglesStrings;
	
	@BeforeClass
	public static void init(){
		RectangleGenerator generator = new RectangleGenerator();
		rectanglesStrings = new String[INSTANCE_COUNT];
		
		BinPackingRectangle[] rectangles;
		String rectanglesString;
		rectanglesGroups = new BinPackingRectangle[INSTANCE_COUNT][];
		for(int i = 0; i < INSTANCE_COUNT; i++){
			rectangles = generator.generateRandomly(RECTANGLE_COUNT, MIN_SIDE_LENGTH, MAX_SIDE_LENGTH);
			rectanglesString = "Rectangles:";
			 for(int j = 0; j < rectangles.length; j++){
				rectanglesString += "\nrects["+j+"] = (new BinPackingRectangle("+rectangles[j].getId()+", "+rectangles[j].getWidth()+", "+rectangles[j].getHeight()+"));";
			}
			rectanglesGroups[i] = rectangles;
			rectanglesStrings[i] = rectanglesString;
		}
	}
	

@Test
public void swapCostTest() {
	RectangleGenerator generator = new RectangleGenerator();
	BinPackingRectangle[] rects = generator.generateRandomly(RECTANGLE_COUNT, MIN_SIDE_LENGTH, MAX_SIDE_LENGTH);

	FeasibleSolution s = new PermutationBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rects)), BOX_LENGTH, 0);
	PermutationNeighborhood n = new PermutationNeighborhood(BOX_LENGTH, 0);

	long startTime = System.currentTimeMillis();
	ArrayList<Box> originalBoxes = ((PermutationBinPackingSolution) s).boxes;
	ArrayList<BinPackingRectangle> rectangles = ((PermutationBinPackingSolution) s).rectangles;
	BinPackingRectangle temp = rectangles.get(25);
	rectangles.set(25, rectangles.get(9));
	rectangles.set(9, temp);
	double cost = n.getSwapApproxCost(originalBoxes, rectangles, 9);
	
	long stopTime = System.currentTimeMillis();

	System.out.println("Cost: " + cost);
	System.out.println(" Elapsed Time: " + (stopTime - startTime) + " ms");
}


@Test
public void swapCost2Test() {
	RectangleGenerator generator = new RectangleGenerator();
	BinPackingRectangle[] rects = generator.generateRandomly(RECTANGLE_COUNT, MIN_SIDE_LENGTH, MAX_SIDE_LENGTH);

	FeasibleSolution s = new PermutationBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rects)), BOX_LENGTH, 0);
	PermutationNeighborhood n = new PermutationNeighborhood(BOX_LENGTH, 0);

	long startTime = System.currentTimeMillis();
	ArrayList<Box> originalBoxes = ((PermutationBinPackingSolution) s).boxes;
	ArrayList<BinPackingRectangle> rectangles = ((PermutationBinPackingSolution) s).rectangles;
	BinPackingRectangle temp = rectangles.get(25);
	rectangles.set(25, rectangles.get(9));
	rectangles.set(9, temp);
	double cost = n.getSwapApproxCost2(originalBoxes, rectangles, 9);
	
	long stopTime = System.currentTimeMillis();

	System.out.println("Cost: " + cost);
	System.out.println(" Elapsed Time: " + (stopTime - startTime) + " ms");
}

@Test
public void swapTest() {
	RectangleGenerator generator = new RectangleGenerator();
	BinPackingRectangle[] rects = generator.generateRandomly(RECTANGLE_COUNT, MIN_SIDE_LENGTH, MAX_SIDE_LENGTH);

	FeasibleSolution s = new PermutationBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rects)), BOX_LENGTH, 0);
	PermutationNeighborhood n = new PermutationNeighborhood(BOX_LENGTH, 0);

	
	long startTime = System.currentTimeMillis();
	n.swaps(s);
	int count = n.count;
	long stopTime = System.currentTimeMillis();
	System.out.println(" Elapsed Time: " + (stopTime - startTime) + " ms");
	System.out.println(" Swaps Count: " + count);
}
}

	
