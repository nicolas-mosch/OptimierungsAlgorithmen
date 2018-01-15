package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import interfaces.Feature;

public class PermutationBinPackingSolution extends BinPackingSolution{

	private LinkedList<Box> modifiedBoxes;
	private Pair<Integer, Integer> swap;
	HashMap<Box, Double> originalBoxCosts;
	
	
	public PermutationBinPackingSolution(ArrayList<BinPackingRectangle> newRectangles, ArrayList<Box> newBoxes) {
		super(newRectangles, newBoxes);
		modifiedBoxes = new LinkedList<Box>();
		originalBoxCosts = new HashMap<Box, Double>();
	}

	public PermutationBinPackingSolution(ArrayList<BinPackingRectangle> rectangles, int boxLength, int allowedOverlapping) {
		super(rectangles, boxLength, allowedOverlapping);
		modifiedBoxes = new LinkedList<Box>();
		originalBoxCosts = new HashMap<Box, Double>();
	}

	@Override
	public HashSet<Feature> getFeatures() {
		HashSet<Feature> features = new HashSet<Feature>();
		if(super.rectangles == null){
			return features;
		}
		for(int i = 0; i < super.rectangles.size(); i++){
			features.add(
				new PermutationBinPackingSolutionFeature(
					super.rectangles.get(i).getId(),
					//i == 0 ? -1 : super.rectangles.get(i - 1).getId()
					i
				)
			);
		}
		return features;
	}
	
	public PermutationBinPackingSolution deepCopy(){
		ArrayList<BinPackingRectangle> newRectangles = new ArrayList<BinPackingRectangle>();
		ArrayList<Box> newBoxes = new ArrayList<Box>();
		Box box;
		for(Box b: boxes){
			box = b.deepCopy();
			newRectangles.addAll(box.getRectangles());
			newBoxes.add(box);
			box.saveCurrentState();
		}
		
		return new PermutationBinPackingSolution(newRectangles, newBoxes);
	}
	
	public void swapRectanglePositions(int i, int j, float allowedOverlapping){
		swap = new Pair<Integer, Integer>(i, j);
		
		BinPackingRectangle r1 = rectangles.get(i);
		BinPackingRectangle r2 = rectangles.get(j);
		
		Box b1 = r1.getBox();
		Box b2 = r2.getBox();
		
		boolean wasFirstInBox1 = b1.getFirstInsertedRectangle() == r1;
		boolean wasFirstInBox2 = b2.getFirstInsertedRectangle() == r2;
		
		rectangles.set(i, r2);
		rectangles.set(j, r1);
		
		
		int ri = i;
		Box box = wasFirstInBox1 && i > 0 ? rectangles.get(i - 1).getBox() : b1;
		int bi = boxes.indexOf(box);

		modifiedBoxes.add(box);
		
		if(box == b1){
			box.removeRectanglesInsertedFrom(r1);
		}
		
		BinPackingRectangle r;
		
		while(ri < rectangles.size()){
			r = rectangles.get(ri);
			if(!box.tryInsertRectangle(r, allowedOverlapping)){
				++bi;
				if(bi == boxes.size()){
					// if reached end of boxes, add a new one
					box = new Box(boxes.size(), box.getLength());
					boxes.add(box);
					modifiedBoxes.add(box);
					continue;
				}
				box = boxes.get(bi);
				if(r == box.getFirstInsertedRectangle() && r.getX() == 0 && box != b2 && box != b1){
					// until we reach a box which doesn't actually change from new permutation
					break;
				}
				modifiedBoxes.add(box);
				box.clear();
			}
			else{
				++ri;
			}
		}
		
		if(ri <= j){
			// if we didn't reach the right index from previous changes
			//  then changes are necessary starting from that index as well
			
			box = wasFirstInBox2 ? rectangles.get(j - 1).getBox() : b2;
			if(ri < j){
				modifiedBoxes.add(box);
			}
			
			bi = boxes.indexOf(box);
			
			if(box == b2){
				box.removeRectanglesInsertedFrom(r2);
			}
			ri = j;
			while(ri < rectangles.size()){
				r = rectangles.get(ri);
				if(!box.tryInsertRectangle(r, allowedOverlapping)){
					++bi;
					if(bi == boxes.size()){
						// if reached end of boxes, add a new one
						box = new Box(boxes.size(), box.getLength());
						boxes.add(box);
						modifiedBoxes.add(box);
						continue;
					}
					box = boxes.get(bi);
					if(r == box.getFirstInsertedRectangle() && r.getX() == 0 && r.getY() == 0 && r != r2){
						// until we reach a box which doesn't actually change from new permutation
						break;
					}
					modifiedBoxes.add(box);
					box.clear();
				}else{
					++ri;
				}
			}
		}
		++bi;
		
		while(bi < boxes.size()){
			// remove empty/invalid boxes at end
			if(boxes.get(bi).isEmpty() || boxes.get(bi).getFirstInsertedRectangle().getBox() != boxes.get(bi)){
				modifiedBoxes.add(boxes.get(bi));
				boxes.remove(bi);
				
			}
			else{
				++bi;
			}
		}
	}
	
	public void restoreToPreSwapState(){
		Box b;
		while(!modifiedBoxes.isEmpty()){
			b = modifiedBoxes.removeFirst();
			if(!originalBoxCosts.containsKey(b)){
				boxes.remove(b);
				continue;
			}
			if(!boxes.contains(b)){
				boxes.add(b);
			}
			b.restoreSavedState();
		}
		originalBoxCosts.clear();
		BinPackingRectangle r = rectangles.get(swap.a);
		rectangles.set(swap.a, rectangles.get(swap.b));
		rectangles.set(swap.b, r);
	}

}
