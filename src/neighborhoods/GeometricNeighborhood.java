package neighborhoods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import binpacking_models.Solution;
import binpacking_models.GeometricSolution;
import binpacking_models.GeometricSolutionFeature;
import geometric_models.Rectangle;
import geometric_models.Box;
import interfaces.FeasibleSolution;
import interfaces.Feature;
import interfaces.Neighborhood;

public class GeometricNeighborhood implements Neighborhood {
	
	private int allowedOverlapping = 0;
	
	private HashMap<Integer, ArrayList<ApproximateNeighbor>> approximateNeighborMap;
	
	private class ApproximateNeighbor implements Comparable<ApproximateNeighbor>{
		public final boolean isSwap;
		public final Box destinationBox;
		public final Rectangle r1, r2;
		double costDelta;
		
		public ApproximateNeighbor(Rectangle r1, Rectangle r2){
			this.r1 = r1;
			this.r2 = r2;
			this.isSwap = true;
			destinationBox = null;
			
			if(r1.getShortSide() == r2.getShortSide() && r1.getLongSide() == r2.getLongSide()){
				costDelta = 0;
			}else{
				Box b1 = r1.getBox();
				Box b2 = r2.getBox();
				
				double oldBoxCost1 = b1.getCost();
				
				double oldBoxCost2 = b2.getCost();
				
				double newBoxCost1 = Math.pow(
						b1.getOccupiedSurface() - r1.getSurface() + r2.getSurface(), 2
					) / Math.pow(b1.getLength(), 5);
				
				double newBoxCost2 = Math.pow(
						b2.getOccupiedSurface() - r2.getSurface() + r1.getSurface(), 2
					) / Math.pow(b2.getLength(), 5);
				
				costDelta = (oldBoxCost1 + oldBoxCost2) - (newBoxCost1 + newBoxCost2);
			}
		}
		
		public ApproximateNeighbor(Rectangle r, Box b){
			isSwap = false;
			r1 = r;
			destinationBox = b;
			r2 = null;
			
			double oldBoxCost1 = Math.pow(
					r.getBox().getOccupiedSurface(), 2
				) / Math.pow(b.getLength(), 5);
			
			double oldBoxCost2 = Math.pow(
					b.getOccupiedSurface(), 2
				) / Math.pow(b.getLength(), 5);
			
			double newBoxCost1 = Math.pow(
					r.getBox().getOccupiedSurface() - r.getSurface(), 2
				) / Math.pow(b.getLength(), 5);
			
			double newBoxCost2 = Math.pow(
					b.getOccupiedSurface() + r.getSurface(), 2
				) / Math.pow(b.getLength(), 5);
			
			costDelta = (oldBoxCost1 + oldBoxCost2) - (newBoxCost1 + newBoxCost2);
		}

		@Override
		public int compareTo(ApproximateNeighbor o) {
			return new Double(costDelta).compareTo(o.costDelta);
		}
	}
	
	public GeometricNeighborhood(FeasibleSolution solution){
		approximateNeighborMap = new HashMap<>();
		GeometricSolution s = (GeometricSolution) solution;
		int i;
		Rectangle r1;
		
		for(i = 0; i < s.rectangles.size(); i++){
			r1 = s.rectangles.get(i);
			approximateNeighborMap.put(r1.getId(), new ArrayList<ApproximateNeighbor>());
			setApproximateNeighborsForRect(r1, s);
		}
		
	}
	
	private void setApproximateNeighborsForRect(Rectangle r1, GeometricSolution s){
		approximateNeighborMap.get(r1.getId()).clear();
		
		// Moves
		for(Box b: s.boxes){
			if(r1.getBox() != b && b.getFreeSurface() >= r1.getSurface()){
				approximateNeighborMap.get(r1.getId()).add(new ApproximateNeighbor(r1, b));
			}
		}
		
		Rectangle r2;
		// Swaps
		for(int j = s.rectangles.indexOf(r1) + 1; j < s.rectangles.size(); j++){
			r2 = s.rectangles.get(j);
			if(
				r1.getBox() != r2.getBox()
				&& r2.getBox().getFreeSurface() + r2.getSurface() >= r1.getSurface()
				&& r1.getBox().getFreeSurface() + r1.getSurface() >= r2.getSurface()
				&& !r1.isLike(r2)
			){
				approximateNeighborMap.get(r1.getId()).add(new ApproximateNeighbor(r1, r2));
			}
		}
	}
	
