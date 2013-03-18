package mt;

import java.util.TreeSet;

public class AscendingBase extends Base {

	private static final int PATTERNMINLENGTH = 50;
	private static final int PATTERNMAXLENGTH = 60;

	public AscendingBase(int begin, int end, int min){
		super(begin, end, min);
	}

	public static boolean find(float[] prices, double[] volumes, DataAnalyzer da, int begin, int end, boolean[] baseBlocks, TreeSet<Base> list,boolean[] bullArray, float[] SandP) {
		final float MINDIP = (float).92;
		final float MAXDIP = (float).8;

		int dips,min,max;
		int trend = 0;
		for(int i = begin;i < end - PATTERNMINLENGTH;++i){
			if(marketIsWeak(begin,SandP)){
				dips = 0;
				int j,day1;
				day1 = i;
				for(j = i;j < i + PATTERNMAXLENGTH;++j){
					if (j < end)   // make sure we don't go past the end of this section
						trend = getTrend(prices,j,10);
					if(trend < 0)//ensure broad trend never turns down
						break;
					if (getTrend(prices,j,3) < 0){//count narrow downtrends 
						min = findLowDay(j - 4,j + 4,prices);
						max = findHighDay(j-6,j,prices);
						if(min < max * MAXDIP)//if it dips too much, break out of this analysis loop
							break;
						if(min <= max * MINDIP){// if it dips enough then count it
							if(++dips == 3)
								break;
							while (j < end && getTrend(prices,j,3) < 0)// fast forward until we're uptrending again
								++j;
						}
					}
				}
				if(dips == 3)
					for(int k = j;k < j + 10;++k){//wait 10 days for market to strengthen
						if(k < end && !marketIsWeak(begin, SandP)){
							Base ab = new AscendingBase(day1,k,day1);
							list.add(ab);
							da.baseBlocker(day1,k,baseBlocks);
							return true;
						}
					}
			}	
		}
		return false;
	}

	public static boolean marketIsWeak(int begin,  float[] SandP){
		return getTrend(SandP,begin,20) < 0;// determine trend with 40 day window
		
	}
}
