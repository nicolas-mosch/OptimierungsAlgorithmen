package geometric_models;

import java.util.HashSet;

/**
 * @author Nicolas
 *
 */
public class Rectangle  implements Comparable<Rectangle>{
	private final int id;
	private final int longSide, shortSide;
	private final int surface;
	public boolean highlight;
	private Box box;
	private int x, y;
	private boolean isHorizontal;
	private HashSet<Point> points;
	private boolean hasChanged;
	
	private Box savedBox;
	private int savedX, savedY;
	private boolean savedIsHorizontal;
	private HashSet<Point> savedPoints;
	
	
	/**
	 * @param id
	 * @param a
	 * @param b
	 */
	
	public Rectangle(int id, int a, int b){
		if(a > b){
			longSide = a;
			shortSide = b;
		} else{
			longSide = b;
			shortSide = a;
		}
		this.id = id;
		this.x = 0;
		this.y = 0;
		highlight = false;
		points = new HashSet<Point>();
		hasChanged = false;
		surface = a * b;
	}
	
	public Rectangle(Rectangle r){
		id = r.id;
		longSide = r.longSide;
		shortSide = r.shortSide;
		x = r.x;
		y = r.y;
		isHorizontal = r.isHorizontal;
		highlight = false;
		points = new HashSet<Point>(r.points);
		hasChanged = r.hasChanged;
		surface = r.surface;
	}
	
	public int getLongSide(){
		return longSide;
	}
	
	public int getShortSide(){
		return shortSide;
	}
	
	public int getSurface(){
		return surface;
	}
	
	public int getId(){
		return id;
	}
	
	public void setPosition(int x, int y){
		hasChanged = this.x != x || this.y != y;
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getWidth(){
		if(isHorizontal)
			return this.longSide;
		else
			return this.shortSide;
	}
	
	public int getHeight(){
		if(!isHorizontal)
			return this.longSide;
		else
			return this.shortSide;
	}
	
	public double getOverlappingPercentage(Rectangle r){
		/*
		int left = Math.max(x, r.x);
		int right = Math.min(x + this.getWidth(), r.x + r.getWidth());
		int bottom = Math.max(y, r.y);
		int top = Math.min(y + this.getHeight(), r.y + r.getHeight());
		
		if(left < right && bottom < top){
			return 0;
		}
		return ((right - left) * (top - bottom)) / Math.max(this.getSurface(), r.getSurface());
		 */
		HashSet<Point> intersection = new HashSet<Point>(r.getPoints());
		intersection.retainAll(this.getPoints());
		return intersection.size() / Math.max(this.getSurface(), r.getSurface());
	}
	
	public void setHorizontal(){
		hasChanged = !isHorizontal;
		isHorizontal = true;
	}
	
	public void setVertical(){
		hasChanged = isHorizontal;
		isHorizontal = false;
	}
	
	public boolean isHorizontal(){
		return isHorizontal;
	}
	
	void setBox(Box b){
		hasChanged = box != b;
		this.box = b;
	}
	
	public Box getBox(){
		return box;
	}
	
	@Override
	public int compareTo(Rectangle o) {
		if(this.getLongSide() == o.getLongSide()){
			return o.getShortSide() - this.getShortSide();
		}
		return o.getLongSide() - this.getLongSide();
	}
	
	@Override
	public String toString(){
		return "R" + id + ": (" + x + "," + y + "), " + this.getWidth() + "x" + this.getHeight() + ", B" + this.box.getId();
	}
	
	public void saveCurrentPosition(){
		savedBox = box;
		savedX = x;
		savedY = y;
		savedIsHorizontal = isHorizontal;
		savedPoints = new HashSet<Point>(points);
	}
	
	public void restoreSavedPosition(){
		box = savedBox;
		x = savedX;
		y = savedY;
		isHorizontal = savedIsHorizontal;
		points = new HashSet<Point>(savedPoints);
	}
	
	public boolean isLike(Rectangle r){
		return longSide == r.longSide && shortSide == r.shortSide; 
	}
	
	public HashSet<Point> getPoints(){
		if(hasChanged){
			points.clear();
			int i, j;
			for(i = x; i < x + getWidth(); i++){
				for(j = y; j < y + getHeight(); j++){
					points.add(new Point(i, j));
				}
			}
		}
		return points;
	}
	
	public void removeFromBox(){
		if(box != null){
			box.removeRectangle(this);
			box = null;
		}
	}
	
	public boolean sameAs(Rectangle r){
		return id == r.id && box.getId() == r.box.getId() && x == r.x && y == r.y && isHorizontal == r.isHorizontal;
	}
}


