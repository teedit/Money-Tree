/**
 * Encapsulates the price and volume arrays for each stock into one object
 * @author Hugh
 *
 */
package ibd.classes;

import java.util.ArrayList;
import java.sql.Date;

public class Data {

    //these are all class variables or fields of the object, these are available to other classes since they are public,
    //if they were private then there would have to be public methods to make them accessible
    public float[] priceDataClose;
    public float[] priceDataCloseAVG50;
    public float[] priceDataHigh;
    public float[] priceDataLow;
    public float[] priceDataOpen;
    public long[] volumeData;
    public long[] volumeAVG50;
    public Date[] dateData;
    public float[] priceTrendData35;
    public double[] priceAVG50;
    public double[] priceCV50;
    public double[] volumeCV50;
    public double priceCV50AVGpDay = 0;
    public double volCV50AVGpDay = 0;
  
    //public int numberDays;//this counts the number of days that data was obtained

    /**
     * this is a constructor cause their is no return type and it doesn't say class, the variables in paranthesese are arguments of the constructor
     * that come from the dataParser method
     * @param date
     * @param pricesOpen
     * @param pricesHigh
     * @param pricesLow
     * @param pricesClose
     * @param volumes
     */
    public Data(ArrayList<Date> date, ArrayList<Float> pricesOpen, ArrayList<Float> pricesHigh,
	    ArrayList<Float> pricesLow, ArrayList<Float> pricesClose, ArrayList<Long> volumes) {
	//these are the instance variables of the Data class.  arrays are being initialized and subsequently populated
	priceDataClose = new float[pricesClose.size()];
	priceDataHigh = new float[pricesHigh.size()];
	priceDataLow = new float[pricesLow.size()];
	priceDataOpen = new float[pricesOpen.size()];
	volumeData = new long[volumes.size()];
	dateData = new Date[date.size()];
	volumeAVG50 = new long[volumes.size()];
	priceTrendData35 = new float[pricesClose.size()];
	priceAVG50 = new double[pricesClose.size()];
	priceCV50 = new double[pricesClose.size()];
	volumeCV50 = new double[volumes.size()];

	for (int i = 0; i < pricesClose.size(); ++i) {
	    priceDataClose[i] = pricesClose.get(i);
	    priceDataHigh[i] = pricesHigh.get(i);
	    priceDataLow[i] = pricesLow.get(i);
	    priceDataOpen[i] = pricesOpen.get(i);
	    volumeData[i] = volumes.get(i);
	    dateData[i] = date.get(i);
//	    long vSum50 = 0;
//	    float pSum50 = 0;
	    long[] vArray = new long[50];
	    double[] pArray = new double[50];
	    breakout1:
	    for (int j = 0; j < 50; j++) {
		try {
		    vArray[j] = volumes.get(i+j);
		    pArray[j] = pricesClose.get(i+j);
//		    vSum50 = vSum50 + volumes.get(i + j);
//		    pSum50 = pSum50 + pricesClose.get(i + j);
		} catch (IndexOutOfBoundsException e) {
//		    vSum50 = 0;
//		    pSum50 = 0;
		    break breakout1;
		}
	    }
	    volumeAVG50[i] = (long) Statistics.average(vArray);
	    priceAVG50[i] = Statistics.average(pArray);
	    volumeCV50[i] = Statistics.coeffVar(vArray);
//	    System.out.println(volumeAVG50[i]);
	    priceCV50[i] = Statistics.coeffVar(pArray);

	    float pDiffSum35 = 0;
	    breakout2:
	    for (int k = 0; k < 35; k++) {
		try {
		    pDiffSum35 = pDiffSum35 + (pricesClose.get(k + i) - pricesClose.get(k + i + 1)) / pricesClose.get(k + i + 1);
		} catch (IndexOutOfBoundsException e) {
		    pDiffSum35 = 0;
		    break breakout2;
		}
	    }
	    priceTrendData35[i] = pDiffSum35 / 35;//average of percent gains from day before for 35 days, used for dday churning calculation and to determine if rally is possible
	    }

	for (int i=0;i<priceCV50.length;i++){
	    priceCV50AVGpDay = priceCV50AVGpDay + priceCV50[i];
	    volCV50AVGpDay = volCV50AVGpDay + (double) volumeCV50[i];
//	    System.out.println(volumeAVG50[i]);
	}
	priceCV50AVGpDay = priceCV50AVGpDay/priceCV50.length;
	volCV50AVGpDay = volCV50AVGpDay/volumeCV50.length;
    }
}
