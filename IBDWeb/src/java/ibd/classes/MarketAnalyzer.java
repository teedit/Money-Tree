/*
 * This file is similar to Markets_4 except it uses data from a database instead of the internet
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.classes;

/**
 *stuff to do
 * make sure values are the best for 1990 and later
 * run the same main method (copy this) for nasdaq and dow and maybe other indices
 * it might be good to combine indices for a single buy or sell date or maybe 2 out of 3 etc.
 * check actual mutual funds to see how they compare
 * capture todays info such as rallyDays or Ddays etc, last buy date or sell date, buy period or sell period
 * plot over 50 years to compare to graphs
 * figure out how to graph stuff for the web with arrows for each buy and sell day or week
 * output to the website every day, only offer email once a week.  Have a weekly cap email on Friday
 * @author Aaron
 */
import java.io.*;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class MarketAnalyzer {

    /**
     * Gets the value and volume data from the requested markets (by calling
     * dataParser() ), then determines how many distribution startDaysAgo there have
     * been in the last 4 weeks.  A distribution day is defined as a day when
     * value decreased and volume increased, with the threshold set at less
     * than 4.  Boolean is saved in the marketStatus HashMap.
     * @return  true if less than 4 d-startDaysAgo, false otherwise
     * @throws IOException
     * @throws NullPointerException
     * @param var
     */
    public static Output checkMarkets(Variables var) throws IOException, NullPointerException {

	HashMap<String, Data> indexData = MarketAnalyzer.retrieveMarketData(var);

//this part gets the number of days of data between startDays and endDays of yahoo data, not calendar days, excludes holidays and weekends
	Data data3 = indexData.get(var.list);
	Date[] dates = data3.dateData;
//	System.out.println(dates.length);
	int loopDays = 1;//loopDays is number of days of data, has to be 1 cuz "if" statement says "before"
	for (int i = 0; i < dates.length; ++i) {
	    if (var.startDate.before(dates[i])) {
		loopDays += 1;
	    }
	}

	Vector BD = fiveDDayDates(indexData, var, loopDays);//calls followThroghDates method
	ArrayList<Date> fiveDDayDates = (ArrayList<Date>) BD.get(0);//unpacks the arraylist from the vector
	int dDaysToday = (Integer) BD.get(1);

	Vector FT = new Vector();
	FT = (Vector) BD.get(2);
//	ArrayList<Date> buyDates = new ArrayList<Date>();
	ArrayList<Date> followThroughDates = (ArrayList<Date>) FT.get(0);//used to return checkMarkets data
	int rallyDaysToday = (Integer) FT.get(1);//used t0 return rallyDaysToday

	Date[][] buySellPairs = getBuySellPairs(dates, followThroughDates, fiveDDayDates);
	Date[] buyDates = buySellPairs[0];
	Date[] sellDates = buySellPairs[1];
	Date[] sellDatesTrue = buySellPairs[2];

	//this part determines whether in a buy or sell period
	String buyOrSellToday = "BUY";
	if (buyDates[buyDates.length - 1].before(sellDatesTrue[sellDatesTrue.length - 1])) {
	    buyOrSellToday = "SELL";
	}
	//gets last buy and sell date for output
	Date lastBuyDate = buyDates[buyDates.length - 1];
	Date lastSellDate = sellDatesTrue[sellDatesTrue.length - 1];
	float dDaysPerc = (float) dDaysToday / (float) var.dDayParam * 100;

//this adds a dummy day to the end of sellDatesTrue so it is the same size as buyDates, adds a null day to the end if needed
	Date[] sellDatesTrue1 = new Date[1];
	if (buyDates[buyDates.length - 1].after(sellDatesTrue[sellDatesTrue.length - 1])) {
	    sellDatesTrue1 = Arrays.copyOf(sellDatesTrue, sellDatesTrue.length + 1);//this makes it one size bigger
//	    sellDatesTrue[sellDatesTrue.length]=Date.valueOf("0001-01-01");
	} else {
	    sellDatesTrue1 = Arrays.copyOf(sellDatesTrue, sellDatesTrue.length - 1);//this makes it the same size as sellDatesTrue
	}

//	System.out.println(sellDatesTrue.length+" "+sellDatesTrue1.length);

	float[][] histReturns = yearlyReturns(indexData, buyDates, sellDates, var);

	for (int i = 0; i < histReturns.length; i++) {
	    System.out.println("origGain= " + histReturns[i][0] + "  modGain= " + histReturns[i][1]);
	}

	String marketName = "";
	if (var.list.equals("^IXIC")) {
	    marketName = "NASDAQ";
	} else if (var.list.equals("^GSPC")) {
	    marketName = "S&P 500";
	} else if (var.list.equals("^DJI")) {
	    marketName = "Dow Jones";
	} else {
	    marketName = "market not recognized";
	}
	String results = marketName + "\t" + histReturns[3][0] + "\t" + histReturns[3][1];

	try {
	    writeFile(results, var);
	    System.out.println("wrote file\n");
	} catch (IOException e) {
	    System.out.println("can't write to file");
	}


	Output output = new Output(var, rallyDaysToday, buyOrSellToday, lastBuyDate, lastSellDate,
		dDaysPerc, buyDates, sellDatesTrue1, histReturns, dates[0]);

	return output;//emailString;
    }//this ends checkMarkets method

    /**
     *this could possibly be the class constructor so the data is accessible by the class
     * @param var
     * @return yearlyReturns checkMarkets
     */
    public static HashMap<String, Data> retrieveMarketData(Variables var) {

	HashMap<String, Data> dataMap = new HashMap<String, Data>();//obtain database records and store in a hashmap
	Connection connection = MarketDB.getConnection();
	int holiDays = 100;//days to get data before gregStartDate so calculations can be made 35 or 50 days before first day of period
	//increases the number of days to get data to cover holidays, sat, sun
	long startDateMS = var.startDate.getTime();
	long holiDaysMS = (long) holiDays * 24 * 60 * 60 * 1000;
	long diff = startDateMS - holiDaysMS;
//	System.out.println("holiDays=" + var.holiDays);
//	System.out.println("startDaysMS=" + startDateMS + " holiDaysMS=" + holiDaysMS);
//	System.out.println("diff=" + diff);
//	System.out.println("sDbefore=" + var.startDate + ".getTime=" + startDateMS);
	Date startDateNew = (Date) var.startDate.clone();
	startDateNew.setTime(diff);
//	System.out.println("sDafter=" + var.startDate);

//	for (int j = 0; j < var.list.length; j++) {
	try {
	    Data data = MarketDB.getRecord(connection, var.list, startDateNew, var.endDate);
	    dataMap.put(var.list, data);
	} catch (SQLException e) {
	    System.out.println("Can't use getRecord");
	}
//	}//end of for loop
	return dataMap;
    }

    /**
     * The method counts dDays over the given period
     * @param indexData
     * @param var
     * @param loopDays
     * @return
     */
    public static ArrayList<Date> dDayDates(HashMap<String, Data> indexData, Variables var, int loopDays) {

	//this part counts d Days.
	//c is today if it is 0
//	int dDaysToday = 0;

	ArrayList<Date> dDayDates = new ArrayList<Date>();

	//this part extracts the data from the indexData HashMap
	//maybe this could be done in the constructor only once - nope because constructor can only be used if the class is instantiated via object creation
	Data data1 = indexData.get(var.list);
	Date[] dates = data1.dateData;
	float[] pricesOpen = data1.priceDataOpen;
	float[] pricesHigh = data1.priceDataHigh;
	float[] pricesLow = data1.priceDataLow;
	float[] pricesClose = data1.priceDataClose;    //data is an object with priceData field
	long[] volumes = data1.volumeData;
	long[] volumeAVG50 = data1.volumeAVG50;
	float[] priceTrend35 = data1.priceTrendData35;

//	System.out.println(loopDays);

	for (int c = 0; c < loopDays; ++c) {
	    //this part gets regular ddays
	    try {
		if (pricesClose[c] < pricesClose[c + 1]
			&& volumes[c] > volumes[c + 1]) {
		    dDayDates.add(dates[c]);//this makes an array of dDayDates
		} else {

		    //this part gets churning ddays
		    //churning is defined as
		    //price must close in the bottom half of its range
		    //volume must be within 3% of the previous days volume
		    //priceClose must be less than 2% greater than previous day
		    //priceClose must be greater than or equal to previous day
		    //volume must be greater than avg daily
		    //price must be on upswing over  previous 35 days
		    int j = 0;//this is a dummy variable.  The switch statement falls through each case unless a break is reached
		    switch (j) {
			case 0:
			    if (pricesClose[c] < (pricesHigh[c] + pricesLow[c]) / 2 & //price must close in the bottom half of its range
				    volumes[c] >= volumes[c + 1] * (1 - var.churnVolRange) &//volume must be greater than 97% of the previous days volume
				    volumes[c] <= volumes[c + 1] * (1 + var.churnVolRange) &//volume must be less than 103% of the previous days volume
				    pricesClose[c] <= pricesClose[c + 1] * (1 + var.churnPriceRange)) {//priceClose must be less than 102% of the previous day
			    } else {
				break;
			    }
			case 1://priceClose must be greater than or equal to previous day
			    if (var.churnPriceCloseHigherOn == true) {
				if (pricesClose[c] >= pricesClose[c + 1]) {
				} else {
				    break;
				}
			    }
			case 2://volume of churn day must be higher than 50 day average
			    if (var.churnAVG50On == true) {
				if (volumes[c] > volumeAVG50[c]) {
				} else {
				    break;
				}
			    }
			case 3://priceTrend35 means average price gain is at least .1%/day for 35 days before the churn day
			    if (var.churnPriceTrend35On == true) {
				if (priceTrend35[c] > var.churnPriceTrend35) {//this value is ~.001
				} else {
				    break;
				}
			    }
			case 4:
			    dDayDates.add(dates[c]);
//			System.out.println(dates[c] + " vc=" + volumes[c] + " vc+1=" + volumes[c + 1] + " pc=" + pricesClose[c] + " pc+1=" +
//				pricesClose[c + 1] + "churning");
		    }
		}
	    } catch (IndexOutOfBoundsException e) {
		break;
	    }
	}

//	for (int i = 0; i < dDayDates.size(); i++) {
//	    System.out.println("dDayDates="+dDayDates.get(i));
//	}

	return dDayDates;
    }

    /**
     * Determines five D day dates in 20 days from dDayDates
     * @param indexData
     * @param var
     * @param loopDays
     * @return
     */
    public static Vector fiveDDayDates(HashMap<String, Data> indexData, Variables var, int loopDays) {

	Data data1 = indexData.get(var.list);
	Date[] dates = data1.dateData;
	ArrayList<Date> fiveDDayDates = new ArrayList<Date>();
	ArrayList<Date> dDayDates = dDayDates(indexData, var, loopDays);
	Vector FT = getFollowThroughDates(indexData, var, loopDays);//calls followThroghDates method
	ArrayList<Date> followThroughDates = (ArrayList<Date>) FT.get(0);//unpacks the arraylist from the vector
	int dDaysToday = 0;
	for (int c = 0; c < loopDays; ++c) {

	    int dd = 0;
//	    breakout:
	    for (int n = 0; n < 20; ++n) {// identify distribution Days for last 4 weeks
		if (followThroughDates.contains(dates[n + c])) {//this part doesn't add a dd if buy date is day n+c and it kicks it out of the n loop
		    dd = 0;
		    break;
		} else if (dDayDates.contains(dates[n + c])) {
		    ++dd;
		}
	    }

	    if (c == 0) {//c=0 is actually the startDay, not today
		dDaysToday = dd;
	    }
	    if (dd >= var.dDayParam) {
		fiveDDayDates.add(dates[c]);
//		System.out.println("5 d days  " + dates[c] + "  " + dd);
	    }

	}//this ends the c for loop
	Vector BDs = new Vector();
	BDs.add(0, fiveDDayDates);
	BDs.add(1, dDaysToday);
	BDs.add(2, FT);
	return BDs;
    }

    /**
     * finds followthrough dates
     * @param indexData
     * @param var
     * @param loopDays
     * @return
     */
    public static Vector getFollowThroughDates(HashMap<String, Data> indexData, Variables var, int loopDays) {

	//this part extracts the data from the indexData HashMap
	//could possibly be done in the constructor
	Data data1 = indexData.get(var.list);
	Date[] dates = data1.dateData;
	float[] pricesOpen = data1.priceDataOpen;
	float[] pricesHigh = data1.priceDataHigh;
	float[] pricesLow = data1.priceDataLow;
	float[] pricesClose = data1.priceDataClose;    //data is an object with priceData field
	double[] pricesCloseAVG50 = data1.priceAVG50;
	long[] volumes = data1.volumeData;
	long[] volumeAVG50 = data1.volumeAVG50;
	float[] priceTrend35 = data1.priceTrendData35;
	double[] priceCV50 = data1.priceCV50;
	double[] volumeCV50 = data1.volumeCV50;
	double priceCV50AVGpDay = data1.priceCV50AVGpDay;
	double volCV50AVGpDay = data1.volCV50AVGpDay;

//	for (int i=0;i<volumeCV50.length;i++){
//	System.out.println(volCV50AVGpDay + " " + priceCV50AVGpDay);
//	}

	//this part counts market rally days
	int rallyDay = 0;
	int rallyDaysToday = 0;
	boolean rallyCheck = true;
	boolean followThrough = false;
	boolean followThroughToday = false;
	ArrayList<Date> followThroughDates = new ArrayList<Date>();
//	ArrayList<Integer> rallyDays = new ArrayList<Integer>();

	for (int c = 0; c < loopDays; ++c) {//if c=0 it is today
	    int j = 1;//j is the potential pivot day and counts backwards in time to rDays ago
	    rallyDay = 0;
	    float rallyPriceHigh = 0;
	    while (j < var.rDaysMax) {// & priceTrend35[j + c] < pivotTrend35) {//j loops from 1 to rDays, j is the potential pivot day,
		//priceTrend35 makes sure price trend is down -0.1% on average before pivot day for 35 days
		int n = 0;
		switch (n) {
		    case 0:
			if (var.pivotTrend35On == true) {
			    if (priceTrend35[j + c] < var.pivotTrend35) {
			    } else {
				break;
			    }
			}
		    case 1:
			int k = 0;//k is the variable that loops from j=1 to j=10
			rallyCheck = true;
//			System.out.println(pricesHigh.length);
			rallyPriceHigh = pricesHigh[c + j];//sets the rallyPriceHigh to the high of the pivot day
			while (k < j & rallyCheck == true) {
			    if (pricesLow[c + k] > pricesLow[c + j]) {
				if (rallyPriceHigh < pricesHigh[c + k] & k > 0) {//keeps track of the highprice of the rally, used later to make sure followthrough is higher than the high of the rally
				    rallyPriceHigh = pricesHigh[c + k];//this is used to capture the followthrough day high********
				}
				rallyCheck = true;
			    } else {
				rallyCheck = false;
			    }
			    if (k == j - 1 & rallyCheck == true) {//if k doesn't get kicked out of the while loop before k=j-1 then the rallyDay is true
				rallyDay = j;
			    }
//                        System.out.println("k loop c="+c+" j="+j+" k="+k+" rallyCheck="+rallyCheck+
//                                " rallyDay="+rallyDay+"\npricesLow="+pricesLow[c+k]
//                                +" dates="+dates[c+k]+" pricesClose="+pricesClose[c+j]+"\n");
			    ++k;
			}
//                    System.out.println("j loop c="+c+" j="+j+" k="+k+" rallyCheck="+rallyCheck+
//                                " rallyDay="+rallyDay+"\npricesLow="+pricesLow[c+k-1]
//                                +" dates="+dates[c+k-1]+" pricesClose="+pricesClose[c+j]);
		}
		++j;

	    }

//this part determines if a followthrough day is present by the following determinations
//priceClose must be greater than the high price of day before *priceMult
//volumes must be greater than the volume of day before*volumeMult
//followthrough day volumes must be greater than the 50 day avg volume,  this needs to be turned on/off
//the closing price of followthrough day must be the high of the rally,  this needs to be turned on/off
	    if (rallyDay > var.rDaysMin & rallyDay < var.rDaysMax) {
		int g = 0;
		switch (g) {
		    case 0://this determines if priceMult are dependent on volatility
			if (var.priceVolatilityOn == true) {
			    var.priceMult = var.priceMult * priceCV50[c] / priceCV50AVGpDay;//.000509 is the average priceCV50 over the last 50 years
			    if (var.priceMult < var.priceMultBot) {
				var.priceMult = var.priceMultBot;
			    } else if (var.priceMult > var.priceMultTop) {
				var.priceMult = var.priceMultTop;
			    }
			}
		    case 1://this determines if volumeMult are dependent on volatility
			if (var.volVolatilityOn == true) {
			    var.volumeMult = var.volumeMult * volumeCV50[c] / volCV50AVGpDay;//.0037 is the average volCV50 over the last 50 years
			    if (var.volumeMult < var.volMultBot) {
				var.volumeMult = var.volMultBot;
			    } else if (var.volumeMult > var.volMultTop) {
				var.volumeMult = var.volMultTop;
			    }
			}
		    case 2://priceClose must be greater than the high price of day before *priceMult
			//volumes must be greater than the volume of day before*volumeMult
			if (pricesClose[c] > pricesClose[c + 1] * var.priceMult
				& volumes[c] > volumes[c + 1] * var.volumeMult) {
//			    System.out.println(priceMult);
			} else {
			    break;
			}
		    case 3://followthrough day volumes must be greater than the 50 day avg volume
			if (var.rallyVolAVG50On == true) {
			    if (volumes[c] > volumeAVG50[c]) {
			    } else {
				break;
			    }
			}
		    case 4://the closing price of followthrough day must be the high of the rally
			if (var.rallyPriceHighOn == true) {
			    if (rallyPriceHigh < pricesHigh[c]) {
			    } else {
				break;
			    }
			}
		    case 5:
			followThrough = true;
			followThroughDates.add(0, dates[c]);
//			rallyDays.add(rallyDay);
		}
	    }
	    if (c == 0) {//this sets rallyday and followthrough for today
		rallyDaysToday = rallyDay;
//		System.out.println("dddddddddd" + rallyDaysToday);
//		followThroughToday = followThrough;
	    }
	}//end of c for loop

//	for (Date buyDates1 : getFollowThroughDates) {
//	    System.out.println("getFollowThroughDates " + buyDates1);
//	}
	Vector followThroughs = new Vector();
	followThroughs.add(0, followThroughDates);
	followThroughs.add(1, rallyDaysToday);

	return followThroughs;
    }

    /**
     * writes the data to a file
     * @param results
     * @param var
     * @throws IOException
     */
    public static void writeFile(String results, Variables var) throws IOException {

//	DateFormat defaultDate = DateFormat.getDateInstance();
//	String startDateString = defaultDate.format(var.startDate);//startDate is defined in the class field
//	String endDateString = defaultDate.format(var.endDate);//endDate is defined in the class field

	File outputResults = new File("C:/Users/Aaron/Documents/IBDTestRuns/" + var.fileName);
	PrintWriter out = new PrintWriter(
		new BufferedWriter(
		new FileWriter(outputResults, true)));

	out.println(var.startDate + "\t" + var.endDate + "\t" + results + "\t"
		+ var.dDayParam + "\t" + var.churnVolRange + "\t" + var.churnPriceRange + "\t" + var.churnPriceCloseHigherOn + "\t"
		+ var.churnAVG50On + "\t" + var.churnPriceTrend35On + "\t" + var.churnPriceTrend35 + "\t\t"
		+ var.volumeMult + "\t" + var.volMultTop + "\t" + var.volMultBot + "\t" + var.priceMult + "\t" + var.priceMultTop + "\t"
		+ var.priceMultBot + "\t" + var.rDaysMin + "\t" + var.rDaysMax + "\t" + var.pivotTrend35 + "\t"
		+ var.rallyVolAVG50On + "\t" + var.rallyPriceHighOn + "\t" + var.priceVolatilityOn + "\t" + var.volVolatilityOn);
	out.close();
    }

    /**
     *
     * @param indexData
     * @param buyDate
     * @param sellDate
     * @param var
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    //the problem is sellDate is obtained from today rather than the last day data is available
    public static float[][] yearlyReturns(HashMap<String, Data> indexData, Date[] buyDate, Date[] sellDate, Variables var)
	    throws IOException, NullPointerException {

	//converts an array to an ArrayList just cuz I'm lazy and don't want to change the rest to arrays
	ArrayList<Date> buyDates = new ArrayList<Date>();
	ArrayList<Date> sellDates = new ArrayList<Date>();
	for (int y = 0; y < buyDate.length; y++) {
	    buyDates.add(buyDate[y]);
	    sellDates.add(sellDate[y]);
	}

	//this part extracts the data from the indexData HashMap
	Data data1 = indexData.get(var.list);
	Date[] dates = data1.dateData;
	float[] pricesClose = data1.priceDataClose;    //data is an object with priceData field
	long[] volumes = data1.volumeData;
	GregorianCalendar cal = new GregorianCalendar();

	//takes out null day from last periodSellDates and replaces with today's date if sellDates comes from returnFundData servlet
	if (sellDates.get(sellDates.size() - 1) == null) {
	    sellDates.set(sellDates.size() - 1, dates[0]);
	}

	//this part determines loopDays for each period, arrays are only queried for loopDays number of days
	int[] periods = {5, 10, 15, 20, 30, 40, 50};
	float[][] gains = new float[periods.length][2];//store the gains in a 2 D array
	for (int i = 0; i < periods.length; i++) {
	    cal.setTime(var.endDate);
	    cal.add(Calendar.YEAR, -periods[i]);
//	    System.out.println("\n" + cal.getTime());
	    int loopDays = 1;//loopDays is actual number of days
	    try {
		while (dates[loopDays].after(cal.getTime()) & loopDays <= dates.length) {
		    loopDays++;//size of the new array
		}
	    } catch (IndexOutOfBoundsException e) {
		break;
	    }

//	    System.out.println(loopDays);
	    //finds the price for each buy day
	    //finds the price for each sell day
	    ArrayList<Date> periodBuyDates = new ArrayList<Date>();
	    ArrayList<Date> periodSellDates = new ArrayList<Date>();
	    ArrayList<Float> sellPrices = new ArrayList<Float>();
	    ArrayList<Float> buyPrices = new ArrayList<Float>();
//	    ArrayList<Float> buyPriceChange = new ArrayList<Float>();//this calculates the % change from day before
//	    ArrayList<Double> buyVolChange = new ArrayList<Double>();//this calculates the % change from day before
	    //TODO, this loops through the number of days for the 5, 10, 15, 20, 30, 40, 50 year time periods
	    //This for is the problem, it doesn't pick up today as a sell day for fundCheckCharts
	    for (int p = loopDays - 1; p >= 0; --p) {
		if (buyDates.contains(dates[p])) {
		    periodBuyDates.add(dates[p]);
//		    System.out.println("BD "+dates[p]);
		    buyPrices.add(pricesClose[p]);
//		    buyPriceChange.add((pricesClose[p] - pricesClose[p + 1]) / pricesClose[p + 1] * 100);//same as comment below
//		    buyVolChange.add(((double) volumes[p] - (double) volumes[p + 1]) / (double) volumes[p + 1] * 100);//just trust that p and p+1 is right, I checked it 8/15/2010
//		    System.out.println(((double)volumes[p]-(double)volumes[p-1])/(double)volumes[p-1]*100);
		}
		Date joker=dates[p];
//		System.out.println("joker="+joker);
		if (sellDates.contains(joker)) {
		    periodSellDates.add(dates[p]);
		    System.out.println("SD "+dates[p]);
		    sellPrices.add(pricesClose[p]);
		}
	    }

	    //adds a buy date to the beginning of the buyDates array if a sell date is first.  The buydate added is the 1st day of the period
	    if (periodSellDates.get(0).before(periodBuyDates.get(0))) {
		periodBuyDates.add(0, dates[loopDays - 1]);
		buyPrices.add(0, pricesClose[loopDays - 1]);
//		System.out.println(dates[loopDays]);
	    }


	    //this part tests the validity for each time period 5, 10, 15, etc.
	    float origGain = (pricesClose[0] - pricesClose[loopDays - 1]) / pricesClose[loopDays - 1] * 100;
	    float modGain = 0;//modGain is the gain over the whole period
	    float percGain = 0;//percGain is the gain for each buy/sell combo
	    float[] amntInv = new float[buyPrices.size()];
	    for (int j = 0; j < buyPrices.size(); j++) {//percGain is the percent gain for each buy/sell combo
//TODO
		//		it stops here for some reason in the fundCheckCharts
		//the problem is that sellDates doesn't pick up today's date so the buy and sell arrays are not the same size
		percGain = (sellPrices.get(j) - buyPrices.get(j)) / buyPrices.get(j);
		if (j == 0) {//if j=0 then the initial amount invested is the closing price of the first day of the period of interest
		    amntInv[j] = percGain * pricesClose[loopDays - 1] + pricesClose[loopDays - 1];
		} else {//else amount invested is the previous amount invested times percGain plus previous amount invested
		    amntInv[j] = percGain * amntInv[j - 1] + amntInv[j - 1];
		}
		System.out.println("period=" + periods[i] + " " + var.list + " buyDates " + periodBuyDates.get(j) + " "
			+ buyPrices.get(j) + "  sellDates " + periodSellDates.get(j) + " " + sellPrices.get(j)
			+ " percGain " + percGain * 100 + " " + j);
	    }//put in somewhere in here the size of sellDates and buyDates to see if index is out of bounds
	    modGain = (amntInv[amntInv.length - 1] - pricesClose[loopDays - 1]) / pricesClose[loopDays - 1] * 100;

	    gains[i][0] = origGain;
	    gains[i][1] = modGain;
	}//end of for loop

	return gains;
    }//end of method yearlyReturns

    public static Date[][] getBuySellPairs(Date[] dates, ArrayList<Date> followThroughDates, ArrayList<Date> fiveDDayDates) {

	//this gets rid of extraneous sell days.  It adds the first fiveDDayDate after the buyDate to the array
	ArrayList<Date> sellDates = new ArrayList<Date>();
	ArrayList<Date> buyDates = new ArrayList<Date>();
	for (int countFT = followThroughDates.size() - 1; countFT >= 0; countFT--) {
	    boolean gotIt = false;
	    int countFDD = fiveDDayDates.size() - 1;
	    while (countFDD >= 0 & gotIt == false) {//this part finds the sell day
		if (fiveDDayDates.get(countFDD).after(followThroughDates.get(countFT))) {
		    sellDates.add(0, fiveDDayDates.get(countFDD));
		    gotIt = true;
		}
		--countFDD;
	    }
	}

//	    this part takes out duplicate sell dates.  There are duplicate sell days because a sell day is found for each buy day
//	    which can be the same day for many buy days
	int g = 0;
	while (g < sellDates.size() - 1) {
	    if (sellDates.get(g).equals(sellDates.get(g + 1))) {
		sellDates.remove(g);
		g--;//g has to be decremented because remove decreases the size of the arraylist
	    }
	    g++;
	}

	//copies sellDates to get the correct last sellDate rather than today
	ArrayList<Date> sellDatesTrue = (ArrayList<Date>) sellDates.clone();
	//add in an extra sell date for today to count modified gains up to today
	if (sellDates.get(sellDates.size() - 1).before(followThroughDates.get(followThroughDates.size() - 1))) {
	    sellDates.add(dates[0]);//this adds today to the last of the sell dates to make it have the same number as followThroughDates
	}

	//this part takes out extraneous buy dates (two buy dates in a row)
	g = 0;
	while (g < followThroughDates.size() - 1) {
	    try {
		if (followThroughDates.get(g + 1).before(sellDates.get(g))
			|| followThroughDates.get(g + 1).equals(sellDates.get(g))) {
		    followThroughDates.remove(g + 1);
		    g--;//g has to be decremented because remove decreases the size of the arraylist
		}
		g++;
	    } catch (IndexOutOfBoundsException e) {
		break;
	    }
	}

	buyDates = followThroughDates;

	Date[][] buySellPairs = new Date[3][];
	buySellPairs[0] = new Date[buyDates.size()];
	buySellPairs[1] = new Date[sellDates.size()];
	buySellPairs[2] = new Date[sellDatesTrue.size()];
	for (int h = 0; h < buyDates.size(); h++) {
	    buySellPairs[0][h] = buyDates.get(h);
	}
	for (int h = 0; h < sellDates.size(); h++) {
	    buySellPairs[1][h] = sellDates.get(h);
	}
	for (int h = 0; h < sellDatesTrue.size(); h++) {
	    buySellPairs[2][h] = sellDatesTrue.get(h);
	}
	return buySellPairs;
    }//end of method
}//end of class
