package mt;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
//need to: 
//fix CupWithHandle to use getTrend for the handle transition
public class Commander {

	private String[] list = {"^GSPC","^IXIC"};//the list of markets to query, simply add market here to query more
	private LinkedList<String> picks;
	public static void main(String[] args) throws SQLException{
		Commander cdr = new Commander();
		cdr.command();
	}

	public void command() throws SQLException{
		DataRetriever dr = new DataRetriever();
		DataAnalyzer da = new DataAnalyzer();
		Data marketData,data;
		marketData = dr.getMarketData("^GSPC",3,1,2003);// get a data object for the S&P
		dr.initialize();//evaluate the basic parameters of the stocks in the top 100
		da.initialize(marketData);//initialize the DataAnalyzer object
		if(da.bullOrBear(marketData))//assess climate
			if(da.checkDDays(marketData))// check distribution days
				while(dr.moreToRetrieve()){
					data = dr.retrieve();
					if(data != null)
						da.analyze(data);
				}
				picks = da.getPicks();
				if(picks != null){
					Iterator<String> iter = picks.iterator();
					System.out.println("Buy recommendations:");
					while(iter.hasNext())
						System.out.println(iter.next());
				}
				else
					System.out.println("No buy recommendations");	
	}
}
