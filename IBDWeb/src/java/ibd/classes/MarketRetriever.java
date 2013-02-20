/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.classes;

/**
 *This class gets data from yahoo.com for the markets listed in main and stores the data in a database,
 * Run this class once a day before MarketAnalyzer
 * @author Aaron
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Aaron
 */
public class MarketRetriever {

    /**
     * indices to obtain data for
     */
//    public static String[] list = {"^MID","^SML"};
    /**
     * number of days back from today to obtain data
     */
//    public static int numDays = 55000;
    /**
     *
     * @param var
     * @throws IOException
     */
    public static void main(Variables var) throws IOException{

	Connection connection = MarketDB.getConnection();
	Data data = null;
//	for (int i = 0; i < list.length; ++i) {
	int numDays = getNumDays(var);
	boolean loop;//if true this decreases the numDays by 1 and sends back through loop
	do {
	    loop=false;
	    try {//this is the try for the getRecord method
		try {
		    data = getData(var.list, numDays);
		} catch (URISyntaxException e) {
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		    break;
		} catch (Exception e) {
		}

		Date[] dates = data.dateData;
		float[] opens = data.priceDataOpen;
		float[] highs = data.priceDataHigh;
		float[] lows = data.priceDataLow;
		float[] closes = data.priceDataClose;
		long[] volumes = data.volumeData;

		MarketDB.addRecord(connection, var.list, dates, opens, highs, lows, closes, volumes);
	    } catch (SQLException e) {
		System.err.println("Cannot add record for numDays=" + numDays);
		numDays -= 1;
		loop=true;
	    }
	} while (loop==true&numDays>0);
//	}end of for loop
    }

    /**
     * Overseeing method for retrieving stock-specific price and volume data,
     * getYahooStockURL() is first called to assemble the appropriate URL, then
     * dataParser compiles the stock and volume data into a Data object
     * @param symbol  the stock for which data is to be gathered
     * @param daysAgo  how many startDaysAgo for which to gather data
     * @return a Data object
     * @throws URISyntaxException
     * @throws MalformedURLException
     * @throws IOException
     */
    public static Data getData(String symbol, int daysAgo) throws URISyntaxException, MalformedURLException, IOException {//these three are what dataParser throw as well
	Data data = null;
	int flag = 0;
	while (flag < 20) {
	    try {
		String URL = getYahooURL(symbol, daysAgo);//gets URL for S&P500,dow,and nasdaq data
		//System.out.println(URL);
		data = dataParser(URL);// extract price and volume data for URL, # of yahoo days
		flag = 20;  //this kicks the obtain data out of the loop since flag=20
	    } catch (NumberFormatException e) {
		++flag;
		System.err.println("Proper connection getYahooMarketURL failed, trying again...");
	    } catch (Exception e) {//catch any other exception
		System.err.println("Error: " + e);
		break;
//		System.exit(1);

	    }
	}
	return data;
    }

