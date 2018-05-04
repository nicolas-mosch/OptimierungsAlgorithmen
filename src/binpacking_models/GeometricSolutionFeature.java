package binpacking_models;

import geometric_models.Rectangle;
import interfaces.Feature;

public class GeometricSolutionFeature extends Feature{
	public final int rectangleId;
	public final int boxId;
	
	public GeometricSolutionFeature(Rectangle r){
		rectangleId = r.getId();
		boxId = r.getBox().getId();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) 
			return true;
        if (other == null || getClass() != other.getClass()) 
        	return false;
        GeometricSolutionFeature o = (GeometricSolutionFeature) other;
        
		return rectangleId == o.rectangleId 
			&& boxId == o.boxId; 
	}
	
	@Override
	public int hashCode(){
		int result = (rectangleId ^ (rectangleId >>> 32));
        result = 31 * result + (int) (boxId ^ (boxId >>> 32));
        return result;
	}	
}