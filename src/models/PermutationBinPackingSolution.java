package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import interfaces.Feature;

public class PermutationBinPackingSolution extends BinPackingSolution{

	public PermutationBinPackingSolution(ArrayList<BinPackingRectangle> newRectangles, ArrayList<Box> newBoxes) {
		super(newRectangles, newBoxes);
	}

	public PermutationBinPackingSolution(ArrayList<BinPackingRectangle> rectangles, int boxLength, int allowedOverlapping) {
		super(rectangles, boxLength, allowedOverlapping);
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
}
