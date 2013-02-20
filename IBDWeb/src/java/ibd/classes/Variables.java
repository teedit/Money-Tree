/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.classes;

import java.sql.Date;

/**
 *
 * @author Aaron
 */
public class Variables {//constructor of the object
//instance variables
	public String fileName;//name of file to write results to
	public int holiDays;//days to get data before gregStartDate so calculations can be made 35 or 50 days before first day of period
	public String list;
	public Date startDate;//the actual start date is holiDays before this date.  1950 jan 3 is first year in database except need 100 days after this
	public Date endDate;
//    these are churn dday params
	public int dDayParam;//could this be changed based on, BV=10
	public double churnVolRange;//volume must be within 3% of the previous days volume for churn dday, BV=.03
	public double churnPriceRange;//priceClose must be less than 2% greater than previous day for churn day, BV=.02
	public boolean churnPriceCloseHigherOn;//priceClose must be greater than or equal to previous day, BV=true
	public boolean churnAVG50On;//volume of churn day must be higher than 50 day average, BV=true
	public boolean churnPriceTrend35On;//price must be on upswing over previous 35 days for a churn day, BV=false
	public double churnPriceTrend35;//if churnPriceTrend35On=true, price must be on upswing over previous 35 days for a churn day, BV=.001
//these are followthrough params
	public boolean volVolatilityOn;//if tru, calculates followthrough day based on volume volatility of the market, BV=false
	public double volumeMult;//follow through day volume multiplier, BV=1.1
	public double volMultTop;//if volVolatilityOn=true then this is the maximum value that volMult can be, BV=1.1
	public double volMultBot;//if volVolatilityOn=true then this is the minimum value that volMult can be, BV=1.1
	public boolean priceVolatilityOn;//if tru, calculates followthrough day based on the volatility of the market, BV=true
	public double priceMult;//follow through day price multiplier.  This should be 1.7% up from day before, BV=1.013
	public double priceMultTop;//if priceVolatilityOn=true then this is the maximum value that priceMult can be, BV=1.014, this might change if time period more present
	public double priceMultBot;//if priceVolatilityOn=true then this is the min value that priceMult can be, BV=1.012
	public int rDaysMin;//rally days must be greater than this, BV = 4
	public int rDaysMax;//rally days must be less than this, BV=18, this might change with time period change
	public boolean pivotTrend35On;//if tru, pivotTrend35 is turned on, BV=false
	public double pivotTrend35;//if pivotTrend35On=true price must trend down for 35 days by an average of -.1%/day before pivot day
	public boolean rallyVolAVG50On;//followthrough day volumes must be greater than the 50 day avg volume, BV=false
	public boolean rallyPriceHighOn;//if tru, the followthrough day must be high of the rally, BV=truee
}
