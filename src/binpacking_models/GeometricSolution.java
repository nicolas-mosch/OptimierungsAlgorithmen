package binpacking_models;

import java.util.ArrayList;
import java.util.HashSet;

import geometric_models.Rectangle;
import geometric_models.Box;
import interfaces.Feature;

public class GeometricSolution extends Solution{
	
	public GeometricSolution(ArrayList<Box> newBoxes) {
		super(newBoxes);
	}

	public GeometricSolution(ArrayList<Rectangle> rectangles, int boxLength, int allowedOverlapping) {
		super(rectangles, boxLength, allowedOverlapping);
	}

	@Override
	public HashSet<Feature> getFeatures(){
		HashSet<Feature> features = new HashSet<Feature>();
		for(Rectangle r: rectangles){
			features.add(new GeometricSolutionFeature(r));
		}
		return features;
	}
	
	public GeometricSolution deepCopy(){
		ArrayList<Box> newBoxes = new ArrayList<Box>();
		Box box;
		for(Box b: boxes){
			box = b.deepCopy();
			newBoxes.add(box);
			box.saveCurrentState();
		}
		return new GeometricSolution(newBoxes);
	}
}
