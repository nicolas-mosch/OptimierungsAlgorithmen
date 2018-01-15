package neighborhoods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import interfaces.FeasibleSolution;
import interfaces.Feature;
import interfaces.Neighborhood;
import models.BinPackingSolution;
import models.Box;
import models.GeometricBinPackingSolution;
import models.GeometricBinPackingSolutionFeature;
import models.BinPackingRectangle;

public class GeometricNeighborhood implements Neighborhood {
	
	private int allowedOverlapping = 0;
	
	private class Move implements Comparable<Move>{
		public final boolean isSwap;
		public final Box destinationBox;
		public final BinPackingRectangle r1, r2;
		double costDelta;
		
		public Move(BinPackingRectangle r1, BinPackingRectangle r2){
			this.r1 = r1;
			this.r2 = r2;
			this.isSwap = true;
			destinationBox = null;
			
			if(r1.getShortSide() == r2.getShortSide() && r1.getLongSide() == r2.getLongSide()){
				costDelta = 0;
			}else{
				Box b1 = r1.getBox();
				Box b2 = r2.getBox();
				
				double oldBoxCost1 = Math.pow(
						b1.getOccupiedSurface(), 2
					) / Math.pow(b1.getLength(), 5);
				
				double oldBoxCost2 = Math.pow(
						b2.getOccupiedSurface(), 2
					) / Math.pow(b2.getLength(), 5);
				
				double newBoxCost1 = Math.pow(
						b1.getOccupiedSurface() - r1.getSurface() + r2.getSurface(), 2
					) / Math.pow(b1.getLength(), 5);
				
				double newBoxCost2 = Math.pow(
						b2.getOccupiedSurface() - r2.getSurface() + r1.getSurface(), 2
					) / Math.pow(b2.getLength(), 5);
				
				costDelta = (oldBoxCost1 + oldBoxCost2) - (newBoxCost1 + newBoxCost2);
			}
		}
		
		public Move(BinPackingRectangle r, Box b){
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
		public int compareTo(Move o) {
			return new Double(costDelta).compareTo(o.costDelta);
		}
	}
	
	
	@Override
	public FeasibleSolution getBestNeighbor(FeasibleSolution solution, Set<Feature> tabooList) {
		GeometricBinPackingSolution s = ((GeometricBinPackingSolution) solution).deepCopy(); 
		
		
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		
		for(BinPackingRectangle r1: s.rectangles){
			if(tabooList.contains(new GeometricBinPackingSolutionFeature(r1))){
				continue;
			}
			
			for(Box b: s.boxes){
				if(r1.getBox() != b && b.getFreeSurface() >= r1.getSurface()){
					possibleMoves.add(new Move(r1, b));
				}
			}
			
			for(BinPackingRectangle r2: s.rectangles){
				if(
					!tabooList.contains(new GeometricBinPackingSolutionFeature(r2))
					&& r1.getBox() != r2.getBox()
					&& r2.getBox().getFreeSurface() + r2.getSurface() >= r1.getSurface()
					&& r1.getBox().getFreeSurface() + r1.getSurface() >= r2.getSurface()
				){
					possibleMoves.add(new Move(r1, r2));
				}
			}
		}
		
		Collections.sort(possibleMoves);
		
		Box b1, b2;
		
		for(Move m: possibleMoves){
			if(m.isSwap){
				b1 = m.r1.getBox();
				b2 = m.r2.getBox();
				b1.removeRectangle(m.r1);
				b2.removeRectangle(m.r2);
				if(
					b1.tryInsertRectangleBySize(m.r2, allowedOverlapping)
					&& b2.tryInsertRectangleBySize(m.r1, allowedOverlapping)
				){
					return s;
				}else{
					b1.removeRectangle(m.r2);
					b2.removeRectangle(m.r1);
					b1.tryInsertRectangleBySize(m.r1, allowedOverlapping);
					b2.tryInsertRectangleBySize(m.r2, allowedOverlapping);
				}
			}else{
				b1 = m.r1.getBox();
				b2 = m.destinationBox;
				if(b2.tryInsertRectangleBySize(m.r1, allowedOverlapping)){
					b1.removeRectangle(m.r1);
					return s;
				}
			}
		}
		
		
		System.err.println("Couldn't find a neighbor");
		return null;
	}

	@Override
	public FeasibleSolution getAugmentingNeighbor(FeasibleSolution solution) {
		GeometricBinPackingSolution s = ((GeometricBinPackingSolution) solution).deepCopy(); 
		
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
					for(BinPackingRectangle r: b1.getRectangles()){
						if(b2.tryInsertRectangleBySize(r, allowedOverlapping)){
							b1.removeRectangle(r);
							r.highlight = true;
							return new GeometricBinPackingSolution(newBoxes);
						}
					}
				}
			}
		}
		
		// Try to swap two rectangles of different boxes
		BinPackingRectangle r1, r2;
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
							b1.removeRectangle(r1);
							b2.removeRectangle(r2);
							if(b1.tryInsertRectangleBySize(r2, allowedOverlapping) && b2.tryInsertRectangleBySize(r1, allowedOverlapping)){
								r1.highlight = true;
								r2.highlight = true;
								return new GeometricBinPackingSolution(newBoxes);
							}else{
								b1.removeRectangle(r2);
								b2.removeRectangle(r1);
								b1.restoreSavedState();
								b2.restoreSavedState();
								b1.insertRectangleAtPosition(r1);
								b2.insertRectangleAtPosition(r2);
							}
						}
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public BinPackingSolution getRandomNeighbor(FeasibleSolution solution) {
		GeometricBinPackingSolution s = (GeometricBinPackingSolution) solution;
		Random rand = new Random();
		ArrayList<Box> newBoxes = new ArrayList<Box>();
		ArrayList<BinPackingRectangle> newRectangles = new ArrayList<BinPackingRectangle>();
		ArrayList<Integer> selectableBoxIndexes = new ArrayList<Integer>();
		int i = 0;
		for(Box b: s.boxes){
			newBoxes.add(b.deepCopy());
			newRectangles.addAll(newBoxes.get(newBoxes.size() - 1).getRectangles());
			selectableBoxIndexes.add(i);
			i++;
		}
		
		BinPackingRectangle r1 = newRectangles.get(rand.nextInt(newRectangles.size()));
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
				return new GeometricBinPackingSolution(newBoxes);
			}
			
			// Try to swap r with rectangle(s) in b
			ArrayList<BinPackingRectangle> testRectangles = new ArrayList<BinPackingRectangle>(b2.getRectangles());
			BinPackingRectangle r2;
			b1.saveCurrentState();
			b2.saveCurrentState();
			
			while(!testRectangles.isEmpty()){
				r2 = testRectangles.remove(rand.nextInt(testRectangles.size()));
				b2.removeRectangle(r2);
				
				if(b1.tryInsertRectangleBySize(r2, allowedOverlapping) && b2.tryInsertRectangleBySize(r1, allowedOverlapping)){
					r1.highlight = true;
					r2.highlight = true;
					return new GeometricBinPackingSolution(newBoxes);
				}
				b1.restoreSavedState();
				b2.restoreSavedState();
			}
			selectableBoxIndexes.remove(new Integer(i));
		}
		newBoxes.add(new Box(newBoxes.size(), newBoxes.get(0).getLength()));
		newBoxes.get(newBoxes.size() - 1).tryInsertRectangle(r1, allowedOverlapping);
		return new GeometricBinPackingSolution(newBoxes);
		
	}
}
