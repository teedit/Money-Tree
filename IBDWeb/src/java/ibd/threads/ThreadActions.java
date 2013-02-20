/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.threads;

import ibd.classes.SendFromGmail;
import ibd.classes.VarDow;
import ibd.classes.VarNasdaq;
import ibd.classes.VarSP500;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stan.rathbone
 */
public class ThreadActions {

    //ENTER NEXT RUNTIME VARIABLE HERE.  BE SURE THEY ARE STATIC OR IT WILL RESET THEM EVERYTIME YOU CALL THE CLASS
    private static IBDCalendar updateAnalysis = getNextDailyRunTime("15:30", -1);
    private static boolean hasProcessed = false;
    private static IBDCalendar sendEmail = getNextDailyRunTime("15:50", -1);

    public static void processJobs() throws IOException {//method to process jobs
	//ADD ANY CODE HERE THAT YOU WANT PROCESSED.....

	if (!hasProcessed || isItTime(updateAnalysis)) {
	    VarDow.varDow();
	    VarSP500.varSP500();
	    VarNasdaq.varNasdaq();
	    updateAnalysis = getNextDailyRunTime("15:30", 1);
	    hasProcessed = true;
	}
	if (isItTime(sendEmail)){
	    try {
		SendFromGmail.main();
	    } catch (Exception ex) {
		Logger.getLogger(ThreadActions.class.getName()).log(Level.SEVERE, null, ex);
	    }
	    sendEmail = getNextDailyRunTime("15:50", 1);
	}
    }

    public static IBDCalendar getNextDailyRunTime(String hour, int howManyDays) {
	if (hour == null) {
	    hour = "01:00";
	}
	IBDCalendar tomorrow = new IBDCalendar();
	tomorrow.add(IBDCalendar.DAY_OF_MONTH, howManyDays);
	IBDCalendar nextDay = new IBDCalendar(tomorrow.toString(IBDCalendar.DATE) + " 01:00", IBDCalendar.formats[50]);
	return nextDay;
    }

    public static IBDCalendar getNextHourRunTime(int howManyHours) {
	IBDCalendar a = new IBDCalendar();
	a.add(IBDCalendar.HOUR_OF_DAY, howManyHours);
	return a;
    }

    public static IBDCalendar getNextMinuteRunTime(int howManyMinutes) {
	IBDCalendar a = new IBDCalendar();
	a.add(IBDCalendar.MINUTE, howManyMinutes);
	return a;
    }

    public static boolean isItTime(IBDCalendar nextRunTime) {
	IBDCalendar currentTime = new IBDCalendar();
	long timeLeft = currentTime.getTimeInMillis() - nextRunTime.getTimeInMillis();
	if (timeLeft <= 0) {
	    return true;
	} else {
	    return false;
	}
    }
}
