package mt;

import java.util.TreeSet;

public class ShortStroke extends Base {
	public ShortStroke(int begin,int end,int min){
		super(begin,end,min);
	}

	private static final float VARIANCE = (float).025;
	private static final int PATTERNLENGTH = 5;
	public static final float VOLDOWN = (float).1;
	public static final float DAYSLOWVOLUME = (float)1;

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
			if(!stop){
				if(prices[i + PATTERNLENGTH] > prices[i])
					if(volumeIsDown(i,i + PATTERNLENGTH,volumes,VOLDOWN,DAYSLOWVOLUME)){
						Base ss = new ShortStroke(i,i + PATTERNLENGTH, i);
						list.add(ss);
						da.baseBlocker(i,i + PATTERNLENGTH, baseBlocks);
						return true;
					}
			}
		}
		return false;
	}


}
