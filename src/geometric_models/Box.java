package geometric_models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 * @author Nicolas
 *
 */
/**
 * @author Nicolas
 *
 */


//TODO: Make Box subclasses for each type of insertion (inorder with priorityQueue, not in-order) 							??
//TODO: Dont use save/restore state. Just copy instead box instead when necessary. Or handle unused stored data better.		??
//TODO: Get rid of totalOverlap. Keep track of it with int occurrences = Collections.frequency(animals, "bat");				??


public class Box implements Comparable<Box>{
	private final int id, length;
	private double cost, storedCost;
	private boolean hasChanged, storedHasChanged;
	private ArrayList<Rectangle> rectangles, storedRectangles;
	private double totalOverlap;
	private HashSet<Point> freePoints, storedFreePoints;
	private ArrayList<Point> occupiedPoints, storedOccupiedPoints;
	
	/**
	 * @param length
	 */
	public Box(int id, int length) {
		this.length = length;
		rectangles = new ArrayList<Rectangle>();
		this.id = id; 
		hasChanged = false;
		cost = 0;
		totalOverlap = 0;
		int i, j;
		freePoints = new HashSet<Point>();
		occupiedPoints = new ArrayList<Point>();
		for(i = 0; i < length; i++){
			for(j = 0; j < length; j++){
				freePoints.add(new Point(i, j));
			}
		}
	}

	public int getLength() {
		return length;
	}

	public int getFreeSurface() {
		return freePoints.size();
	}
 
	
	public int getOccupiedSurface() {
		return length*length - getFreeSurface();
	}
	
	/** Try to insert a rectangle in first position where it fits
	 *  Attempt is performed from bottom left corner to top right.
	 *  First horizontally, then vertically
	 * @param n
	 * @return boolean whether or not the rectangle was inserted
	 */
	public void insertRectangle(Rectangle n){
		rectangles.add(n);
		HashSet<Point> rectanglePoints = n.getPoints();
		freePoints.removeAll(rectanglePoints);
		occupiedPoints.addAll(rectanglePoints);
		hasChanged = true;
	}
	
	/** Try to insert a rectangle in first position where it fits
	 *  Attempt is performed from bottom left corner to top right.
	 *  First horizontally, then vertically
	 * @param n
	 * @return boolean whether or not the rectangle was inserted
	 */
	public boolean tryInsertRectangle(Rectangle n, float allowedOverlapping){
		if(getFreeSurface() < n.getSurface())
			return false;
		int row, col;
		n.saveCurrentPosition();
		double totalOverlappingAtPosition, overlapWithRectangle;
		
		// Try to place vertically
		n.setVertical();
		for(row = 0; row <= length - n.getLongSide(); row++){
			col = 0;
			columnloop:
			do{
				n.setPosition(col, row);
				totalOverlappingAtPosition = 0;
				for(Rectangle r: rectangles){
					overlapWithRectangle = n.getOverlappingPercentage(r);
					if(overlapWithRectangle > allowedOverlapping){
						col = r.getX() + r.getWidth();
						continue columnloop;
					}
					totalOverlappingAtPosition += overlapWithRectangle;
				}
				n.setBox(this);
				rectangles.add(n);
				hasChanged = true;
				totalOverlap += totalOverlappingAtPosition;
				occupiedPoints.addAll(n.getPoints());
				freePoints.removeAll(n.getPoints());
				return true;
			}while(col <= length - n.getShortSide());
		}
		
		if(n.getLongSide() != n.getShortSide()){
			// Try to place horizontally
			n.setHorizontal();
			for(row = 0; row <= length - n.getShortSide(); row++){
				col = 0;
				columnloop:
				do{
					n.setPosition(col, row);
					totalOverlappingAtPosition = 0;
					for(Rectangle r: rectangles){
						overlapWithRectangle = n.getOverlappingPercentage(r);
						if(overlapWithRectangle > allowedOverlapping){
							col = r.getX() + r.getWidth();
							continue columnloop;
						}
						totalOverlappingAtPosition += overlapWithRectangle;
					}
					n.setBox(this);
					rectangles.add(n);
					hasChanged = true;
					totalOverlap += totalOverlappingAtPosition;
					occupiedPoints.addAll(n.getPoints());
					freePoints.removeAll(n.getPoints());
					return true;
				}while(col <= length - n.getLongSide());
			}
		}
		n.restoreSavedPosition();
		return false;
	}
	
