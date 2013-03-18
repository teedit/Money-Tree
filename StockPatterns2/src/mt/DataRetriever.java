package mt;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * This class is the data gathering module of the "MoneyTree" project.  DataRetriever
 * connects to yahoo.com and acquires general market data and assesses the current
 * financial climate by counting distribution days.  If conditions are favorable,
 * DataRetriever then scans through the IBD top 100 stock picks at investors.com,
 * and assesses each stock for its current condition using the IBD 5 quality
 * ratings.  The last 18 months' price and volume data for each stock that
 * 'passes' is retrieved and stored in a database for subsequent analysis
 * by the analysis module. 
 */
public class DataRetriever{

	/**
	 * the initial cookie necessary for accessing IBD members only content
	 */
	private String shortCookie = "ibdSession=Webuserid=185690723951612&SessionID=71ab59b8-0020-458c-aafd-6013ca21a335&LogInFlag=1&token=5z1y8LA+iUHmL3fUw95PhpaemYt+g97M1UTMTcmSl0NZOn4UdfUl2EU3N1KtVWXquzqdwKrh7dudWeXyuVoUmiuPlPLMVPiFURWmnCENdYe/VGfWxuy0j1lhRzzjHwWO;";

	/**
	 * the cookie necessary for full access to top100 data
	 */
	private String longCookie;

	private int counter;
	
	/**
	 * indicates whether queried markets are favorable for buying
	 */
	HashMap<String,Boolean> marketStatus = new HashMap<String,Boolean>();

	/**
	 * array of the ticker symbols for the IBD100
	 */
	private String[] top100;

	private String[] list = {"^GSPC","^IXIC"};//the list of markets to query, simply add market here to query more
	public static final int MONTH;
	public static final int DAY;
	public static final int YEAR;
	public static final int THELENGTH = 1;
	static{
		GregorianCalendar gc = new GregorianCalendar();
		MONTH = gc.get(GregorianCalendar.MONTH);
		DAY = gc.get(GregorianCalendar.DAY_OF_MONTH);
		YEAR = gc.get(GregorianCalendar.YEAR);
	}
	/*
	 * Good Java programming dictates that main be tiny, instantiating
	 * an object of the class, then sending the logic to another method.
	 * This provides an object of the class on which each method can operate.
	 */
	public static void main(String[] args)throws FileNotFoundException, IOException, URISyntaxException, SQLException, ClassNotFoundException{
		DataRetriever dr = new DataRetriever();
		dr.retrieve();
	}
	/*
	 * getTop100Cookie() accesses investors.com by using a cookie acquired by 
	 * watching LiveHeaders (Firefox plugin) when legitimate username and password
	 * are used to access the website.  With valid access cookie, investors.com 
	 * sets a cookie that contains the entire top 100, which is stored as cookieValue.
	 * The useful portion of this cookie is stored as top100String by extractTop100String,
	 * then, top100String is parsed into a String array by getTop100.
	 */
	public int initialize(){
		counter = 0;
		int successCount = 0;
		top100 = new String[100];
		setTop100Catcher();//retrieve top 100 and store them in top100
		for(int i = 0;i < THELENGTH;++i){//loop through and set all non-ideal stocks to null
			if(!checkStock(top100[i]))
				top100[i] = null;
			else
				++successCount;//track how many are good
		}
		System.out.println(successCount + " passed and " + (THELENGTH - successCount) + " failed");
		return successCount;
	}
	
	/*
	 * The primary engine for DataRetriever.  
	 *   Then retrieve()
	 * loops through some portion of the top100 and isViable() accesses IBD ratings 
	 * values and identifies those stocks whose ratings meet the established thresholds
	 * (see isViable() ).  These stocks price and value data for the last 18 months is 
	 * retrieved and saved in a MYSQL database.
	 */
	public Data retrieve(){
			counter++;
			if(top100[counter] != null)
				return getData(top100[counter],252 * 5);
			return null;		
	}

