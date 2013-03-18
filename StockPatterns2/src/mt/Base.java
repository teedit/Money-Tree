package mt;

public class Base {
	private int min;
	private int max;
	private int begin;
	private int end;
	private boolean buyable;
	
	public Base(int begin, int end, int min){
		begin = this.begin;
		end = this.end;
		min = this.min;
	}
	/**
	 * The basic strategy for pattern searching involves setting up a target pattern, or
	 * "ideal" pattern, looking for the rudiments of that pattern, then examining each data
	 * point (or day) in the candidate pattern for deviation from the ideal value.  If a 
	 * given day's data varies more than the allowable percentage, the pattern is abandoned
	 * and the search begins anew.  If the variation is within the tolerated limits for the
	 * entire pattern, the data set is further tested for proper price patterns in the weeks 
	 * before the pattern occurred, appropriate volume trends, and whether the pattern was
	 * detected in time to make buying reasonable.  Essentially, each point in a given data
	 * set is tested individually.  In other words, lookForFlagPole() tests if a pattern 
	 * begins on Day 1, then tests if one begins on Day 2, etc until a day is reached that 
	 * will no longer allow time for the pattern to properly develop.  The initial for loop
	 * loops through days between which the entire pattern can fit, thus it starts on the 
	 * timeSincePreviousHigh value (this allows searching time before the start of the pattern
	 * to make sure there are not periods of higher value in the recent past.) and goes to the 
	 * end of the array, minus a reasonable length for the pattern to occupy.  checkUpSlope()
	 * looks for rises that correspond in magnitude to the parameters chosen, finds the peak,
	 * then assures there are no deviations as the slope progresses, then returns the day 
	 * that the peak occurred if a passing pattern is detected (see below).  If it is, the
	 * previous price data for a determined period is scanned for values higher than the value 
	 * of the starting price of the pattern.  If any are found, the pattern fails.  If not, 
	 * checkDownSlope() is called to search for the presence of an appropriate "handle", in a 
	 * manner similar to checkUpSlope().  If a handle is present, the volume data during the 
	 * handle is tested for a "dry up" phase, according to set parameters.  If low volume is
	 * detected during the handle, checkFinish() watches for a recovery to a pivot point, and 
	 * reports with an appropriate message.       
	 * @param prices  the array of price data
	 * @param volumes  the array of volume data
	 * @return  true if a flag pole pattern is detected
	 */
	
	public static void find(float[] prices, double[] volumes){
		System.out.println("Failed to hide superclass find method");
		System.exit(1);
	}
	
	/**
	 * finds the value for the average volume for the last days number of days
	 * @param index   the current day
	 * @param days   number of days for which to get average volume
	 * @param array   the array of volume values
	 * @return   the volume average
	 */
	protected static double getAverage(int index, int days, float[] array){
		double sum = 0;
		if (index < days)
			throw new IllegalArgumentException();
		for(int i = index - days;i < index; ++i)
			sum += array[i];
		return sum/days;
	}

	protected static double getVolAverage(int index, int days, double[] array){
		double sum = 0;
		if (index < days)
			throw new IllegalArgumentException();
		for(int i = index - days;i < index; ++i)
			sum += array[i];
		return sum/days;
	}
	
	/**
	 *   Detects a gain of a given magnitude over a given time period and checks the deviation
	 *   from a straight line slope. checkUpSlope() is given a single day, 'start', for which 
	 *   to check for the presence of the specified gain.  In other words, looping through the 
	 *   entire data set is not performed in this method, but in the root analysis method.  Since
	 *   the length of time that an increase takes to occur can vary within a given range, 
	 *   ('lengthMin' - 'lengthMax') this method loops through those lengths, searching for the 
	 *   specified gain and always starting with the same day.  If a proper gain is found, the 
	 *   method goes on to find the peakDay by calling findHighDay(), then checks to see that the 
	 *   slope is not too steep, i.e. that the increase does not occur so rapidly so as to exclude
	 *   it from the specified parameters.  Finally this method searches for deviations
	 *   from a straight line by calling checkRampDeviation().  The 'wanderValue' value dictates 
	 *   how much deviation is allowed and is used as a percentage of the starting point of the 
	 *   increase.  If the deviation is acceptable, the peakDay value is returned, otherwise
	 *   -1 is returned.  
	 *   
	 * @param start  the day to use to check for a gain pattern
	 * @param lengthMin  the minimum length in which to check for a gain
	 * @param lengthMax  the max length in which to check for a gain
	 * @param prices  the array of price data to analyze
	 * @param gainMin  the minimum expected gain for an increase, expressed as a multiplier 
	 * @param wanderValue  percentage of deviation allowed for each day's price value
	 * @param gainMax  the maximum gain allowed
	 * @return  the peakDay value acquired in this method
	 */
	protected static int checkUpSlope(int start, int lengthMin, int lengthMax,
			float prices[], double gainMin, double wanderValue, double gainMax){
		int peakDay;
		if(start < 0)
			start = 0;
		boolean goOn = false;
		for(int j = start + lengthMin;j <= start + lengthMax;++j)//loop through correct pole length boundaries
			if(j < prices.length && prices[j] >= prices[start] * gainMin){//is a potential flagpole present?
				goOn = true;
				break;
			}
		if(!goOn)
			return -1;
		peakDay = findHighDay(start+lengthMin,start+lengthMax,prices);//get the high point
		if(prices[peakDay] > prices[start] * gainMax) //is the slope too steep?
			return -1;
		if(checkRampDeviation(peakDay,start,prices,wanderValue,1))
			return peakDay;
		return -1;	
	}
	
