package models;

import java.util.ArrayList;

import interfaces.FeasibleSolution;

public abstract class BinPackingSolution implements FeasibleSolution {
	
	public ArrayList<Box> boxes;
	public ArrayList<BinPackingRectangle> rectangles;
	
	public BinPackingSolution(ArrayList<Box> boxes){
		rectangles = new ArrayList<BinPackingRectangle>();
		
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
	
	public BinPackingSolution(ArrayList<BinPackingRectangle> rectangles, int boxLength, float allowedOverlapping){
		this.rectangles = rectangles;
		boxes = new ArrayList<Box>();
		Box currentBox = new Box(0, boxLength);
		for(BinPackingRectangle r: rectangles){
			if(!currentBox.tryInsertRectangle(r, allowedOverlapping)){
				boxes.add(currentBox);
				currentBox = new Box(boxes.size(), boxLength);
				currentBox.tryInsertRectangle(r, 0);
			}
		}
		boxes.add(currentBox);
	}
	
	public BinPackingSolution(ArrayList<BinPackingRectangle> rectangles, ArrayList<Box> boxes){
		this.rectangles = rectangles;
		this.boxes = boxes;
	}
	
	protected double boxCost(Box b){
		return Math.pow(b.getOccupiedSurface(), 2) / Math.pow(b.getLength(), 5);
	}
	
	@Override
	public double getCost() {
		double c = 0;
		for(Box b: boxes){
			c -= boxCost(b); 
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
	
	public abstract BinPackingSolution deepCopy();
}
