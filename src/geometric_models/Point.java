package geometric_models;

public class Point implements Comparable<Point> {
	public final int x;
	public final int y;
	public Point(int x, int y){
		this.x = x; this.y = y;
	}
	@Override
	public int compareTo(Point o) {
		if(y == o.y){
			return x - o.x;
		}
		return y - o.y;
	}
	
	@Override
	public boolean equals(Object other){
		if(this == other)
			return true;
		if(!(other instanceof Point))
			return false;
		
		Point otherPosition = (Point) other;
		
		
		return x == otherPosition.x && y == otherPosition.y;
	}
	
	@Override
	public int hashCode(){
		return x * 31 + y;
	}
}
