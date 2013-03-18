package mt;

import java.util.ArrayList;

public class Data {
		private String name;
		private float[] priceData;
		private double[] volumeData;
		private float[] priceLowData;
		public Data(String nam,ArrayList<Float> prices,ArrayList<Double> volumes, ArrayList<Float> priceLows){
			nam = name;
			priceData = new float[prices.size()];
			volumeData = new double[volumes.size()];
			priceLowData = new float[priceLows.size()];
			for(int i = 0;i < prices.size();++i){
				priceData[i] = prices.get(i);
				volumeData[i] = volumes.get(i);
				priceLowData[i] = priceLows.get(i);
			}
		}
		public float[] getPriceData(){
			return priceData;
		}
		public double[] getVolumeData(){
			return volumeData;
		}
		public float[] getPriceLowsData(){
			return priceLowData;
		}
		public String getName(){
			return name;
		}
	
}