	/**
	 * Establishes a connection to the MYSQL database
	 * @return  the Connection object
	 */
	public static Connection getConnection(){
		Connection con = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String dbURL = "jdbc:mysql://localhost:3307/moneytree";
			String username = "root";
			String password = "password";
			con = DriverManager.getConnection(dbURL, username, password);
		}
		catch(ClassNotFoundException e){
			System.out.println("Database driver not found.");
		}
		catch(SQLException e){
			System.out.println("Error loading database driver: " + e.getMessage());
		}
		return con;
	}

	/**
	 * Gets the value and volume data from the requested markets (by calling 
	 * dataParser() ), then determines how many distribution days there have 
	 * been in the last 4 weeks.  A distribution day is defined as a day when
	 * value decreased and volume increased, with the threshold set at less 
	 * than 4.  Boolean is saved in the marketStatus HashMap.
	 * @return  true if less than 4 d-days, false otherwise
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Data getMarketData(String market, int startMonth,int startDay,int startYear) {
		Data data = null;
		int flag = 0;
		while (flag < 20){
			try{
				String URL = buildYahooUrl(market,startMonth,startDay,startYear);
				data = dataParser(market,URL);// extract price and volume data
				flag = 25;
			}catch (NumberFormatException e){
				++flag;
				System.err.println("Proper connection failed, trying again...");
			}catch (UnknownHostException e){
				++flag;
				System.err.println("Proper connection failed, trying again...");
			}catch (Exception e){
				e.printStackTrace(System.out);
				System.exit(1);}
		}
		if(flag < 25){
			System.err.println("Failed to connect to yahoo.com");
			System.exit(1);
		}
		return data;
	}

	/**
	 * method solely for the purpose of removing messy exception-catching code from
	 * other places, so it goes here out of the way. 
	 */
	private void setTop100Catcher(){
		int flag = 0;
		while (flag < 20){
			try{
				setTop100();
				flag = 25;
			}catch (StringIndexOutOfBoundsException e){
				++flag;
				System.err.println("Proper connection failed, trying again...");
			}catch (Exception e){
				System.err.println("Error: " + e);
				System.exit(1);
			}
		}
		if(flag < 25){
			System.err.println("Failed to extract top 100 from investors.com");
			System.exit(1);
		}
	}
	/**
	 * Uses IBD members web site to access the IBD 100.  When a user logs in with a
	 * browser, IBD sets a cookie that includes the top 100 stocks.  So we only need
	 * to parse that cookie to store the top 100.  By watching Live Headers (Firefox
	 * utility) I could get the user cookie that is set when a login occurs.  This is 
	 * accessed by the getCookie() method, which simply returns that cookie acquired 
	 * from Live Headers.  By accessing the user site with that cookie, we can get
	 * the new cookie, which contains the top 100. 
	 * @return the string consisting of the cookie with the top 100
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void setTop100()throws FileNotFoundException, IOException, URISyntaxException{
		String u = "http://www.investors.com/member/IBD100TopRated/";
		URL url = new URL(u);
		HttpURLConnection HUC =(HttpURLConnection)url.openConnection();
		HUC.setRequestProperty("Cookie",shortCookie);
		HUC.setRequestMethod("GET");
		Map<String, List<String>> headers = HUC.getHeaderFields(); 
		List<String> values = headers.get("Set-Cookie"); 
		StringBuffer cookieValue = new StringBuffer(shortCookie); 
		for (Iterator<String> iter = values.iterator(); iter.hasNext(); )
			cookieValue.append(iter.next());
		parseTop100(cookieValue.toString());
	}

	/**
	 * parses the cookie string into a String array
	 * @param top100String the cookie string passed from setTop100
	 */
	private void parseTop100(String cookie){
		top100 = new String[100];
		String spacer = "%2C";
		String initTarget = "ibdLists=StockList100=";
		int mark1 =  cookie.indexOf(initTarget) + initTarget.length();
		int mark2 = cookie.indexOf(spacer);
		for(int i = 0;i < 100;++i){
			top100[i] = cookie.substring(mark1,mark2);
			System.out.println(top100[i]);
			mark1 = mark2 + spacer.length();
			mark2 = cookie.indexOf(spacer,mark1);
		}
		String c = cookie.substring(cookie.indexOf(initTarget,mark2 + 3));
		longCookie = shortCookie + c + ";";
	}

	/**
	 * accesses the 5 IBD parameters by building the appropriate URL with the ticker
	 * symbol and using a cookie acquired with getTop100Cookie() and extractTop100String().
	 * getParams is called to get the parameters, then isViable determines if they meet
	 *  the required thresholds:
	 * EPS:  85 or greater
	 * price strength:  80 or greater
	 * SMR:  B or better
	 * industry:  A
	 * Accumulation/Distribution:  B or better
	 * @param tickSymbol  the ticker symbol of the current stock
	 * @return  whether or not the stocks parameters meet all the thresholds
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private boolean checkStock(String tickSymbol) {
		String ratingsURL = "http://www.investors.com/member/checkup/checkUp.asp?t="
			+ tickSymbol + "&ss=YES&ac=IFT";//  This builds the URL for the site where each stocks IBD ratings are
		int flag = 0;
		StockParameters params = null;
		while (flag < 20){
			try{
				String dataString = urlContentToString(ratingsURL,longCookie);
				params = getParams(dataString);
				flag = 20;
			}catch (StringIndexOutOfBoundsException e){
				++flag;
				System.err.println("Proper connection failed, trying again...");
			}catch (NumberFormatException e){
				++flag;
				System.err.println("Proper connection failed, trying again...");
			}catch (Exception e){
				System.err.println("Error: " + e);
				System.exit(1);
			}
		}
		System.out.println(tickSymbol + ":");
		System.out.println(params.EPSrating + " " + params.PSrating + " " + params.SMRrating + 
				" " + params.INDrating + " " + params.ADrating);
		if (params.EPSrating > 84 && params.PSrating > 79 && (params.SMRrating == 'A' ||
				params.SMRrating == 'B') && params.INDrating == 'A' && (params.ADrating == 'A' || 
						params.ADrating == 'B')){	
			System.out.println("PASSED");
			return true;
		}
		System.out.println("FAILED");
		return false;
	}

	/**
	 * Converts the contents of a URL to a String
	 * @param url  the URL to be read from 
	 * @param needsCookie  indicates a need for a cookie to access secure content,
	 * such as subscription stock data on investors.com
	 * @return  String representation of the site's content
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static String urlContentToString(String url, String cookie) throws MalformedURLException, URISyntaxException, IOException{
		URL ur = new URL(url);
		HttpURLConnection HUC =(HttpURLConnection)ur.openConnection();
		if (cookie.length() > 0)
			HUC.setRequestProperty("Cookie",cookie);
		DataInputStream is = new DataInputStream(HUC.getInputStream());
		byte[] g = new byte[4096];//buffer array for incoming data
		int f = 0;
		StringBuffer theString = new StringBuffer("");
		while (true) {     
			f=is.read(g); 
			if (f <= 0)  break;//read 4k worth of data into byte array
			String tS = new String(g);//convert that array into string
			theString.append(tS);//add to growing String of html text
		}
		is.close();HUC.disconnect();
		return theString.toString();//pass back result
	}

	/**
	 * Extracts market capitalization value for given tick symbol.  Accesses yahoo.com
	 *  and parses data page, extracts market capitalization value and converts to
	 *  long.  Note: this method is not currently used, since S&P500 is the only
	 *  market used currently.
	 * @param tick  represents the stock for which market cap is to be acquired
	 * @return the market cap value
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static long getMC(String tick) throws MalformedURLException, URISyntaxException, IOException
	{
		String yahooFundamentalsURL = "http://finance.yahoo.com/q?s=" + tick;
		String str = urlContentToString(yahooFundamentalsURL,"");
		String marketCapTarget = "Market Cap:</td><td class=\"yfnc_tabledata1\"><span id=";
		int a = str.indexOf(marketCapTarget);
		a = a + marketCapTarget.length();
		a = str.indexOf(">",a);
		int b = str.indexOf("<",a);
		String mc = str.substring(a,b);
		long MC = Long.parseLong(mc.substring(0,mc.length() - 1));
		if (mc.charAt(mc.length()) == 'K')
			MC = MC * 1000;
		else if (mc.charAt(mc.length()) == 'M')
			MC = MC * 1000 * 1000;
		else if(mc.charAt(mc.length()) == 'B')
			MC = MC * 1000 * 1000 * 1000;
		return MC;
	}

	/**
	 * Retrieves values for each of the 5 IBD rating values, saves them in a 
	 * String array, then uses the StockParameters inner class constructor
	 * to parse and save them in their proper form.
	 * @param str the string representing the content of an individual stock's
	 * IBD data page.
	 * @return  an instance of the StockParameters class, which encloses a stock's
	 * rating values.
	 */
	public StockParameters getParams(String str)
	{
		String[] params = new String[5];
		int mark1 = 0;int mark2 = 0;
		//this string of stuff immediately precedes each rating value in the html text
		String target = "%\"><h2><font color=\"#336699\">";
		mark2 = target.length();
		mark1 = str.indexOf(target,mark1);//a is now set to the index of the first target string
		mark1 += mark2;//now it's after the target string so it's right at the first value
		params[0] = str.substring(mark1,mark1 + 2);//extracts two characters corresponding to the EPS rating
		mark1 = str.indexOf(target,mark1);//finds the next target
		mark1 += mark2;               //etc
		params[1] = str.substring(mark1,mark1 + 2);//relative price strength rating
		mark1 = str.indexOf(target,mark1);
		mark1 += mark2;
		params[2] = "" + str.charAt(mark1);//industry strength
		mark1 = str.indexOf(target,mark1);
		mark1 += mark2;
		params[3] = "" + str.charAt(mark1);//smr
		mark1 = str.indexOf(target,mark1);
		mark1 += mark2;
		params[4] = "" + str.charAt(mark1);//accumulation/distribution rating
		//stores the array in a StockParameters object where they are properly converted
		StockParameters sp = new StockParameters(params); 
		return sp;
	}

	/**
	 * An inner class which encapsulates the 5 IBD ratings for each stock
	 * @author Hugh
	 *
	 */
	private class StockParameters
	{
		private int EPSrating;
		private int PSrating; //price strength
		private char SMRrating;
		private char INDrating;//industry
		private char ADrating;//accumulation/distribution
		public StockParameters(){}
		/**
		 * constructor for converting each rating value to its proper form
		 * @param parameters  the string array to convert
		 */
		public StockParameters(String[] parameters){
			EPSrating = Integer.parseInt(parameters[0]);
			PSrating = Integer.parseInt(parameters[1]);
			SMRrating = parameters[2].charAt(0);
			INDrating = parameters[3].charAt(0);
			ADrating = parameters[4].charAt(0);
		}
	}

	/**
	 * Overseeing method for retrieving stock-specific price and volume data,
	 * getYahooStockURL() is first called to assemble the appropriate URL, then
	 * dataParser compiles the stock and volume data into a Data object
	 * @param symbol  the stock for which data is to be gathered
	 * @param days  how many days for which to gather data
	 * @return a Data object
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private Data getData(String symbol,int days){
		int flag = 0;
		Data data = null;
		String url = getYahooURL(symbol,730);
		while (flag < 25){
			try{
				data = dataParser(symbol,url);
				flag = 25;
			}catch (NumberFormatException e){
				++flag;
				System.err.println("Proper connection failed, trying again...");
			}catch (UnknownHostException e){
				++flag;
				System.err.println("Proper connection failed, trying again...");
			}catch (Exception e){
				e.printStackTrace(System.out);
				System.exit(1);}
		}
		if(flag < 25){
			System.err.println("Unable to connect to Yahoo.com : " + url);
			System.exit(1);
		}
		return data;
	}

	/**
	 * reads data from the specified URL, parses price and volume data
	 * and stores them in the Data class. 
	 * @param url  the URL to read from
	 * @param days the number of previous days from which data should be 
	 * gathered
	 * @return an instance of the Data class, containing an array 
	 * of prices and an array of corresponding volumes
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Data dataParser(String symbol, String url) throws MalformedURLException, IOException, URISyntaxException
	{
		ArrayList<Float> priceLowList = new ArrayList<Float>();
		ArrayList<Float> priceList = new ArrayList<Float>();
		ArrayList<Double> volumeList = new ArrayList<Double>();		
		URL ur = new URL(url);
		HttpURLConnection HUC =(HttpURLConnection)ur.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(HUC.getInputStream()));
		String line;
		int lineCount = 0;
		in.readLine();//reads the first line, it's just headers
		//gets a line of input if available, beginning of main loop
		// data format: Date,Open,High,Low,Close,Volume,Adj Close
		while((line = in.readLine()) != null){
			int len = line.length();
			int lineIndex = 0;
			//count through the first 3 items; they are ignored
			for(int count = 0;lineIndex < len && count < 3;++count)
				lineIndex = line.indexOf(',',lineIndex) + 1;
			int begin = lineIndex;
			for(int count = 0;lineIndex < len && count < 3;++count)
				lineIndex = line.indexOf(',',lineIndex) + 1;
			if(lineIndex >= len){
				System.err.println("Error reading data from Yahoo");
				System.exit(1);
			}
			int end = lineIndex - 2;//move back before last comma
			String sub = line.substring(begin,end);
			String[] array = sub.split(",");


			//add current values to arrayLists

			priceLowList.add(Float.parseFloat(array[0]));//low is in first position
			priceList.add(Float.parseFloat(array[1]));//close price is second
			volumeList.add(Double.parseDouble(array[2]));//volume is third
			++lineCount;
		}
		return new Data(symbol,priceList,volumeList,priceLowList);//stores the data arrays in a Data object and returns it
	}

	/**
	 * Builds url for 4 weeks of market data on yahoo.com.  The url contains values 
	 * for the starting year, month and day and ending year, month and day for the 
	 * requested data.  
	 * @param market the market for which data is desired
	 * @return a String representing the constructed url
	 */
	public static String getYahooURL(String market,int days){
		GregorianCalendar calendar = new GregorianCalendar();
		int month,day,year,startMonth,startDay,startYear;
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		year = calendar.get(Calendar.YEAR);
		startMonth = month - (int)((days%365)/30.41667);
		startDay = day - (int)((days%365)%30.41667);
		startYear = year - days/365;
		if(startMonth < 0){
			startMonth += 12;
			--startYear;
		}
		if(startDay < 0){
			startDay += 30;
			--startMonth;
		}

		return buildYahooUrl(market,startMonth,startDay,startYear);
	}

	public static String buildYahooUrl(String market,int startMonth,int startDay,int startYear ){
		return "http://ichart.finance.yahoo.com/table.csv?s=" + market +
		"&d=" + MONTH + "&e=" + DAY + "&f=" + YEAR + "&g=d&a=" + startMonth + "&b=" + startDay +
		"&c=" + startYear + "&ignore=.csv";
	}

	/**
	 * decides which market a ticker symbol belongs to based on number of characters, 
	 * this method is not used; I'm not sure this is reliable
	 * @param symbol
	 * @return
	 */
	private String assign(String symbol){
		if(symbol.length() == 4)
			return "^IXIC";
		return "^GSPC";
	}
	
	public boolean moreToRetrieve(){
		
		return counter < THELENGTH;
	}
	
	public int arrayLength(){
		return THELENGTH;
	}


}

