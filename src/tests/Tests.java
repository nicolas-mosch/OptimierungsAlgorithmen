package tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import algorithms.LocalSearch;
import algorithms.SimulatedAnnealing;
import algorithms.TabooSearch;
import generators.RectangleGenerator;
import interfaces.FeasibleSolution;
import interfaces.Feature;
import models.BinPackingSolution;
import models.GeometricBinPackingSolution;
import models.PermutationBinPackingSolution;
import models.BinPackingRectangle;
import neighborhoods.GeometricNeighborhood;
import neighborhoods.PermutationNeighborhood;

public class Tests {
	
	private static final int INSTANCE_COUNT = 1;
	private static final int RECTANGLE_COUNT = 20;
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
	public void testLocalSearchWithGeometricNeighborhood() {
		LocalSearch solver = new LocalSearch();
		
		BinPackingSolution initialSolution;
		GeometricNeighborhood geometricNeighborhood = new GeometricNeighborhood();
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			initialSolution = new GeometricBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
			int initialBoxCount = initialSolution.boxes.size();
			long startTime = System.currentTimeMillis();
			BinPackingSolution result = (BinPackingSolution) solver.solve(
					initialSolution,
					geometricNeighborhood
				);
			long stopTime = System.currentTimeMillis();
			System.out.println("--== LOCAL SEARCH (Geometric) ==--");
			System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms");
		}
	}
	
	@Ignore
	public void testSimulatedAnnealingWithGeometricNeighborhood() {
		SimulatedAnnealing solver = new SimulatedAnnealing();
		GeometricNeighborhood permutationNeighborhood = new GeometricNeighborhood();
		
		BinPackingSolution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new GeometricBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				int initialBoxCount = initialSolution.boxes.size();
				long startTime = System.currentTimeMillis();
				BinPackingSolution result = (BinPackingSolution) solver.solve(initialSolution, permutationNeighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("--== SIMULATED ANNEALING (Geometric) ==--");
				System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms");
			}catch(Exception e){
				//System.out.println(n.log);
				System.out.println(rectanglesStrings[i]);
				throw e;
			}
		}
	}
	
	@Test
	public void testTabooSearchWithGeometricNeighborhood() {
		TabooSearch solver = new TabooSearch();
		GeometricNeighborhood neighborhood = new GeometricNeighborhood();
		
		BinPackingSolution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new GeometricBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				int initialBoxCount = initialSolution.boxes.size();
				long startTime = System.currentTimeMillis();
				BinPackingSolution result = (BinPackingSolution) solver.solve(initialSolution, neighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("--== TABOO SEARCH (Geometric) ==--");
				System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms");
			}catch(Exception e){
				//System.out.println(n.log);
				System.out.println(rectanglesStrings[i]);
				throw e;
			}
		}
	}
	
	
	@Ignore
	public void testLocalSearchWithPermutationNeighborhood() {
		LocalSearch solver = new LocalSearch();
		PermutationNeighborhood permutationNeighborhood = new PermutationNeighborhood(BOX_LENGTH, 0);
		
		BinPackingSolution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new PermutationBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				int initialBoxCount = initialSolution.boxes.size();
				long startTime = System.currentTimeMillis();
				BinPackingSolution result = (BinPackingSolution) solver.solve(initialSolution, permutationNeighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("--== LOCAL SEARCH (Permutation) ==--");
				System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms");
			}catch(Exception e){
				//System.out.println(n.log);
				System.out.println(rectanglesStrings[i]);
				throw e;
			}
		}
	}
	
	@Ignore
	public void testSimulatedAnnealingWithPermutationNeighborhood() {
		SimulatedAnnealing solver = new SimulatedAnnealing();
		PermutationNeighborhood permutationNeighborhood = new PermutationNeighborhood(BOX_LENGTH, 0);
		
		BinPackingSolution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new PermutationBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				int initialBoxCount = initialSolution.boxes.size();
				long startTime = System.currentTimeMillis();
				BinPackingSolution result = (BinPackingSolution) solver.solve(initialSolution, permutationNeighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("--== SIMULATED ANNEALING (Permutation) ==--");
				System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms");
			}catch(Exception e){
				//System.out.println(n.log);
				System.out.println(rectanglesStrings[i]);
				throw e;
			}
		}
	}
	
	@Ignore
	public void testTabooSearchWithPermutationNeighborhood() {
		TabooSearch solver = new TabooSearch();
		PermutationNeighborhood permutationNeighborhood = new PermutationNeighborhood(BOX_LENGTH, 0);
		
		BinPackingSolution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new PermutationBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				System.out.println("TS Permutation initial boxCount:  " + initialSolution.boxes.size());
				System.out.println("TS Initial Solution:  \n" + initialSolution);
				long startTime = System.currentTimeMillis();
				BinPackingSolution result = (BinPackingSolution) solver.solve(initialSolution, permutationNeighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("TS Permutation final boxCount:  " + result.boxes.size() + ". Elapsed Time: " + (stopTime - startTime) + "ms");
			}catch(Exception e){
				System.out.println(rectanglesStrings[i]);
				throw e;
			}
		}
	}
	
	
	@Test
	public void manualTesting(){
		/*
		BinPackingRectangle[] rects = new BinPackingRectangle[30];
		rects[0] = (new BinPackingRectangle(1, 1, 1));
		rects[1] = (new BinPackingRectangle(2, 1, 3));
		rects[2] = (new BinPackingRectangle(3, 1, 3));
		rects[3] = (new BinPackingRectangle(4, 2, 3));
		rects[4] = (new BinPackingRectangle(5, 1, 3));
		rects[5] = (new BinPackingRectangle(6, 2, 3));
		rects[6] = (new BinPackingRectangle(7, 2, 3));
		rects[7] = (new BinPackingRectangle(8, 1, 3));
		rects[8] = (new BinPackingRectangle(9, 3, 3));
		rects[9] = (new BinPackingRectangle(10, 1, 2));
		rects[10] = (new BinPackingRectangle(11, 2, 3));
		rects[11] = (new BinPackingRectangle(12, 1, 2));
		rects[12] = (new BinPackingRectangle(13, 1, 2));
		rects[13] = (new BinPackingRectangle(14, 2, 3));
		rects[14] = (new BinPackingRectangle(15, 1, 2));
		rects[15] = (new BinPackingRectangle(16, 2, 3));
		rects[16] = (new BinPackingRectangle(17, 2, 3));
		rects[17] = (new BinPackingRectangle(18, 2, 2));
		rects[18] = (new BinPackingRectangle(19, 2, 2));
		rects[19] = (new BinPackingRectangle(20, 1, 3));
		rects[20] = (new BinPackingRectangle(21, 1, 2));
		rects[21] = (new BinPackingRectangle(22, 3, 3));
		rects[22] = (new BinPackingRectangle(23, 1, 3));
		rects[23] = (new BinPackingRectangle(24, 1, 3));
		rects[24] = (new BinPackingRectangle(25, 1, 2));
		rects[25] = (new BinPackingRectangle(26, 1, 2));
		rects[26] = (new BinPackingRectangle(27, 1, 2));
		rects[27] = (new BinPackingRectangle(28, 1, 1));
		rects[28] = (new BinPackingRectangle(29, 2, 3));
		rects[29] = (new BinPackingRectangle(30, 1, 3));
		*/
		
		
		FeasibleSolution s = new PermutationBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectanglesGroups[0])), BOX_LENGTH, 0);
		PermutationNeighborhood n = new PermutationNeighborhood(BOX_LENGTH, 0);
		
		
		for(int i=0; i < 1; i++){
			s = n.getBestNeighbor(s, new HashSet<Feature>());
		}
		
	}
}
