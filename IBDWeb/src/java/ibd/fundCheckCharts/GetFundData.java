/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.fundCheckCharts;

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
public class GetFundData {

//    private String fund;
//    private String time;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param fund
     * @return
     * @throws ServletException if a servlet-specific error occurs
     * @throws MalformedURLException
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException
     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//	    throws ServletException, IOException, MalformedURLException, NullPointerException {
    public static Vector getData(String fund) {
	Vector gains = new Vector();
	try {
	    //TODO
	    //start here next time

	    Output outputSP500 = VarSP500.currentSP500;
	    Output outputNasdaq = VarNasdaq.currentNasdaq;
	    Output outputDow = VarDow.currentDow;

	    Date[] BDSP500 = outputSP500.buyDates;
	    Date[] BDDow = outputDow.buyDates;
	    Date[] BDNas = outputNasdaq.buyDates;
	    Date[] SDSP500 = outputSP500.sellDates;
	    Date[] SDDow = outputDow.sellDates;
	    Date[] SDNas = outputNasdaq.sellDates;

	    Data fundData = null;
	    fundData = MarketRetriever.getData(fund, 18000);//this should be enough data for all yeats
	    HashMap<String, Data> dataMap = new HashMap<String, Data>();
	    dataMap.put(fund, fundData);
	    Variables vars = new Variables();
	    vars.list = fund;
	    java.util.Date today = new java.util.Date(); //these two lines convert to a sql.date from util.date
	    vars.endDate = new java.sql.Date(today.getTime()); //this is a constructor that calls the milliseconds of today

	    float[][] gainsSP500 = null;
	    float[][] gainsDow = null;
	    float[][] gainsNas = null;
	    gainsSP500 = MarketAnalyzer.yearlyReturns(dataMap, BDSP500, SDSP500, vars);
	    gainsDow = MarketAnalyzer.yearlyReturns(dataMap, BDDow, SDDow, vars);
	    gainsNas = MarketAnalyzer.yearlyReturns(dataMap, BDNas, SDNas, vars);

	    gains.add(gainsSP500);
	    gains.add(gainsDow);
	    gains.add(gainsNas);

	} catch (URISyntaxException ex) {
	    Logger.getLogger(GetFundData.class.getName()).log(Level.SEVERE, null, ex);
//	    request.setAttribute("error",ex);
//	    request.setAttribute("fund",fund);
//	    RequestDispatcher rd = request.getRequestDispatcher("/fundDataError.jsp");
//	    rd.forward(request, response);
	} catch (MalformedURLException ex) {
	    Logger.getLogger(GetFundData.class.getName()).log(Level.SEVERE, null, ex);
//	    request.setAttribute("error",ex);
//	    request.setAttribute("fund",fund);
//	    RequestDispatcher rd = request.getRequestDispatcher("/fundDataError.jsp");
//	    rd.forward(request, response);
	} catch (IOException ex) {
	    Logger.getLogger(GetFundData.class.getName()).log(Level.SEVERE, null, ex);
//	    request.setAttribute("error",ex);
//	    request.setAttribute("fund",fund);
//	    RequestDispatcher rd = request.getRequestDispatcher("/fundDataError.jsp");
//	    rd.forward(request, response);
	} catch (NullPointerException ex) {
	    Logger.getLogger(GetFundData.class.getName()).log(Level.SEVERE, null, ex);
//	    request.setAttribute("error",ex);
//	    request.setAttribute("fund",fund);
//	    RequestDispatcher rd = request.getRequestDispatcher("/fundDataError.jsp");
//	    rd.forward(request, response);
	}
	return gains;
    }
}
