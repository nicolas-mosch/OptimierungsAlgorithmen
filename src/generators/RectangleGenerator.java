package generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import geometric_models.Rectangle;

public class RectangleGenerator {
	
	Random random;
	
	public RectangleGenerator(){
		random = new Random();
	}
	
	public Rectangle[] generateRandomly(
		int rectangleCount,
		int minSideLength,
		int maxSideLength
	){
		if(rectangleCount <= 0){
			throw new IllegalArgumentException("The amount of rectangles must be positive");
		}
		if(minSideLength < 0){
			throw new IllegalArgumentException("The minimum side-length must be positive");
		}
		if(maxSideLength < minSideLength){
			throw new IllegalArgumentException(
				"The minimum side-length must not be greater than the maximum side-length"
			);
		}
		
		Rectangle[] rectangles = new Rectangle[rectangleCount];
		int diff = maxSideLength - minSideLength;
		
		for(int i = 0; i < rectangleCount; i++){
			rectangles[i] = 
				new Rectangle(
					i + 1,
					random.nextInt(diff) + minSideLength,
					random.nextInt(diff) + minSideLength
				)
			;
		}
		
		return rectangles;
	}
	
	
	// TODO: cannot split minimum-sized rectangles
	public Rectangle[] generateFromSquare(
		int rectangleCount,
		int squareSideLength
	){
		if(rectangleCount <= 0){
			throw new IllegalArgumentException("The amount of rectangles must be positive");
		}
		if(squareSideLength <= 0){
			throw new IllegalArgumentException("The square side-length must be positive");
		}
		
		ArrayList<Rectangle> divisibleRectangles = new ArrayList<Rectangle>();
		ArrayList<Rectangle> nonDivisibleRectangles = new ArrayList<Rectangle>();
		
		divisibleRectangles.add(new Rectangle(1, squareSideLength, squareSideLength));
		
		int i, split;
		Rectangle oldRect, newRect1, newRect2;
		while(divisibleRectangles.size() + nonDivisibleRectangles.size() < rectangleCount && !divisibleRectangles.isEmpty()){
			i = random.nextInt(divisibleRectangles.size());
			oldRect = divisibleRectangles.remove(i);
			
			if(
				random.nextInt(
					oldRect.getShortSide() + oldRect.getLongSide() - 2
				) < (oldRect.getShortSide() - 1)
			){	// split on short side
				split = random.nextInt(oldRect.getShortSide() - 1) + 1;
				
				newRect1 = new Rectangle(oldRect.getId(), split, oldRect.getLongSide());
				
				newRect2 = new Rectangle(
					divisibleRectangles.size() + nonDivisibleRectangles.size() + 1,
					oldRect.getShortSide() - split,
					oldRect.getLongSide()
				);
			}else{ // split on long side
				split = random.nextInt(oldRect.getLongSide() - 1) + 1;
				
				newRect1 = new Rectangle(oldRect.getId(), oldRect.getShortSide(), split);
				
				newRect2 = new Rectangle(
					divisibleRectangles.size() + nonDivisibleRectangles.size() + 1,
					oldRect.getShortSide(),
					oldRect.getLongSide() - split
				);
			}
			
			if(newRect1.getSurface() > 1){
				divisibleRectangles.add(newRect1);
			}else{
				nonDivisibleRectangles.add(newRect1);
			}
			
			
			if(newRect2.getSurface() > 1){
				divisibleRectangles.add(newRect2);
			}else{
				nonDivisibleRectangles.add(newRect2);
			}
		}
		
		
		divisibleRectangles.addAll(nonDivisibleRectangles);
		
		return divisibleRectangles.toArray(new Rectangle[divisibleRectangles.size()]);
	}
}
