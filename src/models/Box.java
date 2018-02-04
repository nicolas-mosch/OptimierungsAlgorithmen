package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author Nicolas
 *
 */
/**
 * @author Nicolas
 *
 */
public class Box implements Comparable<Box>{
	private int length;
	private ArrayList<BinPackingRectangle> rectangles;
	private final int id;
	private ArrayList<BinPackingRectangle> storedRectangles; 
	/**
	 * @param length
	 */
	public Box(int id, int length) {
		this.length = length;
		rectangles = new ArrayList<BinPackingRectangle>();
		this.id = id; 
		
	}

	public int getLength() {
		return length;
	}

	public int getFreeSurface() {
		int freeSurface = length * length;
		for(BinPackingRectangle r: rectangles){
			freeSurface -= r.getSurface();
		}
		return freeSurface;
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
	public boolean tryInsertRectangle(BinPackingRectangle n, float allowedOverlapping){
		if(getFreeSurface() < n.getSurface())
			return false;
		int row, col;
		n.saveCurrentPosition();
		
		
		// Try to place vertically
		n.setVertical();
		for(row = 0; row <= length - n.getLongSide(); row++){
			col = 0;
			columnloop:
			do{
				n.setPosition(col, row);
				for(BinPackingRectangle r: rectangles){
					if(n.overlaps(r, allowedOverlapping)){
						col = r.getX() + r.getWidth();
						continue columnloop;
					}
				}
				n.setBox(this);
				rectangles.add(n);
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
					for(BinPackingRectangle r: rectangles){
						if(n.overlaps(r, allowedOverlapping)){
							col = r.getX() + r.getWidth();
							continue columnloop;
						}
					}
					n.setBox(this);
					rectangles.add(n);
					//System.out.println("Success:");
					//System.out.println(this);
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
	public boolean tryInsertRectangleBySize(BinPackingRectangle n, float allowedOverlapping){
		if(getFreeSurface() < n.getSurface()){
			return false;
		}
		BinPackingRectangle c;
		int i, j;
		int row, col;
		rectangles.add(n);
		Collections.sort(rectangles);
		
		outerloop:
		for(i = 0; i < rectangles.size(); i++){
			c = rectangles.get(i);
			c.saveCurrentPosition();
			
			// Try to place vertically
			c.setVertical();
			for(row = 0; row <= length - c.getLongSide(); row++){
				col = 0;
				columnloop:
				do{
					c.setPosition(col, row);
					for(j = 0; j < i; j++){
						if(c.overlaps(rectangles.get(j), allowedOverlapping)){
							col = rectangles.get(j).getX() + rectangles.get(j).getWidth();
							continue columnloop;
						}
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
						for(j = 0; j < i; j++){
							if(c.overlaps(rectangles.get(j), allowedOverlapping)){
								col = rectangles.get(j).getX() + rectangles.get(j).getWidth();
								continue columnloop;
							}
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
		n.setBox(this);
		return true;
	}
	
	/** Attempts to insert the rectangle at some random position in the box
	 * @param n
	 * @param allowedOverlapping
	 * @return
	 */
	public boolean insertRectangleAtRandom(BinPackingRectangle n, float allowedOverlapping){
		Random rand = new Random();
		if(rand.nextBoolean()){
			n.setHorizontal();
		}else{
			n.setVertical();
		}
		
		n.setPosition(rand.nextInt(length - n.getWidth()), rand.nextInt(length - n.getHeight()));
		for(BinPackingRectangle r: rectangles){
			if(n.overlaps(r, allowedOverlapping)){
				return false;
			}
		}
		System.out.println("Successfully inserted |" + n + "| into box " + id + ": 1");
		rectangles.add(n);
		n.setBox(this); 
		return true;
	}
	
	
	/** Inserts the given rectangle while keeping its current position, and without any additional checks.
	 * @param n
	 */
	public void insertRectangleAtPosition(BinPackingRectangle n){
		rectangles.add(n);
		n.setBox(this); 
	}
	
	/** Removes the given rectangle from the box if contained
	 * @param r
	 * @return
	 */
	public boolean removeRectangle(BinPackingRectangle r){
		if(rectangles.remove(r)){ 
			return true;
		}
		return false;
	}
	
	/** Removes all the rectangles inserted after the given rectangle and the given rectangle itself.
	 * @param r
	 * @return
	 */
	public boolean removeRectanglesInsertedFrom(BinPackingRectangle r){
		if(!rectangles.contains(r))
			return false;
		while(r != rectangles.remove(rectangles.size() - 1));
		return true;
	}
	
	
	public int getId(){
		return id;
	}

	public ArrayList<BinPackingRectangle> getRectangles() {
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
		for(BinPackingRectangle r: rectangles){
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
		BinPackingRectangle rcopy;
		for(BinPackingRectangle r : rectangles){
			rcopy = new BinPackingRectangle(r);
			rcopy.setBox(res);
			res.rectangles.add(rcopy);
		}
		return res;
	}
	
	public Box shallowCopy(){
		Box res = new Box(id, length);
		res.rectangles.addAll(rectangles);
		return res;
	}
	
	public boolean contains(BinPackingRectangle r){
		return rectangles.contains(r);
	}
	
	public BinPackingRectangle getFirstInsertedRectangle(){
		return rectangles.get(0);
	}
	
	public void clear(){
		rectangles.clear();
	}
	
	public void saveCurrentState(){
		storedRectangles = new ArrayList<BinPackingRectangle>(rectangles);
		for(BinPackingRectangle r: storedRectangles){
			r.saveCurrentPosition();
		}
	}
	
	public void restoreSavedState(){
		rectangles = new ArrayList<BinPackingRectangle>(storedRectangles);
		for(BinPackingRectangle r: rectangles){
			r.restoreSavedPosition();
		}
	}

	public boolean isLike(Box box) {
		BinPackingRectangle r;
		for(int i = 0; i < rectangles.size(); i++){
			r = rectangles.get(i);
			if(!r.sameAs(box.rectangles.get(i))){
				return false;
			}
		}
		return true;
	}
}