/* -------------------------------
 * ServletChartGenerator.java
 * -------------------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 */
package ibd.marketHistCharts;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 * A servlet that returns one of three charts as a PNG image file.  This servlet is
 * referenced in the HTML generated by ServletDemo2.
 * <P>
 * Three different charts can be generated, controlled by the 'type' parameter.  The possible
 * values are 'pie', 'bar' and 'time' (for time series).
 * <P>
 * This class is described in the JFreeChart Developer Guide.
 */
public class ServletChartGenerator extends HttpServlet {

    /**
     * Default constructor.
     */
    public ServletChartGenerator() {
	// nothing required
    }

    /**
     * Process a GET request.
     *
     * @param request  the request.
     * @param response  the response.
     *
     * @throws ServletException if there is a servlet related problem.
     * @throws IOException if there is an I/O problem.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	OutputStream out = response.getOutputStream();
	try {
	    String timeString = request.getParameter("time");
	    int time=Integer.valueOf(timeString);
	    JFreeChart chart = null;
	    chart = BarChart.returnChart(time);
	    if (chart != null) {
		response.setContentType("image/png");
		ChartUtilities.writeChartAsPNG(out, chart, 400, 300);
	    }
	} catch (Exception e) {
	    System.err.println(e.toString());
	} finally {
	    out.close();
	}
    }
}
