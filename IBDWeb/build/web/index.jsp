<%-- 
    Document   : newjsp
    Created on : Aug 25, 2010, 7:00:06 PM
    Author     : Aaron
--%>

<%@page import="ibd.threads.ApplicationThread"%>
<%@page import="ibd.classes.VarDow"%>
<%@page import="ibd.classes.VarNasdaq"%>
<%@page import="ibd.classes.VarSP500"%>
<%@page import="java.sql.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Vector"%>
<%@page import="ibd.classes.Output"%>
<%@page import="ibd.classes.Variables"%>
<%@page import="ibd.marketHistCharts.ServletChartGenerator"%>
<%@page import="ibd.fundCheckCharts.ServletOutputImage"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%
	    ApplicationThread.startThread();

	    Output outputSP500 = VarSP500.currentSP500;
	    Output outputNasdaq = VarNasdaq.currentNasdaq;
	    Output outputDow = VarDow.currentDow;

            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, Locale.US);
			java.util.Date today = new java.util.Date();
			String dateOut = dateFormatter.format(today);
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>MarketPredictor.com</title>
    </head>

    <body>
        <h1>MarketPredictor.com</h1>
	<h2><%=dateOut%></h2>


<%
	    if (outputDow == null || outputNasdaq == null || outputSP500 == null) {
%>
	<h1>Data is being gathered.  Please try back in a few minutes.</h1>

<%
		return;
	    }
%>

	<TABLE width="100%" BORDER=15 BGCOLOR="white">
	    <TH>Fund Checker</TH>
	    <TR>
		<TD>
		<FORM ACTION="servlet/ServletOutputImage" METHOD=GET target="fundChartFrame">
			Enter your fund symbol
			<INPUT TYPE="text" NAME="fund" VALUE="FUND" CHECKED>
			<INPUT TYPE="submit" VALUE="Submit">
		    </FORM>
		    <br>
		    <!--an iframe is needed so the form stays visible even with a chart displayed.Otherwise the form would get written over-->
		    <IFRAME marginheight="10" scrolling="no" height="350" NAME="fundChartFrame"
			WIDTH=100% marginwidth="10" frameborder="0">
		    </IFRAME>
		</TD>
	    </TR></TABLE>