	/**
	 * Tries to insert the given rectangle in order (by size)
	 * 
	 * @param n the new rectangle to be inserted
	 * @param allowedOverlapping the percentage of allowed overlapping
	 * @return whether the rectangle was inserted or not
	 */
	public boolean tryInsertRectangleBySize(Rectangle n, float allowedOverlapping){
		if(getFreeSurface() < (n.getSurface() - (n.getSurface() * allowedOverlapping) / 100 )){
			return false;
		}
		Rectangle c;
		int i, j;
		int row, col;
		double totalOverlapAtPosition = 0, overlapWithRectangle;
		
		Collections.sort(rectangles); //TODO: probably not always necessary
		
		this.saveCurrentState();
		
		i = rectangles.size();
		while(rectangles.get(i - 1).compareTo(n) <= 0){
			--i;
			
		}
		rectangles.add(i, n);
		
		// Insert n Re-Insert rectangles larger than n in order by size
		outerloop:
		for(; i < rectangles.size(); i++){
			c = rectangles.get(i);
			c.saveCurrentPosition();
			
			// Try to place vertically
			c.setVertical();
			for(row = 0; row <= length - c.getLongSide(); row++){
				col = 0;
				columnloop:
				do{
					c.setPosition(col, row);
					totalOverlapAtPosition = 0;
					// Compare to rectangles inserted previously (i.e. which are larger in area)
					for(j = 0; j < i; j++){
						overlapWithRectangle = c.getOverlappingPercentage(rectangles.get(j));
						if(overlapWithRectangle > allowedOverlapping){
							col = rectangles.get(j).getX() + rectangles.get(j).getWidth();
							continue columnloop;
						}
						totalOverlapAtPosition += overlapWithRectangle; 
					}
					continue outerloop;
				}while(col <= length - c.getShortSide());
			}
			
			if(c.getLongSide() != c.getShortSide()){
				// Try to place horizontally
				c.setHorizontal();
				for(row = 0; row <= length - c.getShortSide(); row++){
					col = 0;
					columnloop:
					do{
						c.setPosition(col, row);
						totalOverlapAtPosition = 0;
						for(j = 0; j < i; j++){
							overlapWithRectangle = c.getOverlappingPercentage(rectangles.get(j));
							if(overlapWithRectangle > allowedOverlapping){
								col = rectangles.get(j).getX() + rectangles.get(j).getWidth();
								continue columnloop;
							}
							totalOverlapAtPosition += overlapWithRectangle; 
						}
						continue outerloop;
					}while(col <= length - c.getLongSide());
				}
			}
			
			// Could not place, restore original positions
			for(j = 0; j <= i; j++){
				rectangles.get(j).restoreSavedPosition();
			}
			rectangles.remove(n);
			return false;
		}
		totalOverlap += totalOverlapAtPosition;
		n.setBox(this);
		hasChanged = true;
		return true;
	}
	
	/** Removes the given rectangle from the box if contained
	 * @param r
	 * @return
	 */
	public boolean removeRectangle(Rectangle r){
		if(rectangles.remove(r)){
			for(Rectangle s: rectangles){
				totalOverlap -= r.getOverlappingPercentage(s);
			}
			
			hasChanged = true;
			return true;
		}
		return false;
	}
	
