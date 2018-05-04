package neighborhoods;

import java.util.Random;
import java.util.Set;

import binpacking_models.PermutationSolution;
import binpacking_models.PermutationSolutionFeature;
import geometric_models.Rectangle;
import geometric_models.Box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import interfaces.FeasibleSolution;
import interfaces.Feature;
import interfaces.Neighborhood;

public class PermutationNeighborhood implements Neighborhood {
	public static long hashCodeTime = 0;
	public static long hashCodeSkips = 0;
	public static long parallelTime = 0;
	public static int counter = 0;
	
	public static int caseCount[][] = new int[4][3];
	public static boolean cases[] = new boolean[4];
	
	
	private int allowedOverlapping = 0;
	private final int boxLength;
	
	private String out = "";
	
	
	
	private class ConcurrentSolution{
		private PermutationSolution s = null;
		
		public synchronized void setSolution(PermutationSolution s){
			this.s = s;
		}
		
		public PermutationSolution getSolution(){
			return this.s;
		}
		
		public synchronized double getCost(){
			if(s == null){
				return Double.MAX_VALUE;
			}
			return s.getCost();
		}
	}
	
	public PermutationNeighborhood(int boxLength){
		this.boxLength = boxLength;
		for(int l = 0; l < 4; l++){
			caseCount[0][0] = 0;
			caseCount[0][1] = 0;
		}
	}
	
	@Override
	public PermutationSolution getRandomNeighbor(FeasibleSolution solution) {
		PermutationSolution s = (PermutationSolution) solution;
		Random rand = new Random();
		int i = rand.nextInt(s.rectangles.size());
		int j = rand.nextInt(s.rectangles.size() - 1);
		if(j == i){
			j = s.rectangles.size() - 1;
		}
		// Create copies of boxes/rectangles of s
		ArrayList<Rectangle> newRectangles = new ArrayList<Rectangle>();
		ArrayList<Box> newBoxes = new ArrayList<Box>();
		for(Box b: s.boxes){
			b = b.deepCopy();
			newRectangles.addAll(b.getRectangles());
			newBoxes.add(b);
		}
		
		newRectangles.get(i).highlight = true;
		newRectangles.add(j, newRectangles.remove(i));
		updateBoxListFromMove(i, j, newBoxes, newRectangles);
		
		return (new PermutationSolution(newRectangles, newBoxes));
	}
	
	@Override
	public PermutationSolution getAugmentingNeighbor(FeasibleSolution solution) {
		PermutationSolution s = (PermutationSolution) solution;
		int i, j, k;
		Box b1, b2;
		
		ArrayList<Rectangle> newRectangles = new ArrayList<Rectangle>();
		ArrayList<Box> newBoxes = new ArrayList<Box>();
		Box box;
		for(Box b: s.boxes){
			box = b.deepCopy();
			newRectangles.addAll(box.getRectangles());
			newBoxes.add(box);
			box.saveCurrentState();
		}
		
		PermutationSolution neighbor;
		ArrayList<Rectangle> testPermutation;
		ArrayList<Box> testBoxes;
		
		//ArrayList<Pair<Integer, Integer>> unlikelyAugmentingPositions = new ArrayList<Pair<Integer, Integer>>();
		
		HashSet<Integer> testedSizePermutations = new HashSet<Integer>();
		
		Box testBox;
		int code;
		Rectangle r1, r2;
		
		for(i = newRectangles.size() - 1; i > 0; i--){
			r1 = newRectangles.get(i);
			b1 = r1.getBox();
			if(b1.isFull()){
				continue;
			}
			jloop:
			for(j = 0; j < newRectangles.size(); j++){
				if(i == j){
					continue;
				}
				r2 = newRectangles.get(j);
				b2 = r2.getBox();
				if(
					b2.isFull()
					|| b2.getFreeSurface() < r1.getSurface()
					|| b2.getFreeSurface() > b1.getFreeSurface()
					|| (b1 == b2 && r1.getHeight() == r2.getHeight() && r1.getY() == r2.getY())
					|| (b1 == b2 && newBoxes.indexOf(b1) == newBoxes.size() - 1)
				){
					//unlikelyAugmentingPositions.add(new Pair<Integer, Integer>(i, j));
					continue;
				}
				
				testPermutation = new ArrayList<Rectangle>(newRectangles);
				r1.highlight = true;
				testPermutation.add(j, testPermutation.remove(i));
				
				code = getPermutationHashCode(testPermutation);
				if(testedSizePermutations.contains(code)){
					for(Box b: newBoxes){
						b.restoreSavedState();
					}
					continue;
				}else{
					testedSizePermutations.add(code);
				}
				
				
				if(b1 == b2){
					// Check if moving within same box will add sufficient space to shift a rect from next box
					testBox = new Box(-1, boxLength);
					k = Math.min(i, j);
					k = Math.min(k, testPermutation.indexOf(b1.getFirstInsertedRectangle()));
					for(; k < testPermutation.size(); k++){
						if(testPermutation.get(k).getBox() != b1){
							if(testBox.tryInsertRectangle(testPermutation.get(k), allowedOverlapping)){
								for(Rectangle x: testBox.getRectangles()){
									x.restoreSavedPosition();
								}
								break;
							}else{
								for(Rectangle x: testBox.getRectangles()){
									x.restoreSavedPosition();
								}
								continue jloop;
							}
						}else if(!testBox.tryInsertRectangle(testPermutation.get(k), allowedOverlapping)){
							testPermutation.get(k).restoreSavedPosition();
							for(Rectangle x: testBox.getRectangles()){
								x.restoreSavedPosition();
							}
							continue jloop;
						}
					}
				}
				
				testBoxes = new ArrayList<Box>(newBoxes);
				updateBoxListFromMove(i, j, testBoxes, testPermutation);
				
				if(getBoxListCost(testBoxes) < s.getCost()){
					neighbor = new PermutationSolution(testPermutation, testBoxes);
					
					return neighbor;
				}else{
					for(Box b: newBoxes){
						b.restoreSavedState();
					}
				}
			}
		}
		
		return null;
	}

