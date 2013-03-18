package mt;

import java.util.TreeSet;

public class ThreeWeeksTight extends Base {
	public ThreeWeeksTight(int begin,int end,int min){
		super(begin,end,min);
	}

	private static final float VARIANCE = (float).025;
	private static final int PATTERNLENGTH = 15;
	public static final float VOLDOWN = (float).1;
	public static final float DAYSLOWVOLUME = (float)1;
	public static final int LOOKFORPIVOT = 15;

	public static boolean find(float[] prices,double[] volumes,DataAnalyzer da,int begin, int end, boolean[] baseBlocks, TreeSet<Base> list){
		for (int i = begin;i < end - PATTERNLENGTH;++i){
			float firstPrice = prices[i];
			boolean stop = false;
			for (int j = i; j < i + PATTERNLENGTH;++j){
				if(prices[j] > firstPrice + firstPrice * VARIANCE && 
						prices[j] < firstPrice - firstPrice * VARIANCE){
					stop = true;
					break;
				}
			}
			int pivot;
			if(!stop)// if something like a correct pattern is found
				if(volumeIsDown(i,i + PATTERNLENGTH,volumes,VOLDOWN,DAYSLOWVOLUME))// check for low volume
					if((pivot = getPivot(i,i + PATTERNLENGTH,prices)) >= 0){//add it, it's a base
						Base twt = new ThreeWeeksTight(i,pivot, i);
						list.add(twt);
						da.baseBlocker(i,i + PATTERNLENGTH, baseBlocks);
						return true;
					}
		}
		return false;
	}
	
	private static int getPivot(int start, int finish, float[] prices){
		float max = -1;
		for(int i = start;i < start + PATTERNLENGTH;++i) // find high in pattern
			if(prices[i]> max)
				max = prices[i];
		for(int i = start + PATTERNLENGTH;i < start + PATTERNLENGTH + LOOKFORPIVOT;++i)//  find pivot
			if(i < prices.length && prices[i] >= max + .1)
				return i;
		return -1;
	}
}
