/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.buySellLineHistChart;

import ibd.classes.MarketRetriever;
import ibd.classes.Data;
import ibd.classes.MarketAnalyzer;
import ibd.classes.Output;
import ibd.classes.VarDow;
import ibd.classes.VarNasdaq;
import ibd.classes.VarSP500;
import ibd.classes.Variables;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;

/**
 *
 * @author Aaron
 */
public class GetChartData {

//    private String market;
//    private String time;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param market
     * @return
     * @throws ServletException if a servlet-specific error occurs
     * @throws MalformedURLException
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException
     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//	    throws ServletException, IOException, MalformedURLException, NullPointerException {
    public static Vector getData(String market) {

//	HashMap<String, Data> marketData=new HashMap<String, Data>();
	Variables var=null;
	Date[] BD=null;
	Date[] SD=null;
	Vector marketVec=new Vector();
	try {
	    if(market.equals("SP500")){
		Output outputSP500 = VarSP500.currentSP500;
		BD = outputSP500.buyDates;
		SD = outputSP500.sellDates;
		var=outputSP500.var;
	    } else if(market.equals("NAS")){
		Output outputNasdaq = VarNasdaq.currentNasdaq;
		BD = outputNasdaq.buyDates;
		SD = outputNasdaq.sellDates;
		var=outputNasdaq.var;
	    } else if(market.equals("DOW")){
		Output outputDow = VarDow.currentDow;
		BD = outputDow.buyDates;
		SD = outputDow.sellDates;
		var=outputDow.var;
	    }

	    HashMap<String, Data> marketData=MarketAnalyzer.retrieveMarketData(var);
	    Data data3 = marketData.get(var.list);
	    Date[] dates = data3.dateData;
	    float[] pricesClose=data3.priceDataClose;

	    marketVec.add(dates);
	    marketVec.add(pricesClose);
	    marketVec.add(BD);
	    marketVec.add(SD);

	} catch (Exception ex) {
	    Logger.getLogger(GetChartData.class.getName()).log(Level.SEVERE, null, ex);
	}
	return marketVec;
    }
}
