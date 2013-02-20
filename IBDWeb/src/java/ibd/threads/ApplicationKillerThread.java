package ibd.threads;

//import autoliv.common.utils.*;


public class ApplicationKillerThread implements Runnable
{
    
    private static long _killerSleepTime = 6000000;  // killer will sleep for 100 minutes.
    public ApplicationKillerThread(){
    }
    
    public static void setKillerSleepTime(long value){ _killerSleepTime=value;  }
    public static long getKillerSleepTime(){ return _killerSleepTime;   }
    
    public void run()
    {
        try{
            ApplicationThread.debug("killerThread is sleeping for "+_killerSleepTime+" Milliseconds");
            Thread.sleep(_killerSleepTime);
        }
        catch(Exception e)
        {
            ApplicationThread.debug("Killer thread was killed");
        }
        
        if(ApplicationThread.isAlive() && ApplicationThread.getRunning())
        {
            ApplicationThread.debug("############################# killing the application thread dut to time out ############################");
            ApplicationThread.stopThread();
            ApplicationThread.debug("Killer Thread is restarting the UtsThread");
            ApplicationThread.startThread();
        }
    }
    
}