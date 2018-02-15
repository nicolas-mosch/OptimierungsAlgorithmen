package geometric_models;

import java.util.HashSet;

/**
 * @author Nicolas
 *
 */
public class BinPackingRectangle  implements Comparable<BinPackingRectangle>{
	private final int longSide, shortSide;
	private final int id;
	public boolean highlight;
	private Box box;
	private int x, y;
	private boolean isHorizontal;
	
	private Box savedBox;
	private int savedX, savedY;
	private boolean savedIsHorizontal;
	
	/**
	 * @param id
	 * @param a
	 * @param b
	 */
	
	public BinPackingRectangle(int id, int a, int b){
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
	}
	
	public BinPackingRectangle(BinPackingRectangle r){
		id = r.id;
		longSide = r.longSide;
		shortSide = r.shortSide;
		x = r.x;
		y = r.y;
		isHorizontal = r.isHorizontal;
		highlight = false;
	}
	
	public int getLongSide(){
		return longSide;
	}
	
	public int getShortSide(){
		return shortSide;
	}
	
	public int getSurface(){
		return longSide * shortSide;
	}
	
	public int getId(){
		return id;
	}
	
	public void setPosition(int x, int y){
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
	
	public boolean overlaps(BinPackingRectangle r, float allowedOverlapping){
		if(allowedOverlapping == 0){
			return x < r.x + r.getWidth() && x + getWidth() > r.x && y < r.y + r.getHeight() && y + getHeight() > r.y;
		}
		
		int left = Math.max(x, r.x);
		int right = Math.min(x + this.getWidth(), r.x + r.getWidth());
		int bottom = Math.max(y, r.y);
		int top = Math.min(y + this.getHeight(), r.y + r.getHeight());
		
		if(left < right && bottom < top){
			return false;
		}
		
		return (((right - left) * (top - bottom)) / Math.max(this.getSurface(), r.getSurface())) <= allowedOverlapping;
	}
	
	public double getOverlappngPercentage(BinPackingRectangle r){
		int left = Math.max(x, r.x);
		int right = Math.min(x + this.getWidth(), r.x + r.getWidth());
		int bottom = Math.max(y, r.y);
		int top = Math.min(y + this.getHeight(), r.y + r.getHeight());
		
		if(left < right && bottom < top){
			return 0;
		}
		
		return ((right - left) * (top - bottom)) / Math.max(this.getSurface(), r.getSurface());
	}
	
	public void setHorizontal(){
		this.isHorizontal = true;
	}
	
	public void setVertical(){
		this.isHorizontal = false;
	}
	
	public boolean isHorizontal(){
		return this.isHorizontal;
	}
	
	void setBox(Box b){
		this.box = b;
	}
	
	public Box getBox(){
		return box;
	}
	
	@Override
	public int compareTo(BinPackingRectangle o) {
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
	}
	
	public void restoreSavedPosition(){
		box = savedBox;
		x = savedX;
		y = savedY;
		isHorizontal = savedIsHorizontal;
	}
	
	public boolean isLike(BinPackingRectangle r){
		return longSide == r.longSide && shortSide == r.shortSide; 
	}
	
	public HashSet<Point> getPoints(){
		HashSet<Point> result = new HashSet<Point>();
		int i, j;
		for(i = x; i < x + getWidth(); i++){
			for(j = y; j < y + getHeight(); j++){
				result.add(new Point(i, j));
			}
		}
		return result;
	}
	
	public void removeFromBox(){
		if(box != null){
			box.removeRectangle(this);
		}
	}
	
	public boolean sameAs(BinPackingRectangle r){
		return id == r.id && box.getId() == r.box.getId() && x == r.x && y == r.y && isHorizontal == r.isHorizontal;
	}
}


