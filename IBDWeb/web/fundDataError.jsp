<%-- 
    Document   : fundDataError
    Created on : Oct 18, 2010, 9:29:44 PM
    Author     : Aaron
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%
	    Exception err = (Exception) request.getAttribute("error");
	    String error = err.toString();
	    String fund = request.getParameter("fund");
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Can't Find Fund</title>
    </head>
    <body>
        <h3>
	    <!--	    <FORM ACTION="CheckFund" METHOD=GET>-->
	    Can't find fund <%=fund%>.  Enter a valid fund symbol.
	</h3>
	    error <%=error%>
    </body>
</html>
