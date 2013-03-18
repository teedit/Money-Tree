package mt;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;

public class CupWithHandle extends Base {
	private int rightPeak;
	private static final int CUPMINLENGTH = 35;
	private static final int CUPMAXLENGTH = 50;
	private static final float CUPWANDER = (float).4;
	private static final float PEAK2MIN = (float).95;
	private static final float VOLDOWN = (float).5;
	private static final float DAYSLOWVOLUME = (float).5;

	public CupWithHandle(int begin, int end, int min, int rightPeak){
		super(begin,end,min);
		this.rightPeak = rightPeak;		
	}

	public static boolean find(float[] prices, double[] volumes, DataAnalyzer da, int begin, int end, boolean[] baseBlocks, TreeSet<Base> list,boolean[] bullArray, float[] SandP) {
		float minCupDepth = (float).88;
		float maxCupDepth = (float).7;
		if(!bullArray[begin]){
			minCupDepth = (float).7;//adjust for bear times
			maxCupDepth = (float).5;
		}
		int leftPeak,rightPeak,pivot;
		float cupDepth;
		System.out.print("  Cup with handle: ");
		for (int i = begin;i < end - CUPMINLENGTH;++i){//look through the whole range
			CupWithHandle cup = lookForCup(i,prices,minCupDepth,maxCupDepth);
			if(cup != null){
				rightPeak = cup.getRightPeak();
				leftPeak = cup.getBegin();
				cupDepth = (short)(prices[cup.getMin()] + .05);//add 5% to wherever minimum of pattern is, this is the model point for checkParabolaDeviation
				if(checkParabolaDeviation(leftPeak,rightPeak,prices,cupDepth)){
					if(dryUpInLows(prices,volumes,leftPeak,rightPeak)){
						if((pivot = checkHandle(false,prices,rightPeak,volumes,SandP,bullArray,VOLDOWN,DAYSLOWVOLUME)) >= 0){
							cup.setEnd(pivot); 
							list.add(cup);
							da.baseBlocker(leftPeak,pivot,baseBlocks);
							return true;
						}
					}else System.out.println("no proper dry up in lows");	
				}else System.out.println("didn't fit parabola");
			}
		}
		System.out.println("nothing found");
		return false;
	}

	private static CupWithHandle lookForCup(int peakDay,float[] prices,float minCupDepth, float maxCupDepth){
		int peak2 = 0;
		int minDay = peakDay;//initial setting
		if (peakDay + CUPMAXLENGTH > prices.length) //fail if there's no room for pattern
			return null;
		for(int i = peakDay + 1;i < peakDay + CUPMAXLENGTH;++i){
			if(i < peakDay + CUPMINLENGTH && prices[i] >= prices[peakDay]){//abort if price goes up too soon
				return null;
			}
			if(i < prices.length && prices[i] < prices[minDay])
				minDay = i;
		}
		if(prices[minDay] > prices[peakDay]*minCupDepth || prices[minDay] < prices[peakDay]*maxCupDepth)
			return null;  // return failure if cup is too deep or too shallow
		peak2 = minDay;//initial setting
		int i;
		for(i = peakDay + CUPMINLENGTH;i <= peakDay + CUPMAXLENGTH;++i)//loop through possible locations of peak2 to first find uptrend
			if(getTrend(prices,i,5) > 0)
				break;
		if(i > peakDay + CUPMAXLENGTH)//fail if trend change is too late
			return null;
		while(i <= peakDay + CUPMAXLENGTH)//find where the trend changes to down (finding start of handle
			if(getTrend(prices,i,5) < 0){
				peak2 = findHighDay(i - 6,i,prices);//then find specific high day
				break;
			}
		if(peak2 == minDay)//fail if peak2 was never found
			return null;
		if(prices[peak2] <= prices[peakDay] * PEAK2MIN || prices[peak2] >= prices[peakDay])//check if right peak is in correct range
			return null;
		return new CupWithHandle(peakDay,0,minDay,peak2);
	}

	private static boolean checkParabolaDeviation(int start, int end, float[] prices, double depth){
		double maxWander = prices[start] * CUPWANDER;//allowable departure at each point in the line
		for (int j = start;j < end;++j){//loop through each day and look for deviation from straight line
			double target = getParabolaTarget(j,start,end,prices[start],depth);
			if(prices[j] > target + maxWander || prices[j] < target - maxWander)//look for deviations outside allowed limits
				return false;
		}
		return true;
	}

	private static double getParabolaTarget(int cur ,int startDay,int endDay,double startPrice, double drop){
		PrintWriter out = null;
		double offset = (endDay - startDay)/2 + startDay;//centers parabola on y-axis
		double dropValue = startPrice * (1-drop);//fraction of price that cup dips down
		double correction = dropValue/Math.pow((endDay - offset),2);//scales 
		double temp = Math.pow((cur - offset),2) * correction + startPrice * drop;
		try{
			out = new PrintWriter(new FileWriter("C:/Documents and Settings/Hugh/Desktop/mt.csv", true));
		}catch (IOException e)
		{
			System.out.println("error: " + e);
			System.exit(1);
		}	
		out.println(cur + "," + temp);
		out.flush();
		return temp;
	}

	public int getRightPeak() {
		return rightPeak;
	}

	//public void setRightPeak(int rightPeak) {
		//this.rightPeak = rightPeak;
	//}
}