	/**
	 *  this method operates in a manner completely analogous to checkDownSlope, and is useful
	 *  for checking handles and other periods of correction.  
	 * @param peakDay  the day to use to check for the beginning of a correction pattern
	 * @param lengthMin  the minimum length in which to check for a correction
	 * @param lengthMax  the max length in which to check for a correction
	 * @param prices  the array of price data to analyze
	 * @param wanderValue  percentage of deviation allowed for each day's price value
	 * @param lossMin  the minimum expected loss for a down period, expressed as a multiplier 
	 * @param lossMax  the maximum expected loss
	 * @return  minDay, the lowest point in the down pattern or -1 if no legitimate down pattern
	 * is found
	 */
	protected static int checkDownSlope(int start,int lengthMin, int lengthMax, float[] prices,
			double wanderValue, double lossMin, double lossMax){
		if(start < 0)
			start = 0;
		boolean goOn = false;

		for(int j = start + lengthMin;j <= start + lengthMax;++j){
			if(j < prices.length && prices[j] <= prices[start] * lossMin){//is there a potential "handle"?
				goOn = true;
				break;
			}
		}
		if(goOn){
			int minDay = findLowDay(start+lengthMin,start+lengthMax,prices);
			if(prices[minDay] >= prices[start] * lossMax)//too steep?
				if(checkRampDeviation(minDay,start,prices,wanderValue,-1))
					return minDay;
		}
		return -1;
	}
	
	/**
	 * determines if a given fraction (daysLowVolume) of days during the given period 
	 * (start - finish) have volume down by a given percentage (volDown)
	 * @param start  the first day to assess
	 * @param volumes  the array of volume data to assess
	 * @param finish  the last day to assess
	 * @param volDown  the fraction that volume should be down
	 * @param daysLowVolume  the fraction of days that should be down
	 * @return  true if volume is sufficiently down
	 */
	protected static boolean volumeIsDown(int start, int finish, double[] volumes, float volDown,float daysLowVolume){
		final float VOLDOWN = (float).5;
		final float DAYSLOWVOLUME = (float).5;
		final int DAYSVOLUME = 8;//days to use to figure average volume
		double volAve = getVolAverage(start - DAYSVOLUME,DAYSVOLUME,volumes);
		int totalDown = 0;
		for(int j = start;j <= finish;++j){
			if(volumes[j] <= volAve * VOLDOWN)
				totalDown++;
		}
		int handleLength = finish - start;
		int volDownThreshold = (int)(handleLength * DAYSLOWVOLUME);
		return totalDown >= volDownThreshold;
	}
	