	public int getPermutationHashCode(ArrayList<Rectangle> permutation){
		long start = System.currentTimeMillis();
		int[] rectangleHash = new int[permutation.size()];
		int i = 0;
		for(Rectangle r: permutation){
			rectangleHash[i] = r.getLongSide() * 31 + r.getShortSide();
			++i;
		}
		hashCodeTime += System.currentTimeMillis() - start;
		return Arrays.hashCode(rectangleHash);
	}
	
	public double getBoxListCost(ArrayList<Box> boxes){
		double cost = 0;
		for(Box b: boxes){
			cost -= Math.pow(b.getOccupiedSurface(), 2) / Math.pow(b.getLength(), 5); 
		}
		return cost;
	}

	@Override
	public FeasibleSolution getBestNeighbor(FeasibleSolution solution, Set<Feature> tabooList) {
		
		counter++;
		//System.out.println("============-- TABOO " + counter + " --============");
		PermutationSolution s = (PermutationSolution) solution;
		
		ConcurrentSolution bestNeighbor = new ConcurrentSolution();
		int threadCount = 1;
		int rectangleCount = s.rectangles.size();
		HashMap<Integer, ArrayList<Integer>> swapsPerThread = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<Integer> swaps = new ArrayList<Integer>();
		
		int i, j;
		
		for(i = 0; i < threadCount; i++){
			swapsPerThread.put(i, new ArrayList<Integer>());
		}
		
		PermutationSolutionFeature f;
		
		Rectangle r;
		int code;
		HashSet<Integer> testedSizePermutations = new HashSet<Integer>();
		
		for(i = 0; i < rectangleCount - 1; i++){
			f = new PermutationSolutionFeature(s.rectangles.get(i).getId(), i);
			if(
				!tabooList.contains(f)
			){
				s.rectangles.add(s.rectangles.remove(i));
				code = getPermutationHashCode(s.rectangles);
				s.rectangles.add(i, s.rectangles.remove(s.rectangles.size() - 1));
				if(testedSizePermutations.contains(code)){
					hashCodeSkips++;
					continue;
				}
				testedSizePermutations.add(code);
				swaps.add(i);
			}
		}
		
		
		counter = swaps.size();
		
		if(swaps.isEmpty()){
			return s;
		}
		int chunkSize = (int) Math.ceil((double) swaps.size() / (double) threadCount);
		int c = 0;
		int t = 0;
		for(Integer p : swaps){
			if(c == chunkSize){
				t++;
				c = 0;
			}
			swapsPerThread.get(t).add(p);
			c++;
		}
		Thread[] threads = new Thread[threadCount];
		
		long threadStartTimes[] = new long[threadCount];
		long threadEndTimes[] = new long[threadCount];
		
		for(t = 0; t < threadCount; t++){
			final int tid = t;
			
			Thread thread = new Thread(){
				public void run(){
					long start = System.currentTimeMillis();
					threadStartTimes[tid] = start;
					
					PermutationSolution neighbor;
					ArrayList<Rectangle> permutation; 
					for(Integer pair : swapsPerThread.get(tid)){
						permutation = new ArrayList<>(s.rectangles);
						permutation.add(permutation.remove(pair.intValue()));
						
						neighbor = new PermutationSolution(permutation, boxLength);
						
						start = System.currentTimeMillis();
						synchronized(bestNeighbor){		
							if(neighbor.getCost() < bestNeighbor.getCost()){
								bestNeighbor.setSolution(neighbor);
							}
						}
						//neighbor.restoreBoxStates();
						parallelTime -= System.currentTimeMillis() - start;
					}
					threadEndTimes[tid] = System.currentTimeMillis();
				}
			};
			threads[t] = thread;
		}
		
		for(i = 0; i < threads.length; i++){
			threads[i].start();
			// async?
		}
		
		for(i = 0; i < threads.length; i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long max = 0;
		long min = Long.MAX_VALUE;
		
		for(i = 0; i < threads.length; i++){
			if(threadStartTimes[i] > max){
				max = threadStartTimes[i]; 
			}
			if(threadEndTimes[i] < min){
				min = threadEndTimes[i]; 
			}
		}
		
		parallelTime += (min - max);
		
		
		if(bestNeighbor.getSolution() != null){
			//System.out.println(this.out);
			//System.out.println(bestNeighbor.getSolution());
			
			return bestNeighbor.getSolution();
			
		}
		else return s;
	}
	
	public void updateBoxListFromMove(int i, int j, ArrayList<Box> boxes, ArrayList<Rectangle> permutation){
		Rectangle r = permutation.get(j);
		Box b1 = r.getBox();
		Box b2 = permutation.get(j < i ? j + 1 : j - 1).getBox();
		int bi1 = boxes.indexOf(b1);
		int bi2 = boxes.indexOf(b2);
		int bi;
		b1.removeRectangle(r);
		
		Box box;
		// start with box to the left
		if(bi1 < bi2){
			box = b1;
			bi = bi1;
		}else{
			box = b2;
			bi = bi2;
		}

		// start with min (leftmost) position of changed rects
		int p = Math.min(i, j);
		if(!box.isEmpty()){
			// if removing r from b1 didn't empty it, start with the first inserted rect in that box (redo the box)
			p = Math.min(p, permutation.indexOf(box.getFirstInsertedRectangle()));
		}
		boolean insertedInPrevious = false;
		if(bi != 0 && (p == i || p == j)){
			// if r was the first rectangle in the box, the others might now fit in the previous box
			Box previousBox = boxes.get(bi - 1);
			
			if(previousBox.tryInsertRectangle(r, allowedOverlapping)){
				insertedInPrevious = true;
			}
		}
		
		if(!insertedInPrevious){
			// if shifting rects to previous box wasn't possible, then shift from current box
			box.clear();
			while(p < permutation.size()){
				// create new boxes with the new permutation starting from b
				r = permutation.get(p);
				if(!box.tryInsertRectangle(r, allowedOverlapping)){
					bi++;
					if(bi == boxes.size()){
						box = new Box(boxes.size(), boxLength);
						boxes.add(box);
						continue;
					}
					box = boxes.get(bi);
					if(box.isEmpty()){
						continue;
					}
					if(r == box.getFirstInsertedRectangle() && r.getX() == 0 && r.getY() == 0){
						// until we reach a box which doesn't actually change from new permutation
						break;
					}
					box.clear();
				}else{
					p++;
				}
			}
		}
		
		if(p <= Math.max(i, j)){
			// if we didn't reach the right index from previous changes
			//  then changes are necessary starting from that index as well
			if(bi1 > bi2){
				box = b1;
				bi = bi1;
			}else{
				box = b2;
				bi = bi2;
			}
			if(box.isEmpty()){
				// the first changes to the boxes might have left right-box empty
				boxes.remove(box);
				if(bi == boxes.size()){
					return;
				}
				box = boxes.get(bi);
			}
			
			r = box.getFirstInsertedRectangle();
			Box previousBox = boxes.get(bi - 1);
			insertedInPrevious = false;
			while(previousBox.tryInsertRectangle(r, allowedOverlapping)){
				// the previous box might have empty spaces not available previously
				insertedInPrevious = true;
				box.removeRectangle(r);
				if(box.isEmpty()){
					boxes.remove(box);
					if(bi == boxes.size()){
						break;
					}
					box = boxes.get(bi);
				}
				r = box.getFirstInsertedRectangle();
			}
			
			if(!insertedInPrevious || r.getX() != 0 || r.getY() != 0){
				// same as above
				box.clear();
				p = permutation.indexOf(r);
				while(p < permutation.size()){
					r = permutation.get(p);
					if(!box.tryInsertRectangle(r, allowedOverlapping)){
						bi++;
						if(bi == boxes.size()){
							box = new Box(boxes.size(), boxLength);
							boxes.add(box);
							continue;
						}
						box = boxes.get(bi);
						if(r == box.getFirstInsertedRectangle()){
							break;
						}
						box.clear();
					}else{
						p++;
					}
				}
			}
		}
		++bi;
		while(bi < boxes.size()){
			// remove empty/invalid boxes at end
			if(boxes.get(bi).isEmpty() || boxes.get(bi).getFirstInsertedRectangle().getBox() != boxes.get(bi)){
				boxes.remove(bi);
			}
			else{
				bi++;
			}
		}
	}
}	
