/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.classes;

import java.io.IOException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.*;

/**
 *
 * @author Aaron
 */
public class VarDow {

    /**
     * @param args the command line arguments
     */
    public static Output currentDow = null;

    /**
     * 
     * @throws Exception
     */
    public static void varDow() throws IOException {//this method sets values for the object var fields
	Output futureDow = null;
	Variables var = new Variables();//new instance of object Variables

	var.fileName = "ResultsDow.txt";
	var.list = "^DJI";
	var.startDate = Date.valueOf("1980-01-01");//the actual start date is holiDays before this date.  1928 oct 1 is first year in database except need 100 days after this
//	var.endDate = Date.valueOf("2009-12-31");
	java.util.Date today = new java.util.Date();//endDateDate=today.getTime();
	var.endDate = new java.sql.Date(today.getTime());//this is a constructor that calls the milliseconds of today
	//    these are churn dday params
	var.dDayParam = 10;//could this be changed based on, BV=10
	var.churnVolRange = .05;//volume must be within 3% of the previous days volume for churn dday, BV=.03
	var.churnPriceRange = .02;//priceClose must be less than 2% greater than previous day for churn day, BV=.02
	var.churnPriceCloseHigherOn = true;//priceClose must be greater than or equal to previous day, BV=true
	var.churnAVG50On = true;//volume of churn day must be higher than 50 day average, BV=true
	var.churnPriceTrend35On = false;//price must be on upswing over previous 35 days for a churn day, BV=false
	var.churnPriceTrend35 = .003;//if churnPriceTrend35On=true, price must be on upswing over previous 35 days for a churn day, BV=.001
	//these are followthrough params
	var.volVolatilityOn = true;//if tru, calculates followthrough day based on volume volatility of the market, BV=false
	var.volMultTop = 1.30;//if volVolatilityOn=true then this is the maximum value that volMult can be, BV=1.1
	var.volumeMult = 1.18;//follow through day volume multiplier, BV=1.1
	var.volMultBot = 1.05;//if volVolatilityOn=true then this is the minimum value that volMult can be, BV=1.1
	var.priceVolatilityOn = false;//if tru, calculates followthrough day based on the volatility of the market, BV=true
	var.priceMultTop = 1.011;//if priceVolatilityOn=true then this is the maximum value that priceMult can be, BV=1.014, this might change if time period more present
	var.priceMult = 1.011;//follow through day price multiplier.  This should be 1.7% up from day before, BV=1.013
	var.priceMultBot = 1.008;//if priceVolatilityOn=true then this is the min value that priceMult can be, BV=1.012
	var.rDaysMin = 5;//rally days must be greater than this, BV = 4
	var.rDaysMax = 18;//rally days must be less than this, BV=18, this might change with time period change
	var.pivotTrend35On = false;//if tru, pivotTrend35 is turned on, BV=false
	var.pivotTrend35 = -.003;//if pivotTrend35On=true price must trend down for 35 days by an average of -.1%/day before pivot day
	var.rallyVolAVG50On = false;//followthrough day volumes must be greater than the 50 day avg volume, BV=false
	var.rallyPriceHighOn = true;//if tru, the followthrough day must be high of the rally, BV=true

	try {
	    MarketRetriever.main(var);
	} catch (IOException ex) {
	    Logger.getLogger(VarDow.class.getName()).log(Level.SEVERE, null, ex);
	} catch (NullPointerException e){
	} finally {
	    futureDow = (Output) MarketAnalyzer.checkMarkets(var);//calls checkMarkets method of the MarketAnalyzer class
	    currentDow = futureDow;
	}
    }
}