	@Override
	public FeasibleSolution getBestNeighbor(FeasibleSolution solution, Set<Feature> tabooList) {
		
		GeometricSolution s = ((GeometricSolution) solution).deepCopy(); 
		
		ArrayList<ApproximateNeighbor> approximateNeighbors = new ArrayList<>();
		
		for(ArrayList<ApproximateNeighbor> a: approximateNeighborMap.values()){
			approximateNeighbors.addAll(a);
		}
		
		
		Collections.sort(approximateNeighbors);
		
		Box b1, b2;
		
		for(ApproximateNeighbor m: approximateNeighbors){
			if(m.isSwap){
				b1 = m.r1.getBox();
				b2 = m.r2.getBox();
				b1.saveCurrentState();
				b2.saveCurrentState();
				if(
					b1.tryInsertRectangleBySize(m.r2, allowedOverlapping)
					&& b2.tryInsertRectangleBySize(m.r1, allowedOverlapping)
				){
					for(Rectangle r: b1.getRectangles()){
						setApproximateNeighborsForRect(r, s);
					}
					for(Rectangle r: b2.getRectangles()){
						setApproximateNeighborsForRect(r, s);
					}
					return s;
				}else{
					b1.restoreSavedState();
					b2.restoreSavedState();
				}
			}else{
				b1 = m.r1.getBox();
				b2 = m.destinationBox;
				if(b2.tryInsertRectangleBySize(m.r1, allowedOverlapping)){
					b1.removeRectangle(m.r1);
					for(Rectangle r: b1.getRectangles()){
						setApproximateNeighborsForRect(r, s);
					}
					for(Rectangle r: b2.getRectangles()){
						setApproximateNeighborsForRect(r, s);
					}
					return s;
				}
			}
		}
		
		
		System.err.println("Couldn't find a neighbor");
		return null;
	}