	/**
	 * Checks for a recovery to the required point within the required time frame.  The first
	 * loop repeats until the pattern has taken too long to recover to the pivot point.  Inside
	 * the loop, after the a check to make sure the end of the data has not been reached, the 
	 * current price is checked to see if the pivot point is reached.  If it is, a check is 
	 * made to make sure that no more than 2 days have elapsed since the pivot point.  If not,
	 * the pattern is added to a baseCount list to track how many bases have been recorded, and 
	 * if this isn't the 4th base, a buy message is printed.  If more than two days have passed, 
	 * a 'missed buy opportunity' message is printed.  If the pivot point isn't reached, 
	 * 'waiting for pivot' is printed, the count is incremented, and the loop repeats.  If the 
	 * end of the data is reached without the pivot point being reached, 'pattern may be 
	 * developing' message is printed.  If the maximum time allowed for the pattern to recover
	 * is exceeded, a 'pattern failed to develop' is printed.
	 * @param  waitForBuyPoint  days allowed for pattern to recover
	 * @param minDay  low point from which pattern must recover
	 * @param prices  array of price data
	 * @param peakDay  point to which pattern must recover 
	 * @return  true if a proper finish was achieved
	 */
	protected static int checkFinish(int waitForBuyPoint,int minDay,float[] prices,int peakDay){
		int count = 0;
		while(count < waitForBuyPoint){//allow time to get to buy point
			if((minDay + count) >= prices.length){
				System.out.println("    pattern may be developing");
				return -1;
			}
			if(prices[minDay + count] >= prices[peakDay]+0.1) 
				return (minDay + count);
			++count;
			System.out.println("    Waiting for pivot...");
		}
		System.out.println("    pattern failed to recover after handle at day " + minDay);
		return -1;
	}
	
	/**
	 * Determines whether any points higher than the current starting point exist in the 
	 * specified time period in the past
	 * @param start  the current starting day
	 * @param timeSincePreviousHigh  days to search in the past
	 * @param prices  array of price values
	 * @return  true if no high points are found
	 */
	protected static boolean noHighPointsInPast(int marker, int lookBack,float[] prices){
		int start = marker - lookBack;
		if(start < 0)
			start = 0;
		for(int j = start;j < marker;++j)//look for higher points in past, this invalidates the base
			if(prices[j] >= prices[marker])
				return false;
		return true;
	}
	
	/**
	 * Finds the highest day in the given time period
	 * @param begin  the day to start on
	 * @param end  the day to end on
	 * @param prices  the array of price data
	 * @return the highest day, (not the price on that day)
	 */
	protected static int findHighDay(int begin,int end,float[] prices){
		int peakDay = begin;//initial setting for the highest point
		for(int j = begin; j <= end;++j)//find the high point 
			if(j < prices.length && prices[j] > prices[peakDay])
				peakDay = j;
		return peakDay;
	}

	/**
	 * Finds the lowest day in the given time period
	 * @param begin  the day to start on
	 * @param end  the day to end on
	 * @param prices  the array of price data
	 * @return  the lowest day, (not the price on that day)
	 */
	protected static int findLowDay(int begin,int end,float[] prices){
		int minDay = begin;//initial setting for the highest point
		for(int j = begin; j <= end;++j)//find the high point 
			if(j < prices.length && prices[j] < prices[minDay])
				minDay = j;
		return minDay;
	}
	
	/**
	 * Checks for deviation from a linear pattern, either up or down.  maxWander is calculated
	 * by multiplying the wanderValue by the starting point in the line.  Then the difference in 
	 * price for the current pattern is calculated.  The loop samples each day in the pattern,
	 * calculating a progressionFraction and a target.  progressionFraction is the fraction of 
	 * the pattern that has been traversed so far, the target is the point in the line at which
	 * the pattern should ideally be.  If the current price exceeds the target value by more than
	 * the maxWander, the loop is exited and false is returned, otherwise, if the loop is 'safely'
	 * traversed, true is returned.
	 * @param end  the last day to evaluate
	 * @param start  the first day
	 * @param prices  the array of price data
	 * @param wanderValue  the fraction from the starting point that the prices are allowed to deviate
	 * each day
	 * @param upOrDown  parameter passed that indicates whether a pattern should be evaluated as 
	 * going up or down; used to calculate the 'target' value.
	 * @return  true if no deviation is detected
	 */
	protected static boolean checkRampDeviation(int end, int start, float[] prices, double wanderValue, int upOrDown){
		int riseLength = end - start;
		double maxWander = prices[start] * wanderValue;//allowable departure at each point in the line
		double priceJump = Math.abs(prices[end]-prices[start]);
		for (int j = start;j < end;++j){//loop through each day and look for deviation from straight line
			double progressionFraction = ((j-start)/(double)riseLength);//calculate how far through we are
			double target = prices[start] + (upOrDown * priceJump * progressionFraction);//find value for straight line
			if(prices[j] > target + maxWander || prices[j] < target - maxWander)//look for deviations outside allowed limits
				return false;
		}
		return true;
	}
	
