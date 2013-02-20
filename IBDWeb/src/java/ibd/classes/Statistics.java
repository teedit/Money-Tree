/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.classes;

/**
 *
 * @author Aaron
 */
public class Statistics {

    public static double coeffVar(double[] a) {//this computes the CV average per day (not the CV of the whole population)
	double average = average(a);
	double total = 0;
	double all2 = 0;
	for (int i = 0; i < a.length; i++) {
	    double all = Math.pow(a[i] - average, 2);
	    total = total + all;
	}
	double total2 = total / a.length;
	all2 = Math.sqrt(total2);
	return all2 / average/a.length;
    }

    /**
     *
     * @param a
     * @return
     */
    public static double average(double[] a) {
	double average = 0;
	for (int i = 0; i < a.length; i++) {
	    average = average + a[i];
	}
	if (a.length > 0) {
	    return (average / a.length);
	} else {
	    System.out.println("ERROR: Can't average 0 numbers.");
	    return 0;
	}
    }

    public static double coeffVar(long[] a) {//this computes the CV average per day (not the CV of the whole population)
	double average = average(a);
	double total = 0;
	double all2 = 0;
	for (int i = 0; i < a.length; i++) {
	    double all = Math.pow(a[i] - average, 2);
	    total = total + all;
	}
	double total2 = total / a.length;
	all2 = Math.sqrt(total2);
	return all2 / average/a.length;//divides the CV50 by 50 to get CV per day
    }

    /**
     *
     * @param a
     * @return
     */
    public static double average(long[] a) {
	double average = 0;
	for (int i = 0; i < a.length; i++) {
	    average = average + a[i];
	}
	if (a.length > 0) {
	    return (average / a.length);
	} else {
	    System.out.println("ERROR: Can't average 0 numbers.");
	    return 0;
	}
    }
}



