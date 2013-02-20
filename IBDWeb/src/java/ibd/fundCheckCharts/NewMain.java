/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.fundCheckCharts;

import java.util.Vector;
import ibd.fundCheckCharts.GetFundData;

/**
 *
 * @author Aaron
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	Vector gains=new Vector();
        gains=GetFundData.getData("nflx");// TODO code application logic here
	System.out.println(gains);
    }

}
