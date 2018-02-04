package algorithms;

import java.util.HashSet;
import java.util.LinkedList;

import interfaces.FeasibleSolution;
import interfaces.Feature;
import interfaces.Neighborhood;

public class TabooSearch {
	public final long MAX_RUN_TIME = 20000; // in ms
	public final long MAX_TIME_WITHOUT_IMPROVEMENT = 2000;
	public final int K = 2;
	public static long neighborhoodTime = 0;
	
	public FeasibleSolution solve(FeasibleSolution currentSolution, Neighborhood n){
		
		FeasibleSolution bestSolution = currentSolution;
		
		HashSet<Feature> featureSet = new HashSet<Feature>();
		HashSet<Feature> oldFeatures, changedFeatures;
		LinkedList<HashSet<Feature>> tabooList = new LinkedList<HashSet<Feature>>();
		
		
		long startTime = System.currentTimeMillis();
		long start;
		int c = 0;
		long lastImprovementFound = System.currentTimeMillis(); 
		
		while(System.currentTimeMillis() - startTime < MAX_RUN_TIME){
			if(c > K && !tabooList.isEmpty()){
				featureSet.removeAll(tabooList.pop());
			}
			c++;
			/*
			System.out.println("_______TABOO " + c + "_______");
			System.out.println("TabooList: " + tabooList.size());
			System.out.println("featureSet: " + featureSet.size());
			System.out.println("Best: " + bestSolution.getCost());
			*/
			
			oldFeatures = currentSolution.getFeatures();
			
			start = System.currentTimeMillis();
			currentSolution = n.getBestNeighbor(currentSolution, featureSet);
			neighborhoodTime += System.currentTimeMillis() - start;
			changedFeatures = currentSolution.getFeatures();
			
			changedFeatures.removeAll(oldFeatures);
			
			if(currentSolution.getCost() < bestSolution.getCost())
			{
				bestSolution = currentSolution;
				lastImprovementFound = System.currentTimeMillis();
			}
			else if(System.currentTimeMillis() - lastImprovementFound > MAX_TIME_WITHOUT_IMPROVEMENT)
			{
				return bestSolution;
			}
			
			featureSet.addAll(changedFeatures);
			tabooList.add(changedFeatures);
		}
		
		return bestSolution;
	}
}
