package mt;

import java.util.TreeSet;

public class FlagPole extends Base {
	private static final int POLEMIN = 20; // minimum length in days for a flag pole pattern to rise to its maximum
	private static final int POLEMAX = 40;// max length
	private static final short GAINMIN = 2;//minimum gain for a flag pole, i.e. doubling is price
	private static final short GAINMAX = (short)2.2;// gain should be no more than 2.2
	private static final short WANDERVALUE = (short).07;//allowed percent deviation from straight line from bottom to top of pole
	public static final float VOLDOWN = (float).5;
	public static final float DAYSLOWVOLUME = (float).5;

	public FlagPole(int begin, int end, int min){
		super(begin,end,min);
	}

	public static boolean find(float[] prices,double[] volumes, DataAnalyzer da, int begin, int end, boolean[] baseBlocks, TreeSet<Base> list,boolean[] bullArray, float[] SandP) {
		int peakDay,minDay,pivot;
		System.out.print("  flagpole: ");
		for (int i = begin;i < end - POLEMIN;++i){//look through the whole array
			if((peakDay = checkUpSlope(i,POLEMIN,POLEMAX,prices,GAINMIN,WANDERVALUE,GAINMAX)) >= 0)
				if((pivot = checkHandle(true,prices,peakDay, volumes, SandP,bullArray,VOLDOWN,DAYSLOWVOLUME)) >= 0){
					Base pole = new FlagPole(i,pivot,i);
					list.add(pole);
					da.baseBlocker(i, pivot, baseBlocks);
					return true;
				}					
		}
		System.out.println("nothing found");
		return false;
	}



}