	protected static boolean dryUpInLows(float[] prices,double[] volumes,int start, int end){
		float volDown = (float).5;//how much should a day's volume be down to count as "down"
		float fractionDown = (float).4;//how many days should be down to count as a dry up
		int length = end - start;
		float slopeTime = (float)0.2;//how much of the cup pattern should constitute the slope, so we can subtract it out and just look at the low portion of the pattern
		int sum = 0,tally = 0;
		int slopeDays = (int)(length * slopeTime);// days in the slope
		for(int i = start + slopeDays;i < end - slopeDays;++i)
			sum += prices[i];
		float ave = sum/(length - 2*slopeDays);
		double volAve = getVolAverage(start,50,volumes);
		for(int i = start + slopeDays;i < end - slopeDays;++i){//tally up low volume days that are low priced
			if(prices[i] <= ave)
				if(volumes[i] <= (volAve * volDown))
					++tally;
		}
		return (tally >= (length - 2*slopeDays)*fractionDown);
	}
	
	protected static boolean abovePriceLine(int marker,int lookBack,float[] prices){
		if (lookBack > marker){
			lookBack = marker;
			System.out.println("Unable to look back the full " + lookBack + " days");
		}
		double ave = getAverage(marker,lookBack,prices);
		System.out.println(prices[marker] + " " + ave);
		return (prices[marker] > ave);
	}
	
	public static boolean priceStrength(int begin,int end,float[] SandP,float[] prices){
		int correction = SandP.length - prices.length;//this is used to ensure that we're using the 
		// same day in both arrays; necessary if arrays are of different lengths
		float ratio = prices[begin]/SandP[begin + correction];
		for(int i = begin + 1;i < end;++i){
			float newRatio = prices[i]/SandP[i + correction];
			if(ratio > newRatio)
				return false;
			ratio = newRatio;
		}
		return true;
	}

	public static int checkHandle(boolean flagHandle, float[] prices,int highDay, double[] volumes, float[] SandP,boolean[] bullArray, float volDown, float daysLowVolume){
		final int WAITFORPIVOT = 10;
		int index,lowDay,pivot;
		int correction = bullArray.length - prices.length;
		int handleMinLength = 5,handleMaxLength = 25;
		float handleMaxDepth = (float).85;
		float handleMinDepth = (float).9;
		if(flagHandle){
			handleMinLength = 15;//leave max length same
			handleMinDepth = (float).9;
			handleMaxDepth = (float).8;
		}
		if(!bullArray[highDay + correction]){
			handleMaxDepth = (float).7;
			handleMinDepth = (float).8;
		}
		if(getTrend(prices,highDay,5) >= 0)//method should be called only in down trend
			System.err.println("Trend is not as expected");System.exit(1);
		index = highDay;
		while(getTrend(prices,index,5) <= 0){//find the bottom of the handle
			++index;
			if(index == prices.length)
				return -1;
		}
		lowDay = findLowDay(index-6,index,prices);//mark the bottom (current index is probably not it
		if((lowDay - highDay) >= handleMinLength && (lowDay - highDay) <= handleMaxLength){
			if(prices[lowDay]/prices[highDay] >= handleMaxDepth && prices[lowDay]/prices[highDay] <= handleMinDepth){
				if(volumeIsDown(highDay,lowDay,volumes, volDown, daysLowVolume)){
					if(abovePriceLine(lowDay,200,prices)){
						if((pivot = checkFinish(WAITFORPIVOT,lowDay,prices,highDay)) >= 0){
							if(priceStrength(lowDay,pivot,SandP,prices))
								return pivot;
							else System.out.println("not ahead of market in handle recovery");
						}else System.out.println("handle did not finish forming");
					}else System.out.println("not above price line average");
				}else System.out.println("no volume dry up in handle");
			}else System.out.println("no handle developed");
		}else System.out.println("no handle developed");
		return -1;

	}
	
	public static int getTrend(float[] data, int index, int halfRange){
		int trend = 0;
		for(int i = index - halfRange;i < index + halfRange;++i)// look at range 5 before and 5 after current day
			if(i >= 0 && i < data.length)//check to make sure we're not looking out the array bounds
				if (i < data.length && data[i] < data[i + 1])
					++trend;
				else if (i < data.length && data[i] > data[i + 1])
					--trend;
		if(trend > 0)
			return 1;
		if(trend < 0)
			return -1;
		return 0;
	}
	
	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public boolean getBuyable() {
		return buyable;
	}

	public void setBuyable(boolean buyable) {
		this.buyable = buyable;
	}
}
