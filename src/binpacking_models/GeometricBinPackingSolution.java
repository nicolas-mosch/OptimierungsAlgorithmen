package binpacking_models;

import java.util.ArrayList;
import java.util.HashSet;

import geometric_models.BinPackingRectangle;
import geometric_models.Box;
import interfaces.Feature;

public class GeometricBinPackingSolution extends BinPackingSolution{
	
	public GeometricBinPackingSolution(ArrayList<Box> newBoxes) {
		super(newBoxes);
	}

	public GeometricBinPackingSolution(ArrayList<BinPackingRectangle> rectangles, int boxLength, int allowedOverlapping) {
		super(rectangles, boxLength, allowedOverlapping);
	}

	@Override
	public HashSet<Feature> getFeatures(){
		HashSet<Feature> features = new HashSet<Feature>();
		for(BinPackingRectangle r: rectangles){
			features.add(new GeometricBinPackingSolutionFeature(r));
		}
		return features;
	}
	
	public GeometricBinPackingSolution deepCopy(){
		ArrayList<Box> newBoxes = new ArrayList<Box>();
		Box box;
		for(Box b: boxes){
			box = b.deepCopy();
			newBoxes.add(box);
			box.saveCurrentState();
		}
		return new GeometricBinPackingSolution(newBoxes);
	}
}
