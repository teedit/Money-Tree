package mt;

import java.util.TreeSet;
import java.util.zip.DataFormatException;

public class DoubleBottom extends Base{
	private int bottom1,bottom2,middlePeak;
	public static final int PATTERNMINLENGTH = 35;
	public static final int PATTERNMAXLENGTH = 45;
	public static final int LEGMINLENGTH = 7;
	public static final int LEGMAXLENGTH = 15;
	public static final float LEGMINDEPTH = (float).9;
	public static final float LEGMAXDEPTH = (float).75;
	public static final float MIDDLEDOWN = (float).95;
	public static final float VOLDOWN = (float).5;
	public static final float DAYSLOWVOLUME = (float).5;
	public DoubleBottom(int start,int finish,int min,int middlePeak, int bottom1, int bottom2){
		super(start,finish,min);	
		this.bottom1 = bottom1;
		this.bottom2 = bottom2;
		this.middlePeak = middlePeak;
	}
	
	public static boolean find(float[] prices, double[] volumes, DataAnalyzer da, int begin, int end, boolean[] baseBlocks, TreeSet<Base> list,boolean[] bullArray, float[] SandP) {
		//deal with handle or not
		System.out.print("  Double bottom: ");
		int[] markers = new int[7];//array of highs and lows
		clear(markers);
		int count = 0;
		int index;
		int trend = 1;//trend of previous day
		int curTrend = 1;//current day's trend, preset to up
		int consecs = 0; //consecutive days with the same trend
		begin = findUp(prices,begin);
		for (index = begin;index < end;++index){//look through the whole range
			curTrend = getTrend(prices,index,5);//gets the current trend
			if(curTrend != 0 && curTrend != trend){//if it's not a neutral trend and it's a new trend

				if(trend > 0)
					markers[count] = findHighDay(index-5,index,prices);//find exactly where the min or max (as approp) is
				else if(trend < 0)
					markers[count] = findLowDay(index-5,index,prices);
				++count;

				consecs = 0;//reset the number of consecutive days
			}
			else //if no new trend, just add another consecutive day
				++consecs;
			if(curTrend != 0)//if no neutral trend (ignored), reset the trend value to the current trend
				trend = curTrend;
			if(markers[3] >= 0){//check to see if we've filled up four pattern markers,
				if(markers[4] < 0 && prices[index] > prices[markers[2]])// if we don't have the last one filled but we're past the midpeak price
					if(prices[index] >= prices[markers[2]] + .1)//sufficiently to be called a pivot
						markers[4] = index;//then mark the pivot
				if(markers[4] >= 0){//check the pattern if markers are filled
					DoubleBottom twoBums = checkDoubleBottom(prices, markers,volumes,SandP,bullArray);
					if(twoBums == null){
						clear(markers);//if it's no good, clear the markers and reset the count
						count = 0;
						index = findUp(prices,index);//reset the count and find the next uptrend
					}
					else{
						list.add(twoBums);
						if(markers[6] < 0)
							da.baseBlocker(markers[0], markers[4], baseBlocks);
						else
							da.baseBlocker(markers[0], markers[6], baseBlocks);
						return true;
					}
				}
			}
		}
		return false;//return false if looping all the way through produced no patterns
	}


	private static int findUp(float[] prices,int index){
		while(getTrend(prices,index,5) < 1)//move the index to the first place where there is an uptrend
			++index;
		return index;
	}

	public static void clear(int[] array){
		for(int i = 0;i < array.length;++i)
			array[i] = -1;
	}

	private static DoubleBottom checkDoubleBottom(float[] prices,int[] markers, double[] volumes, float[] SandP,boolean[] bullArray){
		if(prices[markers[1]] >= prices[markers[0]] * LEGMAXDEPTH && prices[markers[1]] <= prices[markers[0]] * LEGMINDEPTH &&//check depths
				prices[markers[2]] >= prices[markers[0]] * MIDDLEDOWN && prices[markers[2]] < prices[markers[0]] &&
				prices[markers[3]] >= prices[markers[0]] * LEGMAXDEPTH && prices[markers[3]] <= prices[markers[1]])
			if(markers[1] - markers[0] <= LEGMAXLENGTH && markers[1] - markers[0] >= LEGMINLENGTH &&  // check lengths
					markers[2] - markers[1] <= LEGMAXLENGTH && markers[2] - markers[1] >= LEGMINLENGTH &&		
					markers[3] - markers[2] <= LEGMAXLENGTH && markers[3] - markers[2] >= LEGMINLENGTH)
				if(markers[4] - markers[0] <= PATTERNMAXLENGTH && markers[4] - markers[0] >= PATTERNMINLENGTH ) //check entire length
					if(prices[markers[0]] > prices[markers[2]] && prices[markers[0]] > prices[markers[4]])//points in proper configuration?
						if(prices[markers[3]] < prices[markers[1]]){// 2nd bottom must be lower than first
							if(prices[markers[4]] > prices[markers[2]])//no handle, it's gone straight up
								return new DoubleBottom(markers[0],markers[4],markers[3],markers[2],markers[1],markers[3]);
							int pivot = checkHandle(false,prices,markers[4],volumes,SandP,bullArray,VOLDOWN,DAYSLOWVOLUME);
							if (pivot >= 0)
								return new DoubleBottom(markers[0],markers[6],markers[3],markers[2],markers[1],markers[3]);
						}
		return null;
	}

}
