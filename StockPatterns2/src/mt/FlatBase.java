package mt;

import java.util.TreeSet;

public class FlatBase extends Base {
	public FlatBase(int begin,int end,int min){
		super(begin,end,min);
	}

	private static final float VARIANCE = (float).12;
	private static final int PATTERNMINLENGTH = 25;
	private static final int PATTERNMAXLENGTH = 40;
	public static final int LOOKFORPIVOT = 15;

	public static boolean find(float[] prices,double[] volumes,DataAnalyzer da,int begin, int end, boolean[] baseBlocks, TreeSet<Base> list){
		for (int i = begin;i < end - PATTERNMINLENGTH;++i){
			boolean stop = false;
			for (int j = i; j < i + PATTERNMINLENGTH;++j){
				if(prices[j] > prices[i] + (prices[i] * VARIANCE) && 
						prices[j] < prices[i] - (prices[i] * VARIANCE)){
					stop = true;
					break;
				}
			}
			int pivot;
			if(!stop)// if something like a correct pattern is found
				if((pivot = getPivot(i,i + PATTERNMINLENGTH,prices,volumes)) >= 0){//add it, it's a base
					Base fb = new FlatBase(i,pivot, i);
					list.add(fb);
					da.baseBlocker(i,pivot, baseBlocks);
					return true;
				}
		}
		return false;
	}

	private static int getPivot(int start, int finish, float[] prices, double[] volumes){
		float max = -1;
		int pivot = -1;
		for(int i = start;i < finish;++i) // find high in pattern
			if(i < prices.length && prices[i]> max)
				max = prices[i];
		for(int i = finish;i < finish + LOOKFORPIVOT;++i)//  find pivot
			if(prices[i] >= max + .1){
				pivot = i;
				if(volumeUp(pivot,volumes))
					return pivot;
			}
		return -1;
	}
	
	private static boolean volumeUp(int index, double[] volumes){
		double ave = getVolAverage(index,50,volumes);
		return volumes[index] >= ave * 1.5;
	}
}
