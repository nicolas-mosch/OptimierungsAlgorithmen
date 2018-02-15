package binpacking_models;

import interfaces.Feature;

public class PermutationBinPackingSolutionFeature extends Feature {
	public final int rectangleId;
	public final int leftRectangleId;
	
	public PermutationBinPackingSolutionFeature(int id, int left){
		rectangleId = id;
		leftRectangleId = left;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) 
			return true;
        if (other == null || getClass() != other.getClass()) 
        	return false;
        PermutationBinPackingSolutionFeature o = (PermutationBinPackingSolutionFeature) other;
        
		return rectangleId == o.rectangleId; 
			//&& leftRectangleId == o.leftRectangleId;
		
	}
	
	@Override
	public int hashCode(){
		//int result = rectangleId * 31 + leftRectangleId;
        return rectangleId;
	}

}