	@Override
	public FeasibleSolution getAugmentingNeighbor(FeasibleSolution solution) {
		GeometricSolution s = ((GeometricSolution) solution).deepCopy(); 
		
		ArrayList<Box> newBoxes = s.boxes;
		Collections.sort(newBoxes);
		
		int costB1, costB2;
		int surfaceB1, surfaceB2;
		int i, j, k, l;
		Box b1, b2;
		
		// Try to move a rectangle from one box to another
		for(i = 0; i < newBoxes.size(); i++){
			for(j = newBoxes.size() - 1; j >= 0; j--){
				b1 = newBoxes.get(i);
				b2 = newBoxes.get(j);
				if(
					j == i 
					|| b1.isEmpty() 
					|| b2.isEmpty() 
					|| b1.isFull() 
					|| b2.isFull()
				){continue;}
				
				surfaceB1 = b1.getFreeSurface();
				surfaceB2 = b2.getFreeSurface();
				
				costB1 = - surfaceB1 * surfaceB1;
				costB2 = - surfaceB2 * surfaceB2;
				
				// Try to move a rect from i to j
				if(costB1 <= costB2){
					for(Rectangle r: b1.getRectangles()){
						if(b2.tryInsertRectangleBySize(r, allowedOverlapping)){
							b1.removeRectangle(r);
							r.highlight = true;
							return new GeometricSolution(newBoxes);
						}
					}
				}
			}
		}
		
		// Try to swap two rectangles of different boxes
		Rectangle r1, r2;
		for(i = 0; i < newBoxes.size(); i++){
			for(j = newBoxes.size() - 1; j >= 0; j--){
				if(i == j){
					continue;
				}
				
				b1 = newBoxes.get(i);
				b2 = newBoxes.get(j);
				surfaceB1 = b1.getFreeSurface();
				surfaceB2 = b2.getFreeSurface();
				
				costB1 = - surfaceB1 * surfaceB1;
				costB2 = - surfaceB2 * surfaceB2;
				
				// Try to swap a rect between i and j
				for(k = 0; k < b1.getRectangles().size(); k++){
					r1 = b1.getRectangles().get(k);
					for(l = 0; l < b2.getRectangles().size(); l++){
						r2 = b2.getRectangles().get(l);
						if(
							b1.getFreeSurface() + r1.getSurface() >= r2.getSurface()
							&& b2.getFreeSurface() + r2.getSurface() >= r1.getSurface()
							&& (r1.getLongSide()!= r2.getLongSide() || r1.getShortSide() != r2.getShortSide())
							&& - (surfaceB1 + r1.getSurface() - r2.getSurface()) * (surfaceB1 + r1.getSurface() - r2.getSurface()) 
								- (surfaceB2 + r2.getSurface() - r1.getSurface()) * (surfaceB2 + r2.getSurface() - r1.getSurface())
								< costB1 + costB2
						){
							b1.saveCurrentState();
							b2.saveCurrentState();
							
							b1.removeRectangle(r1);
							b2.removeRectangle(r2);
							if(b1.tryInsertRectangleBySize(r2, allowedOverlapping) && b2.tryInsertRectangleBySize(r1, allowedOverlapping)){
								r1.highlight = true;
								r2.highlight = true;
								return new GeometricSolution(newBoxes);
							}else{
								b1.restoreSavedState();
								b2.restoreSavedState();
							}
						}
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public Solution getRandomNeighbor(FeasibleSolution solution) {
		GeometricSolution s = (GeometricSolution) solution;
		Random rand = new Random();
		ArrayList<Box> newBoxes = new ArrayList<Box>();
		ArrayList<Rectangle> newRectangles = new ArrayList<Rectangle>();
		ArrayList<Integer> selectableBoxIndexes = new ArrayList<Integer>();
		int i = 0;
		for(Box b: s.boxes){
			newBoxes.add(b.deepCopy());
			newRectangles.addAll(newBoxes.get(newBoxes.size() - 1).getRectangles());
			selectableBoxIndexes.add(i);
			i++;
		}
		
		Rectangle r1 = newRectangles.get(rand.nextInt(newRectangles.size()));
		Box b1 = r1.getBox();
		selectableBoxIndexes.remove(new Integer(newBoxes.indexOf(b1)));
		
		while(!selectableBoxIndexes.isEmpty()){
			i = selectableBoxIndexes.get(
				rand.nextInt(
					selectableBoxIndexes.size()
				)
			);
			Box b2 = newBoxes.get(i);
			
			b1.removeRectangle(r1);
			
			// Try to insert r into b
			if(b2.tryInsertRectangleBySize(r1, allowedOverlapping)){
				r1.highlight = true;
				return new GeometricSolution(newBoxes);
			}
			
			// Try to swap r with rectangle(s) in b
			ArrayList<Rectangle> testRectangles = new ArrayList<Rectangle>(b2.getRectangles());
			Rectangle r2;
			b1.saveCurrentState();
			b2.saveCurrentState();
			
			while(!testRectangles.isEmpty()){
				r2 = testRectangles.remove(rand.nextInt(testRectangles.size()));
				b2.removeRectangle(r2);
				
				if(b1.tryInsertRectangleBySize(r2, allowedOverlapping) && b2.tryInsertRectangleBySize(r1, allowedOverlapping)){
					r1.highlight = true;
					r2.highlight = true;
					return new GeometricSolution(newBoxes);
				}
				b1.restoreSavedState();
				b2.restoreSavedState();
			}
			selectableBoxIndexes.remove(new Integer(i));
		}
		newBoxes.add(new Box(newBoxes.size(), newBoxes.get(0).getLength()));
		newBoxes.get(newBoxes.size() - 1).tryInsertRectangle(r1, allowedOverlapping);
		return new GeometricSolution(newBoxes);
		
	}
}
