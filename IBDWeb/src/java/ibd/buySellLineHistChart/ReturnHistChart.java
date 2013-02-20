/* -------------------------
 * BarChart.java
 * -------------------------
 * (C) Copyright 2005-2009, by Object Refinery Limited.
 *
 */
package ibd.buySellLineHistChart;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class ReturnHistChart {// extends ApplicationFrame {

    /**
     * Returns a sample dataset.
     *
     * @return The dataset.
     */
    private static XYDataset createDataset(Vector marketVec) {

	Date[] dates = (Date[]) marketVec.get(0);
	Arrays.sort(dates);
	ArrayList<Date> datesAL = new ArrayList(Arrays.asList(dates));
	float[] pricesClose0 = (float[]) marketVec.get(1);
	float[] pricesClose = new float[pricesClose0.length];
	int k = 0;
	for (int i = pricesClose0.length - 1; i >= 0; i--) {//this reverses the order of the pricesClose array
	    pricesClose[k] = pricesClose0[i];
	    k++;
	}

	Date[] BD = (Date[]) marketVec.get(2);
	Date[] SD = (Date[]) marketVec.get(3);
	if(SD[SD.length-1]==null){//puts todays date at end of SD if it is null
	    java.util.Date goob=new java.util.Date();
	    long woob=goob.getTime();
	    SD[SD.length-1]=new Date(woob);
	}

	TimeSeriesCollection dataset = new TimeSeriesCollection();

	int j = 0;
	TimeSeries buySeries = null;// = new TimeSeries("");
	TimeSeries sellSeries = null;// = new TimeSeries("");
	try {
	    for (int i = 0; i < pricesClose.length; i++) {//loops through pricesClose
		if (dates[i].before(BD[0])) {//gets the loop up to first buyDate
		    continue;
		}
		if (datesAL.get(i).equals(BD[j])) {
		    buySeries = new TimeSeries("buySeries" + (j + 1));
		}
		if (datesAL.get(i).equals(SD[j])) {
		    sellSeries = new TimeSeries("sellSeries" + (j + 1));
		}

		//this populates the buy periods
		if ((dates[i].after(BD[j]) | dates[i].equals(BD[j])) & dates[i].before(SD[j])) {
		    buySeries.add(new Day(new java.util.Date(dates[i].getTime())), pricesClose[i]);

		    //this populates the sell periods
		} else if ((dates[i].after(SD[j]) | dates[i].equals(SD[j])) & dates[i].before(BD[j + 1])) {//todo this last before needs to be changed cuz ther won't be a BD(j+1) if is is in a sell period
		    sellSeries.add(new Day(new java.util.Date(dates[i].getTime())), pricesClose[i]);
		} else {//hits this after sellSeries and buySeries are complete
		    dataset.addSeries(buySeries);
		    dataset.addSeries(sellSeries);
		    j++;
		    i--;//i has to be decremented because by the time it has gone through the iteration is lost
		}
	    }//end of for loop
	} catch (IndexOutOfBoundsException e) {
	    System.err.println(e);
	}

	return dataset;
    }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private static JFreeChart createChart(XYDataset dataset, String symbol) {
	JFreeChart chart = ChartFactory.createXYAreaChart(
		"Buy/Sell Periods for " + symbol,
		"Day", "Price",
		dataset,
		PlotOrientation.VERTICAL,
		true, // legend
		true, // tool tips
		false // URLs
		);
	XYPlot plot = (XYPlot) chart.getPlot();
	plot.setDomainPannable(true);
	ValueAxis domainAxis = new DateAxis("Year");
	domainAxis.setLowerMargin(0.0);
	domainAxis.setUpperMargin(0.0);
	plot.setDomainAxis(domainAxis);
//	plot.setForegroundAlpha(0.7f);

	XYItemRenderer renderer = plot.getRenderer();
	renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
		StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
		new SimpleDateFormat("d-MMM-yyyy"),
		new DecimalFormat("#,##0.00")));
	ChartUtilities.applyCurrentTheme(chart);
	return chart;
    }

    /**
     * Returns a chart object with a given dataset
     * @param marketVec
     * @param symbol
     * @return
     */
    public static JFreeChart returnChart(Vector marketVec, String symbol) {
	XYDataset dataset = ReturnHistChart.createDataset(marketVec);
	JFreeChart chart = ReturnHistChart.createChart(dataset, symbol);
	return chart;
    }
}//end of class

