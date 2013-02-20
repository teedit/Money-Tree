package ibd.classes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.Date;
import java.util.*;

public class MarketDB {

    /**
     *
     * @return
     */
    public static Connection getConnection() {
	Connection connection = null;
	try {
	    Class.forName("com.mysql.jdbc.Driver");
//	    String dbURL = "jdbc:mysql://209.204.83.249/funds";
	    String dbURL = "jdbc:mysql://localhost:3306/moneytree";
//	    "jdbc:mysql://"+dbhost+"/"+dbname+"?user="+user+"&password="+pass;
	    String username = "user";
//	    String username = "root";
	    String password = "password";
	    connection = DriverManager.getConnection(dbURL, username, password);
	    System.out.println("Connection established");
	} catch (ClassNotFoundException e) {
	    System.out.println("Database driver not found.");
	} catch (SQLException e) {
	    System.out.println("Error loading database driver: " + e.getMessage());
	}
	return connection;
    }

    /**
     *
     * @param connection
     * @param sym
     * @param date 
     * @return
     * @throws SQLException
     */
    public static synchronized boolean isMatch(Connection connection, String sym, Date date) throws SQLException {

	String query = "SELECT * FROM `" + sym + "` WHERE date='" + date + "'";
	Statement statement = connection.createStatement();
	ResultSet results = statement.executeQuery(query);
	boolean datePresent = results.next();
	results.close();
	statement.close();
	return datePresent;
    }

    /**
     *
     * @param connection
     * @param sym
     * @param startDate
     * @param endDate
     * @return
     * @throws SQLException
     */
    public static synchronized Data getRecord(Connection connection, String sym, Date startDate, Date endDate) throws SQLException {

	String query;
	query = "SELECT * FROM `" + sym + "` WHERE date>='" + startDate + "' AND date<='" + endDate + "' ORDER BY date DESC";
	//System.out.println(query);
	Statement statement = connection.createStatement();
	ResultSet rs = statement.executeQuery(query);
	System.out.println("executeQuery successful for "+sym+ " where startDate="+startDate+" and endDate="+endDate);

	ArrayList<Date> dates1 = new ArrayList<Date>();
	ArrayList<Float> opens1 = new ArrayList<Float>();
	ArrayList<Float> highs1 = new ArrayList<Float>();
	ArrayList<Float> lows1 = new ArrayList<Float>();
	ArrayList<Float> closes1 = new ArrayList<Float>();
	ArrayList<Long> volumes1 = new ArrayList<Long>();

	rs.first();

	while (rs.isAfterLast() == false) {
	    dates1.add(rs.getDate(1));
	    opens1.add(rs.getFloat(2));
	    highs1.add(rs.getFloat(3));
	    lows1.add(rs.getFloat(4));
	    closes1.add(rs.getFloat(5));
	    volumes1.add(rs.getLong(6));
	    rs.next();
	}

	return new Data(dates1, opens1, highs1, lows1, closes1, volumes1);
    }

    /**
     *
     * @param connection
     * @param sym
     * @param date
     * @param open
     * @param high
     * @param low
     * @param close
     * @param volume
     * @return
     * @throws SQLException
     */
    public static synchronized int addRecord(Connection connection, String sym, Date[] date, float[] open,
	    float[] high, float[] low, float[] close, long[] volume) throws SQLException {

	//System.out.println(date[0]);
	Statement statement = null;
	int status = 0;
	String query = "Insert into `" + sym + "` (Date,Open,High,Low,Close,Volume) Values";
	for (int n = 0; n < date.length ; n++) {
	    query = query + "('" + date[n] + "','" + open[n] + "','" + high[n] + "','" + low[n] + "','" + close[n] + "','" + volume[n] + "')";
	    if (n<date.length-1){
		query=query+",";
	    }
//	    }else if (n==date.length-1){
//		query=query+";";
//	    }
	}
	statement = connection.createStatement();
	status = statement.executeUpdate(query);


	statement.close();
	return status;
    }

    /**
     *
     * @param connection
     * @param priceTable
     * @param volumeTable
     * @param symbol
     * @return
     * @throws SQLException
     */
    public static synchronized int deleteRecord(Connection connection,
	    String priceTable, String volumeTable, String symbol) throws SQLException {
	String query =
		"DELETE FROM " + priceTable +
		" WHERE Symbol = '" + symbol + "'";
	Statement statement = connection.createStatement();
	int status = statement.executeUpdate(query);
	query =
		"DELETE FROM " + volumeTable +
		" WHERE Symbol = '" + symbol + "'";
	statement = connection.createStatement();
	status = statement.executeUpdate(query);
	statement.close();
	return status;
    }

    /**
     *
     * @param connection
     * @param priceTable
     * @param volumeTable
     * @return
     * @throws SQLException
     */
    public static synchronized int deleteAll(Connection connection, String priceTable, String volumeTable) throws SQLException {
	String query = null;
	Statement statement = null;
	int status = 0;

	query = "DELETE FROM " + priceTable + " WHERE ranking >= 0";
	statement = connection.createStatement();
	status = statement.executeUpdate(query);
	query = "DELETE FROM " + volumeTable + " WHERE ranking >= 0";
	statement = connection.createStatement();
	status = statement.executeUpdate(query);

	statement.close();
	return status;
    }
}
