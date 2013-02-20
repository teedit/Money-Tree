package ibd.threads;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationThread implements Runnable {

    public static boolean _continueRunning = true;
    private static boolean _running = false;
    private static boolean _debug = true;
    public static Thread _thread = null;
    public static Thread _killerThread = null;
    public static Thread _pmThread = null;
    private static long _sleepTime = 30000;
    public static boolean _firstTimeRun = true;

    public ApplicationThread() {
    }

    static {
	System.out.println("starting the MarketAnalyzer load.");
	debug("Starting the thread from static method ................................ ");
	_thread = new Thread(new ApplicationThread());
	_thread.setDaemon(true);
	_thread.start();
    }

    public static String getStatus() {
	if (_thread == null) {
	    return "Application Thread is not running";
	} else if (!_thread.isAlive()) {
	    return "<FONT COLOR=RED>Application Thread is dead</FONT>";
	} else {
	    return "<FONT COLOR=GREEN>Application Thread is alive</FONT>";
	}
    }

    public static boolean isAlive() {
	if (_thread == null) {
	    return false;
	} else if (_thread.isAlive()) {
	    return true;
	}
	return false;
    }

    public static void setDebug(boolean value) {
	_debug = value;
    }

    public static void setSleepTime(long value) {
	_sleepTime = value;
    }

    public static long getSleepTime() {
	return _sleepTime;
    }

    public static boolean getDebug() {
	return _debug;
    }

    public static boolean getRunning() {
	return _running;
    }

    public static String isDebugOn() {
	if (getDebug()) {
	    return "<FONT COLOR=RED>Debug is on</FONT>";
	} else {
	    return "<FONT COLOR=GREEN>Debug is off</FONT>";
	}
    }

    public static int stopThread() {
	_continueRunning = false;
	if (_running) {
	    while (_running) {
		System.out.println("thread is running........");
	    }
	    _thread.interrupt();
	} else {
	    _thread.interrupt();
	}
	_running = false;
	return 1;
    }

    public static int startThread() {
	_continueRunning = true;
	if (_thread == null || !_thread.isAlive()) {
	    debug("Starting the application thread from startThread() .........");
	    _thread = new Thread(new ApplicationThread());
	    _thread.setDaemon(true);
	    _thread.start();
	    return 1;
	}
	return 0;
    }

    @Override
    public void run() {
	try {
	    Thread.sleep(10000);
	} catch (Exception e) {
	}





	while (_continueRunning) {
	    _running = true;
	    try {
		/* Start the killer thread so if the thread hangs for a specified amount of time (in KillerThread.java)
		it will kill this thread and restart it.*/
		_killerThread = new Thread(new ApplicationKillerThread());
		_killerThread.setDaemon(true);
		_killerThread.start();
	    } catch (Exception e) {
		System.out.println(e.toString());
		//_killerThread.interrupt();
	    }
	    try {
		ThreadActions.processJobs();
	    } catch (IOException ex) {
		Logger.getLogger(ApplicationThread.class.getName()).log(Level.SEVERE, null, ex);
	    }

	    try {
		_running = false;
		_killerThread.interrupt();
		if (_continueRunning) {
		    long _sleepTime = ThreadActions.getNextMinuteRunTime(10).getTimeInMillis();
		    debug("Thread is sleeping for " + _sleepTime + " milliseconds.");
		    Thread.sleep(_sleepTime);
		    //Thread.sleep(20000);//use this to test, 20 seconds
		}
	    } catch (InterruptedException ex) {
		Logger.getLogger(ApplicationThread.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    public static void debug(String value) {
	if (getDebug()) {
	    System.out.println(value);
	}
    }
}
