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
import binpacking_models.Solution;
import binpacking_models.GeometricSolution;
import binpacking_models.PermutationSolution;
import generators.RectangleGenerator;
import geometric_models.Rectangle;
import interfaces.FeasibleSolution;
import interfaces.Feature;
import neighborhoods.GeometricNeighborhood;
import neighborhoods.PermutationNeighborhood;

public class Tests {
	
	private static final int INSTANCE_COUNT = 5;
	private static final int RECTANGLE_COUNT = 1000;
	private static final int MIN_SIDE_LENGTH = 1;
	private static final int MAX_SIDE_LENGTH = 7;
	private static final int BOX_LENGTH = 10;
	
	private static Rectangle[][] rectanglesGroups;
	private static String[] rectanglesStrings;
	
	@BeforeClass
	public static void init(){
		RectangleGenerator generator = new RectangleGenerator();
		rectanglesStrings = new String[INSTANCE_COUNT];
		
		Rectangle[] rectangles;
		String rectanglesString;
		rectanglesGroups = new Rectangle[INSTANCE_COUNT][];
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
	public void testCompareRect(){
		Rectangle r1 = new Rectangle(1, 1, 3);
		Rectangle r2 = new Rectangle(1, 1, 2);
		System.out.println(r2.compareTo(r1));
	}
	
	@Ignore
	public void testLocalSearchWithGeometricNeighborhood() {
		LocalSearch solver = new LocalSearch();
		
		Solution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			initialSolution = new GeometricSolution(new ArrayList<Rectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
			GeometricNeighborhood geometricNeighborhood = new GeometricNeighborhood(initialSolution);
			int initialBoxCount = initialSolution.boxes.size();
			long startTime = System.currentTimeMillis();
			Solution result = (Solution) solver.solve(
					initialSolution,
					geometricNeighborhood
				);
			long stopTime = System.currentTimeMillis();
			System.out.println("--== LOCAL SEARCH (Geometric) ==--");
			System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms | Iterations: " + solver.iterationCount);
		}
	}
	
	@Ignore
	public void testSimulatedAnnealingWithGeometricNeighborhood() {
		SimulatedAnnealing solver = new SimulatedAnnealing();
		
		Solution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new GeometricSolution(new ArrayList<Rectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				GeometricNeighborhood permutationNeighborhood = new GeometricNeighborhood(initialSolution);
				int initialBoxCount = initialSolution.boxes.size();
				long startTime = System.currentTimeMillis();
				Solution result = (Solution) solver.solve(initialSolution, permutationNeighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("--== SIMULATED ANNEALING (Geometric) ==--");
				System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms | Iterations: " + solver.iterationCount);
			}catch(Exception e){
				//System.out.println(n.log);
				System.out.println(rectanglesStrings[i]);
				throw e;
			}
		}
	}
	
	@Ignore
	public void testTabooSearchWithGeometricNeighborhood() {
		TabooSearch solver = new TabooSearch();
		
		Solution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new GeometricSolution(new ArrayList<Rectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				GeometricNeighborhood neighborhood = new GeometricNeighborhood(initialSolution);
				int initialBoxCount = initialSolution.boxes.size();
				long startTime = System.currentTimeMillis();
				Solution result = (Solution) solver.solve(initialSolution, neighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("--== TABOO SEARCH (Geometric) ==--");
				System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms | Iterations: " + solver.iterationCount);
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
		PermutationNeighborhood permutationNeighborhood = new PermutationNeighborhood(BOX_LENGTH);
		
		Solution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new PermutationSolution(new ArrayList<Rectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				int initialBoxCount = initialSolution.boxes.size();
				long startTime = System.currentTimeMillis();
				Solution result = (Solution) solver.solve(initialSolution, permutationNeighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("--== LOCAL SEARCH (Permutation) ==--");
				System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms | Iterations: " + solver.iterationCount);
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
		PermutationNeighborhood permutationNeighborhood = new PermutationNeighborhood(BOX_LENGTH);
		
		Solution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new PermutationSolution(new ArrayList<Rectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				int initialBoxCount = initialSolution.boxes.size();
				long startTime = System.currentTimeMillis();
				Solution result = (Solution) solver.solve(initialSolution, permutationNeighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("--== SIMULATED ANNEALING (Permutation) ==--");
				System.out.println("Initial box count: " + initialBoxCount + " | Final box count: " + result.boxes.size() + " |  Elapsed Time: " + (stopTime - startTime) + "ms | Iterations: " + solver.iterationCount);
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
		PermutationNeighborhood permutationNeighborhood = new PermutationNeighborhood(BOX_LENGTH);
		
		Solution initialSolution;
		
		for(int i = 0; i < INSTANCE_COUNT; i++){
			try{
				initialSolution = new PermutationSolution(new ArrayList<Rectangle>(Arrays.asList(rectanglesGroups[i])), BOX_LENGTH, 0);
				System.out.println("TS Permutation initial boxCount:  " + initialSolution.boxes.size());
				long startTime = System.currentTimeMillis();
				Solution result = (Solution) solver.solve(initialSolution, permutationNeighborhood);
				long stopTime = System.currentTimeMillis();
				System.out.println("TS Permutation final boxCount:  " + result.boxes.size() + ". Elapsed Time: " + (stopTime - startTime) + "ms | Iterations: " + solver.iterationCount);
			}catch(Exception e){
				System.out.println(rectanglesStrings[i]);
				throw e;
			}
		}
	}
	
	
	@Ignore
	public void manualTesting(){
	
		Rectangle[] rects = new Rectangle[30];
		rects[0] = (new Rectangle(1, 1, 1));
		rects[1] = (new Rectangle(2, 1, 3));
		rects[2] = (new Rectangle(3, 1, 3));
		rects[3] = (new Rectangle(4, 2, 3));
		rects[4] = (new Rectangle(5, 1, 3));
		rects[5] = (new Rectangle(6, 2, 3));
		rects[6] = (new Rectangle(7, 2, 3));
		rects[7] = (new Rectangle(8, 1, 3));
		rects[8] = (new Rectangle(9, 3, 3));
		rects[9] = (new Rectangle(10, 1, 2));
		rects[10] = (new Rectangle(11, 2, 3));
		rects[11] = (new Rectangle(12, 1, 2));
		rects[12] = (new Rectangle(13, 1, 2));
		rects[13] = (new Rectangle(14, 2, 3));
		rects[14] = (new Rectangle(15, 1, 2));
		rects[15] = (new Rectangle(16, 2, 3));
		rects[16] = (new Rectangle(17, 2, 3));
		rects[17] = (new Rectangle(18, 2, 2));
		rects[18] = (new Rectangle(19, 2, 2));
		rects[19] = (new Rectangle(20, 1, 3));
		rects[20] = (new Rectangle(21, 1, 2));
		rects[21] = (new Rectangle(22, 3, 3));
		rects[22] = (new Rectangle(23, 1, 3));
		rects[23] = (new Rectangle(24, 1, 3));
		rects[24] = (new Rectangle(25, 1, 2));
		rects[25] = (new Rectangle(26, 1, 2));
		rects[26] = (new Rectangle(27, 1, 2));
		rects[27] = (new Rectangle(28, 1, 1));
		rects[28] = (new Rectangle(29, 2, 3));
		rects[29] = (new Rectangle(30, 1, 3));
	
		
		
		FeasibleSolution s = new GeometricSolution(new ArrayList<Rectangle>(Arrays.asList(rects)), BOX_LENGTH, 0);
		GeometricNeighborhood n = new GeometricNeighborhood(s);
		System.out.println(s.getCost());
		
		for(int i=0; i < 1; i++){
			s = n.getBestNeighbor(s, new HashSet<Feature>());
			System.out.println(s.getCost());
		}
		
	}
}