	/** Removes all the rectangles inserted after the given rectangle and the given rectangle itself.
	 * @param r
	 * @return
	 */
	public boolean removeRectanglesInsertedFrom(Rectangle r){
		if(!rectangles.contains(r))
			return false;
		while(r != rectangles.remove(rectangles.size() - 1));
		hasChanged = true;
		return true;
	}
	
	
	public int getId(){
		return id;
	}

	public ArrayList<Rectangle> getRectangles() {
		return rectangles;
	}
	
	public boolean isEmpty(){
		return rectangles.isEmpty();
	}
	
	public boolean isFull(){
		return getFreeSurface() == 0;
	}
	
	@Override
	public String toString(){
		String result = "B" + id + " (" + this.getFreeSurface() + ") "+ this.hashCode() + ":\n";
		String tmp;
		
		String sum = "";
		
		int largestRectId = 0;
		String emptySlot = "";
		
		int[][] newArea = new int[length][length];
		for(Rectangle r: rectangles){
			sum+= r + " | ";
			
			for(int row = r.getY(); row < r.getY() + r.getHeight(); row++){
				for(int col = r.getX(); col < r.getX() + r.getWidth(); col++){
					newArea[row][col] = r.getId();
				}
			}
			
			if(r.getId() > largestRectId){
				largestRectId = r.getId();
			}
		}
		
		for(int i = 0; i < ("" + largestRectId).length(); i++){
			emptySlot += " ";
		}
		
		for(int row = length - 1; row >= 0; row--){
			for(int col = 0; col < length; col++){
				if(newArea[row][col] == 0){
					tmp = emptySlot;
				}else{
					tmp = "" + newArea[row][col];
					for(int i = tmp.length(); i < emptySlot.length(); i++){
						tmp = "0" + tmp;
					}
				}
				result += "|" + tmp + "|";
			}
			result += "\n";
		}
		
		return result + "Contained rects: " + sum + "\n";
	}
	
	@Override
	public int compareTo(Box o) {
		return o.getFreeSurface() - getFreeSurface();
	}
	
	public Box deepCopy(){
		Box res = new Box(id, length);
		Rectangle rcopy;
		for(Rectangle r : rectangles){
			rcopy = new Rectangle(r);
			rcopy.setBox(res);
			res.rectangles.add(rcopy);
		}
		res.cost = this.cost;
		res.hasChanged = this.hasChanged;
		return res;
	}
	
	public Box shallowCopy(){
		Box res = new Box(id, length);
		res.rectangles.addAll(rectangles);
		return res;
	}
	
	public boolean contains(Rectangle r){
		return rectangles.contains(r);
	}
	
	public Rectangle getFirstInsertedRectangle(){
		return rectangles.get(0);
	}
	
	public void clear(){
		rectangles.clear();
	}
	
	public void saveCurrentState(){
		storedRectangles = new ArrayList<Rectangle>(rectangles);
		storedFreePoints = new HashSet<Point>(freePoints);
		storedOccupiedPoints = new ArrayList<Point>(occupiedPoints);
		storedHasChanged = hasChanged;
		for(Rectangle r: storedRectangles){
			r.saveCurrentPosition();
		}
		storedCost = cost;
	}
	
	public void restoreSavedState(){
		rectangles = new ArrayList<Rectangle>(storedRectangles);
		freePoints = new HashSet<Point>(storedFreePoints);
		occupiedPoints = new ArrayList<Point>(storedOccupiedPoints);
		cost = storedCost;
		hasChanged = storedHasChanged;
		for(Rectangle r: rectangles){
			r.restoreSavedPosition();
		}
	}

	public boolean isLike(Box box) {
		Rectangle r;
		for(int i = 0; i < rectangles.size(); i++){
			r = rectangles.get(i);
			if(!r.sameAs(box.rectangles.get(i))){
				return false;
			}
		}
		return true;
	}
	
	public double getCost(){
		if(hasChanged){
			//TODO: consider overlapping
			cost = Math.pow(this.getOccupiedSurface(), 2) / Math.pow(length, 5);
			hasChanged = false;
		}
		return cost;
	}
	
}