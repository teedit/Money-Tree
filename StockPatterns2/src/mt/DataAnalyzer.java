package mt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TreeSet;



/**
 * The DataAnalyzer module has methods that search for base patterns in stock price and 
 * volume arrays.  Methods that search for Cup with handle, flat base, saucer with handle,
 * double bottom, and ascending base will be built, with lookForFlagPole() being the proto-
 * type search method.  
 * @author Hugh
 *
 */
public class DataAnalyzer {

	private boolean[] baseBlocks;
	private boolean[] bullArray;
	private float[] SandPPrices;
	private LinkedList<String> picks;
	private TreeSet<Base> list;

	public void initialize(Data marketData){
		SandPPrices = marketData.getPriceData();
		picks = new LinkedList<String>();
		list = new TreeSet<Base>(new BaseComparator());
	}

	/**
	 * Central method for DataAnalyzer, all analysis methods are invoked from analyze.  A main loop
	 * loops through all stock entries in the database one at a time, i.e. all analysis is performed
	 *  on a single set of stock data, before moving to the next set.  The size of the data set is 
	 *  determined, (not necessarily constant, since some stocks are new enough to have less than 18 
	 *  months of data available) (18 months is the default length of time to get data for each stock.)
	 * then both the price and volume data are loaded into two separate arrays, and analysis begins
	 * as each analysis method is called. (just lookForFlagPole() for now.)
	 * @throws SQLException
	 */
	public void analyze(Data data){
		baseBlocks = new boolean[data.getPriceData().length];
		System.out.println(data.getName() + ": ");
		dispatcher(data.getPriceData(), data.getVolumeData());
		if(baseCounter(data.getPriceData(), list) <= 3)
			if(isBuyable(list.last(), data.getPriceData()))
				picks.add(data.getName());
		list.clear();
	}

	public boolean checkDDays(Data data){
		float[] values = data.getPriceData();
		double[] volumes = data.getVolumeData();
		int dd = 0;
		for(int c = 0;c < 20;++c)// identify distribution days for last 4 weeks
			if (values[c] < values[c + 1] && volumes[c] > volumes[c + 1])
				++dd;
		System.out.println("S&P Distribution days: " +  dd);
		boolean good = dd <= 4;
		if (!good)
			System.out.println("Too many D-days, no trading today");
		return good;
	}

	public boolean bullOrBear(Data data){
		boolean bull = true;
		int bearTime = 43;
		short bearLoss = (short).8;
		bullArray = new boolean[data.getPriceData().length];
		bullArray[0] = bull;//		initialize the first day of the array appropriately
		for (int i = 1;i < data.getPriceData().length;++i){
			if(bull){//look for bear during bull
				if(i >= bearTime)  //if we're far enough in to look back that far
					if(isBearLoss(data,i, bearTime, bearLoss)){//check for bears!
						bull = false;
						System.out.println("Bear starts on day " + i);
						for(int j = i - bearTime; j < i;++j)//fill in bear values for the previous decline
							bullArray[j] = false;
					}
			}	
			else{//look for bull during bear
				if(data.getPriceData()[i] > data.getPriceData()[i - 1])// is this a potential rally start?
					for(int j = i + 4;j < i + 10;++j){
						if(isFollowThrough(data,j)) 
							if(rallySucceeds(data,i)){
								bull = true;
								System.out.println("Bull starts on day " + i);
							}
					}
			}
			bullArray[i] = bull;    //set each day to bull or bear
		}
		System.out.print("This is a ");
		if (bull) System.out.print("bull market. "); else System.out.print("bear market.  No trading.");
		return bull;//whatever the 'bull' variable was set to last will be the value returned by this
		// method, which will be what the program operates on from here on out.
	}

	private boolean isFollowThrough(Data data, int index){
		return(data.getPriceData()[index] >= data.getPriceData()[index - 1] * 1.02 &&
				data.getVolumeData()[index] >= data.getVolumeData()[index -1]*1.1);
	}

	private boolean rallySucceeds(Data data, int index){
		int testLength = 20; //we'll say the bull pattern has to last at least a month
		for(int i = index;i < index + testLength;++i)
			if(data.getPriceLowsData()[i] < data.getPriceLowsData()[index])
				return false;
		return true;
	}

	private boolean isBearLoss(Data data, int index, int bearTime, float bearLoss){//look back in time for bear conditions
		if(data.getPriceData()[index] <= data.getPriceData()[index - bearTime] * bearLoss){//is there a bear-size drop?
			for(int i = index - bearTime;i < index;++i){
				if(data.getPriceData()[i] > data.getPriceData()[index - bearTime])// if so, is it below the starting point the whole time?
					return false;
			}
			return true;
		}
		return false;
	}

	public void baseBlocker(int start, int stop,boolean[] baseBlocks){
		for(int i = start;i < stop;++i)
			baseBlocks[i] = true;
	}

	private void dispatcher(float[] prices,double[] volumes){
		int begin = 0, end = 0;
		int index = 0;
		while (index < prices.length){//loop till end of the whole set
			if(!baseBlocks[index]){//base here?
				begin = index; // if not, start here
				while(index < prices.length && !baseBlocks[index]) //fast forward until the end of the empty space
					++index;
				end = --index;// and mark it, then call the find methods
				if(CupWithHandle.find(prices,volumes,this,begin,end,baseBlocks,list,bullArray,SandPPrices))
					index = 0;//start over if base is found
				else if(DoubleBottom.find(prices, volumes,this,begin,end,baseBlocks,list,bullArray,SandPPrices))
						index = 0;
				else if(FlagPole.find(prices, volumes, this, begin, end, baseBlocks, list,bullArray,SandPPrices))
						index = 0;
			}
			++index;
		}
	}

	private int baseCounter(float[] prices, TreeSet<Base> list){
		ArrayList<Base> arraylist = new ArrayList<Base>(list);
		int start = 0;
		int count = 0;
		for(int i = prices.length;i > 0;--i)//find start of current bull (if we're here, it's bull)
			if(!bullArray[i]){
				start = ++i;
				break;
			}
		for(int i = 0;i < arraylist.size();++i){// loop through bases
			boolean restart = false;
			if(arraylist.get(i).getEnd() >= start){//see if current base ended after start
				for(int j = start;j < prices.length;++j){ // go all the way out to get to low points now instead of later
					if(i > 0 && prices[j] < arraylist.get(i - 1).getMin()){//look for days that dip below previous base lows
						start = j; //now we should start counting at the point where the new low occurred
						count = 0; // restart the count
						restart = true;
					}
				}
				if(!restart)
					if(i > 0){ // count this base if it shows a 20% rise from the previous base
						if(arraylist.get(i).getMax() >= arraylist.get(i - 1).getMax() * 1.2)
							++count;
					}
					else// or if it's the first base recorded, just count it
						++count;
			}
		}
		return count;
	}

	private boolean isBuyable(Base base, float[] prices){
		if((prices.length -1) - base.getEnd() <= 15)//forget it if it's been more than 3 weeks
			return prices[prices.length - 1] <= prices[base.getEnd()] * 1.05;
		else return false;
	}

	public LinkedList<String> getPicks(){
		return picks;
	}

}

