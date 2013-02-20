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
public class Output implements Cloneable{

    //class variables
    public int rallyDaysToday;
    public String buyOrSellToday;
    public Date lastBuyDate;
    public Date lastSellDate;
    public float dDaysPerc;
    public Variables var;
    public Date[] buyDates;
    public Date[] sellDates;
    public float[][] histReturns;
    public Date lastDataDate;


    //CONSTRUCTOR
    public Output(Variables var, int rallyDaysToday,String buyOrSellToday,Date lastBuyDate,
	    Date lastSellDate, float dDaysPerc, Date[] buyDates,Date[] sellDates,
	    float[][] histReturns, Date lastDataDate){//String buyOrSellPeriod,Date lastBuyDate,

	this.rallyDaysToday=rallyDaysToday;
	this.buyOrSellToday=buyOrSellToday;
	this.lastBuyDate=lastBuyDate;
	this.lastSellDate=lastSellDate;
	this.dDaysPerc=dDaysPerc;
	this.var=var;
	this.buyDates=buyDates;
	this.sellDates=sellDates;
	this.histReturns=histReturns;
	this.lastDataDate=lastDataDate;
    }
}