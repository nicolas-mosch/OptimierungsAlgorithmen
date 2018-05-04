package binpacking_models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import geometric_models.Rectangle;
import geometric_models.Box;
import geometric_models.Pair;
import interfaces.Feature;

public class PermutationSolution extends Solution{

	private LinkedList<Box> modifiedBoxes;
	private Pair<Integer, Integer> swap;
	HashMap<Box, Double> originalBoxCosts;
	
	
	public PermutationSolution(ArrayList<Rectangle> newRectangles, ArrayList<Box> newBoxes) {
		super(newRectangles, newBoxes);
		modifiedBoxes = new LinkedList<Box>();
		originalBoxCosts = new HashMap<Box, Double>();
	}

	public PermutationSolution(ArrayList<Rectangle> rectangles, int boxLength, int allowedOverlapping) {
		super(rectangles, boxLength, allowedOverlapping);
		modifiedBoxes = new LinkedList<Box>();
		originalBoxCosts = new HashMap<Box, Double>();
	}

	public PermutationSolution(ArrayList<Rectangle> permutation, int boxLength) {
		super(permutation, boxLength, 0);
	}

	@Override
	public HashSet<Feature> getFeatures() {
		HashSet<Feature> features = new HashSet<Feature>();
		if(super.rectangles == null){
			return features;
		}
		for(int i = 0; i < super.rectangles.size(); i++){
			features.add(
				new PermutationSolutionFeature(
					super.rectangles.get(i).getId(),
					//i == 0 ? -1 : super.rectangles.get(i - 1).getId()
					i
				)
			);
		}
		return features;
	}
	
	public PermutationSolution deepCopy(){
		ArrayList<Rectangle> newRectangles = new ArrayList<Rectangle>();
		ArrayList<Box> newBoxes = new ArrayList<Box>();
		Box box;
		for(Box b: boxes){
			box = b.deepCopy();
			newRectangles.addAll(box.getRectangles());
			newBoxes.add(box);
			box.saveCurrentState();
		}
		
		return new PermutationSolution(newRectangles, newBoxes);
	}
	
	public void swapRectanglePositions(int i, int j, float allowedOverlapping){
		swap = new Pair<Integer, Integer>(i, j);
		
		Rectangle r1 = rectangles.get(i);
		Rectangle r2 = rectangles.get(j);
		
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
		
		Rectangle r;
		
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
		Rectangle r = rectangles.get(swap.a);
		rectangles.set(swap.a, rectangles.get(swap.b));
		rectangles.set(swap.b, r);
	}

}