<br>
	<table width="100%" bgcolor="pink" border="15">
	    <th>Today's Market Data</th>
	    <tr><td width="35%"><h3>Data for <%=outputDow.lastDataDate%></h3></td><td><h3>S&P500</h3></td><td><h3>DOW</h3></td><td><h3>NASDAQ</h3></td></tr>
	    <tr><td><h3>Market Condition (BUY or SELL)</h3></td><td><h3><%=outputSP500.buyOrSellToday%></h3></td><td><h3><%=outputDow.buyOrSellToday%></h3></td><td><h3><%=outputNasdaq.buyOrSellToday%></h3></td></tr>
	    <tr><td>Last Buy Day</td><td><%=outputSP500.lastBuyDate%></td><td><%=outputDow.lastBuyDate%></td><td><%=outputNasdaq.lastBuyDate%></td></tr>
	    <tr><td>Last Sell Day</td><td><%=outputSP500.lastSellDate%></td><td><%=outputDow.lastSellDate%></td><td><%=outputNasdaq.lastSellDate%></td></tr>
	    <tr><td>Days Since Potential Market Bottom</td><td><%=outputSP500.rallyDaysToday%></td><td><%=outputDow.rallyDaysToday%></td><td><%=outputNasdaq.rallyDaysToday%></td></tr>
	    <tr><td>Percentage to Top of Market</td><td><%=outputSP500.dDaysPerc%>%</td><td><%=outputDow.dDaysPerc%>%</td><td><%=outputNasdaq.dDaysPerc%>%</td></tr>
	</table>
	<br>
	<table width="100%" bgcolor="red" border="15">
	    <th>Historical MarketPredictor.com Performance</th>
	    <tr><td><IMG align="center" alt="Can't get Chart" SRC="servlet/ServletChartGenerator?time=5" BORDER="1" WIDTH="23%" HEIGHT="300"/>
	<IMG align="center" alt="Can't get Chart" SRC="servlet/ServletChartGenerator?time=10" BORDER="1" WIDTH="23%" HEIGHT="300"/>
	<IMG align="center" alt="Can't get Chart" SRC="servlet/ServletChartGenerator?time=20" BORDER="1" WIDTH="23%" HEIGHT="300"/>
	<IMG align="center" alt="Can't get Chart" SRC="servlet/ServletChartGenerator?time=30" BORDER="1" WIDTH="23%" HEIGHT="300"/>
	</td></tr></table>
	<br>
	<table width="100%" border="15" bgcolor="white" >
	    <tr><td width="35%"><h3>Historical Performance</h3></td><td><h3>S&P500</h3></td><td><h3>DOW</h3></td><td><h3>NASDAQ</h3></td></tr>
	    <tr> <td>5 Year   Market Gain/Loss<br>MarketPredictor Gain/Loss</td><td><%=outputSP500.histReturns[0][0]%><br><%=outputSP500.histReturns[0][1]%></td><td><%=outputDow.histReturns[0][0]%><br><%=outputDow.histReturns[0][1]%></td><td><%=outputNasdaq.histReturns[0][0]%><br><%=outputNasdaq.histReturns[0][1]%></td></tr>
	    <tr><td>10 Year   Market Gain/Loss<br>MarketPredictor Gain/Loss</td><td><%=outputSP500.histReturns[1][0]%><br><%=outputSP500.histReturns[1][1]%></td><td><%=outputDow.histReturns[1][0]%><br><%=outputDow.histReturns[1][1]%></td><td><%=outputNasdaq.histReturns[1][0]%><br><%=outputNasdaq.histReturns[1][1]%></td></tr>
	    <tr><td>15 Year   Market Gain/Loss<br>MarketPredictor Gain/Loss</td><td><%=outputSP500.histReturns[2][0]%><br><%=outputSP500.histReturns[2][1]%></td><td><%=outputDow.histReturns[2][0]%><br><%=outputDow.histReturns[2][1]%></td><td><%=outputNasdaq.histReturns[2][0]%><br><%=outputNasdaq.histReturns[2][1]%></td></tr>
	    <tr><td>20 Year   Market Gain/Loss<br>MarketPredictor Gain/Loss</td><td><%=outputSP500.histReturns[3][0]%><br><%=outputSP500.histReturns[3][1]%></td><td><%=outputDow.histReturns[3][0]%><br><%=outputDow.histReturns[3][1]%></td><td><%=outputNasdaq.histReturns[3][0]%><br><%=outputNasdaq.histReturns[3][1]%></td></tr>
	    <tr><td>30 Year   Market Gain/Loss<br>MarketPredictor Gain/Loss</td><td><%=outputSP500.histReturns[4][0]%><br><%=outputSP500.histReturns[4][1]%></td><td><%=outputDow.histReturns[4][0]%><br><%=outputDow.histReturns[4][1]%></td><td><%=outputNasdaq.histReturns[4][0]%><br><%=outputNasdaq.histReturns[4][1]%></td></tr>
	    <tr><td>40 Year   Market Gain/Loss<br>MarketPredictor Gain/Loss</td><td><%=outputSP500.histReturns[5][0]%><br><%=outputSP500.histReturns[5][1]%></td><td><%=outputDow.histReturns[5][0]%><br><%=outputDow.histReturns[5][1]%></td><td><%=outputNasdaq.histReturns[5][0]%><br><%=outputNasdaq.histReturns[5][1]%></td></tr>
	    <tr><td>50 Year   Market Gain/Loss<br>MarketPredictor Gain/Loss</td><td><%=outputSP500.histReturns[6][0]%><br><%=outputSP500.histReturns[6][1]%></td><td><%=outputDow.histReturns[6][0]%><br><%=outputDow.histReturns[6][1]%></td><td><%=outputNasdaq.histReturns[6][0]%><br><%=outputNasdaq.histReturns[6][1]%></td></tr>
	    <tr><td>MarketPredictor Buy and Sell Days </td><td>
		    <table width="100%" border="0">
			<tr><td>Buy Days</td><td>Sell Days</td></tr>
			<% int i = outputSP500.buyDates.length - 1;
				    while (i >= 0) {
					try {%>
			<tr><td><%=outputSP500.buyDates[i]%></td><td><%=outputSP500.sellDates[i]%></td></tr>
			<% i--;
					} catch (IndexOutOfBoundsException e) {
					    break;
					}
				    }%>
		    </table>
		</td><td>
		    <table width="100%" border="0">
			<tr><td>Buy Days</td><td>Sell Days</td></tr>
			<% i = outputDow.buyDates.length - 1;
				    while (i >= 0) {
					try {%>
			<tr><td><%=outputDow.buyDates[i]%></td><td><%=outputDow.sellDates[i]%></td></tr>
			<% i--;
					} catch (IndexOutOfBoundsException e) {
					    break;
					}
				    }%>
		    </table>
		</td><td>
		    <table width="100%" border="0">
			<tr><td>Buy Days</td><td>Sell Days</td></tr>
			<%i = outputNasdaq.buyDates.length - 1;
				    while (i >= 0) {
					try {%>
			<tr><td><%=outputNasdaq.buyDates[i]%></td><td><%=outputNasdaq.sellDates[i]%></td></tr>
			<% i--;
					} catch (IndexOutOfBoundsException e) {
					    break;
					}
				    }%>
		    </table>
		</td>
	</table>
	<br>


	<table width="100%" bgcolor="red" border="15">
	    <th>Historical MarketPredictor.com Buy and Sell Dates</th>
	    <tr><td>
		<IMG align="center" alt="Can't get Chart" SRC="ServletHistChartGenerator?fund=SP500" BORDER="1" WIDTH="100%" HEIGHT="300"/>
		<IMG align="center" alt="Can't get Chart" SRC="ServletHistChartGenerator?fund=DOW" BORDER="1" WIDTH="100%" HEIGHT="300"/>
		<IMG align="center" alt="Can't get Chart" SRC="ServletHistChartGenerator?fund=NAS" BORDER="1" WIDTH="100%" HEIGHT="300"/>
	</td></tr></table>
	<br>


	<table width="100%" border="15" bgcolor="white" >
	    <th><h3>Learning Center</h3></th>
	</table>
    </body>
</html>