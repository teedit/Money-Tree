/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.buySellLineHistChart;

import ibd.fundCheckCharts.GetFundData;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author Aaron
 */
public class ServletHistChartGenerator extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
//        PrintWriter out = response.getWriter();
//        try {
//         	String timeString = request.getParameter("time");
//	int time = Integer.valueOf(timeString);
	String fund = request.getParameter("fund");
	Vector data = GetChartData.getData(fund);

	JFreeChart chart = null;

	OutputStream out = response.getOutputStream();
	try {
	    chart = ReturnHistChart.returnChart(data, fund);

	    if (chart != null) {
		response.setContentType("image/png");
		ChartUtilities.writeChartAsPNG(out, chart, 400, 300);
		out.flush();
		out.close();
	    }
//			    request.setAttribute("error",ex);
//	    request.setAttribute("fund",fund);
//	    RequestDispatcher rd = request.getRequestDispatcher("/fundDataError.jsp");
//	    rd.forward(request, response);

	} catch (Exception ex) {
	    System.err.println(ex.toString());
	    Logger.getLogger(GetFundData.class.getName()).log(Level.SEVERE, null, ex);
	    request.setAttribute("error",ex);
	    request.setAttribute("fund",fund);
	    RequestDispatcher rd = request.getRequestDispatcher("/fundDataError.jsp");
	    rd.forward(request, response);
        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
