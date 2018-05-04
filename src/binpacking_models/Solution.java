package binpacking_models;

import java.util.ArrayList;

import geometric_models.Rectangle;
import geometric_models.Box;
import interfaces.FeasibleSolution;

public abstract class Solution implements FeasibleSolution {
	
	public ArrayList<Box> boxes;
	public ArrayList<Rectangle> rectangles;
	
	public Solution(ArrayList<Box> boxes){
		rectangles = new ArrayList<Rectangle>();
		
		for(int i = 0; i < boxes.size(); i++){
			if(boxes.get(i).isEmpty()){
				boxes.remove(i);
				i--;
			}else{
				rectangles.addAll(boxes.get(i).getRectangles());
			}
			
		}
		this.boxes = boxes;
	}
	
	public Solution(ArrayList<Rectangle> rectangles, int boxLength, float allowedOverlapping){
		this.rectangles = new ArrayList<>(rectangles);
		boxes = new ArrayList<Box>();
		
		rectangleLoop:
		for(Rectangle r: rectangles){
			for(Box b: boxes){
				if(b.tryInsertRectangle(r, 0)){
					continue rectangleLoop;
				}
			}
			boxes.add(new Box(boxes.size(), boxLength));
			boxes.get(boxes.size() - 1).tryInsertRectangle(r, 0);
		}
	}
	
	public Solution(ArrayList<Rectangle> rectangles, ArrayList<Box> boxes){
		this.rectangles = rectangles;
		this.boxes = boxes;
	}
	
	@Override
	public double getCost() {
		double c = 0;
		for(Box b: boxes){
			c -= b.getCost(); 
		}
		return c;
	} 
	
	@Override
	public String toString(){
		String result = "cost: " + getCost() + "\n";
		int i;
		for(Box b : boxes){
			if(!b.isEmpty()){
				for(i = 0; i < b.getLength(); i++){
					result += "_";
				}
				result += "\n" + b;
				for(i = 0; i < b.getLength(); i++){
					result += "-";
				}
				result += "\n";
			}
		}
		return result;
	}
	
	public abstract Solution deepCopy();
}