    /**
     * reads data from the specified URL, parses price and volume data
     * and stores them in the Data class.
     * @param url  the URL to read from
     * @param startDaysAgo the number of previous startDaysAgo from which data should be
     * gathered
     * @return an instance of the Data class, containing an array
     * of prices and an array of corresponding volumes
     * @throws MalformedURLException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static Data dataParser(String url) throws MalformedURLException, IOException, URISyntaxException {
	ArrayList<Float> priceListClose = new ArrayList<Float>();
	ArrayList<Float> priceListHigh = new ArrayList<Float>();    //this is added
	ArrayList<Float> priceListLow = new ArrayList<Float>(); //this is added
	ArrayList<Float> priceListOpen = new ArrayList<Float>(); //this is added
	ArrayList<Long> volumeList = new ArrayList<Long>();
	ArrayList<Date> dateList = new ArrayList<Date>();    //this is added
	URL ur = new URL(url);
	HttpURLConnection HUC = (HttpURLConnection) ur.openConnection();
	BufferedReader in = new BufferedReader(new InputStreamReader(HUC.getInputStream()));
	String line;
	//int lineCount = 0;  //lineCount is one line for each day
	in.readLine();//reads the first line, it's just headers
	//the entries on the line are date, open, high, low, close, volume, adj close, misc
	//gets a line of input if available, beginning of main loop
	while ((line = in.readLine()) != null) {// && lineCount <= startDaysAgo) {
	    int len = line.length();    //this is the number of character in the line
	    int lineIndex = 0;  //this is the character index in the line

	    String dateStr = "";
	    // add characters to a string until the next ',' is encountered
	    while (lineIndex < len && line.charAt(lineIndex) != ',') {
		dateStr += line.charAt(lineIndex);
		++lineIndex;
	    }

	    ++lineIndex;//get off the current comma
	    String priceOpenStr = "";
	    // add characters to a string until the next ',' is encountered
	    while (lineIndex < len && line.charAt(lineIndex) != ',') {
		priceOpenStr += line.charAt(lineIndex);
		++lineIndex;
	    }

	    ++lineIndex;//get off the current comma
	    String priceHighStr = "";
	    // add characters to a string until the next ',' is encountered
	    while (lineIndex < len && line.charAt(lineIndex) != ',') {
		priceHighStr += line.charAt(lineIndex);
		++lineIndex;
	    }

	    ++lineIndex;//get off the current comma
	    String priceLowStr = "";
	    // add characters to a string until the next ',' is encountered
	    while (lineIndex < len && line.charAt(lineIndex) != ',') {
		priceLowStr += line.charAt(lineIndex);
		++lineIndex;
	    }

	    ++lineIndex;//get off the current comma
	    String priceCloseStr = "";
	    // add characters to a string until the next ',' is encountered
	    while (lineIndex < len && line.charAt(lineIndex) != ',') {
		priceCloseStr += line.charAt(lineIndex);
		++lineIndex;
	    }

	    ++lineIndex;// get off the current comma
	    String volumeStr = "";
	    while (lineIndex < len && line.charAt(lineIndex) != ',') {
		volumeStr += line.charAt(lineIndex);//same for volumes(they are immediately after prices)
		++lineIndex;
	    }

	    //add current values to arrayLists
	    dateList.add(Date.valueOf(dateStr));
	    priceListOpen.add(Float.parseFloat(priceOpenStr));//these have to be parsed because they come in as strings
	    priceListHigh.add(Float.parseFloat(priceHighStr));
	    priceListLow.add(Float.parseFloat(priceLowStr));
	    priceListClose.add(Float.parseFloat(priceCloseStr));
	    volumeList.add(Long.parseLong(volumeStr));
	    //++lineCount;
	}
	////stores the data arrays in a Data object and returns it
	return new Data(dateList, priceListOpen, priceListHigh, priceListLow, priceListClose, volumeList);
    }

    /**
     * *Builds url for a user supplied # of startDaysAgo
     * @param symbol    market or stock for which data is desired
     * @param daysAgo  # of startDaysAgo to retrieve data
     * @return  construed URL
     */
    public static String getYahooURL(String symbol, int daysAgo) {
	GregorianCalendar calendarStart = new GregorianCalendar();
	calendarStart.add(Calendar.DAY_OF_MONTH, -daysAgo);//this subtracts the number of startDaysAgo from todays date.  The add command changes the calendar object
	int d, e, f, a, b, c;
	a = calendarStart.get(Calendar.MONTH);//this gets todays month
	b = calendarStart.get(Calendar.DAY_OF_MONTH);//this gets todays day of month
	c = calendarStart.get(Calendar.YEAR);//this gets todays year

	GregorianCalendar calendarEnd = new GregorianCalendar();
	//calendarEnd.add(Calendar.DAY_OF_MONTH, -endDaysAgo);
	d = calendarEnd.get(Calendar.MONTH);//this gets the beginning dates month
	e = calendarEnd.get(Calendar.DAY_OF_MONTH);//this gets beginning dates day
	f = calendarEnd.get(Calendar.YEAR);//this gets beginning dates year

	//System.out.println("month="+a+" day="+b+" year="+c);


	String str = "http://ichart.finance.yahoo.com/table.csv?s="
		+ symbol + "&d=" + d + "&e=" + e + "&f=" + f + "&g=d&a=" + a + "&b=" + b
		+ "&c=" + c + "&ignore=.csv";
	return str;
    }

    public static int getNumDays(Variables var) {
	Connection connection = MarketDB.getConnection();
	Date date = var.endDate;
	int numDays = -1;
	try {
	    while (MarketDB.isMatch(connection, var.list, date) == false) {
		numDays += 1;
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		java.util.Date utilDate = (cal.getTime());
		date = new java.sql.Date(utilDate.getTime());//convert to sql.date
	    }
	} catch (SQLException e) {
	    System.err.println("cannot perform isMatch method");
	}
	return numDays;
    }
}
