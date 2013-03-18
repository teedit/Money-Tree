package mt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDB
{
	public static synchronized boolean isMatch(Connection connection,
			String sym) throws SQLException
			{
		String query = "SELECT symbol FROM Stocks " +
		"WHERE symbol = '"
		+ sym + "'";
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(query);
		boolean stockExists = results.next();
		results.close();
		statement.close();
		return stockExists;
			}

	public static synchronized void addRecord(Connection connection, int ranking, String sym, float[] priceLowData, float[] priceData,
			double[] volumeData) throws SQLException{
		String query = "Insert into Stockprices (Ranking, Symbol) " +
		"Values (" + ranking + ", '" + sym + "')"; 
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);			
		for (int n = 0;n < priceData.length;++n){
			query = "UPDATE Stockprices SET Day" + (n + 1) + " = '" + 
			priceData[n] + "'" + " WHERE Ranking = " + ranking; 
			statement.executeUpdate(query);
		}  
		query = "Insert into Stockvolumes " + "(Ranking, Symbol)" + "VALUES (" + 
		ranking + ", '" + sym + "')";
		statement.executeUpdate(query);			
		for (int n = 0;n < volumeData.length;++n){
			query = "UPDATE Stockvolumes " + "SET Day" + (n + 1) + " = '" + 
			volumeData[n] + "'" + "WHERE Ranking = " + ranking; 
			statement.executeUpdate(query);
		}
		query = "Insert into Stocklows " + "(Ranking, Symbol)" + "VALUES (" + 
		ranking + ", '" + sym + "')";
		statement.executeUpdate(query);			
		for (int n = 0;n < volumeData.length;++n){
			query = "UPDATE Stocklows " + "SET Day" + (n + 1) + " = '" + 
			priceLowData[n] + "'" + "WHERE Ranking = " + ranking; 
			statement = connection.createStatement();
			statement.executeUpdate(query);
		}
		statement.close();
	}

	public static synchronized int deleteRecord(Connection connection,
			String priceTable, String volumeTable, String symbol) throws SQLException{
		String query =
			"DELETE FROM " + priceTable +
			" WHERE Symbol = '"
			+ symbol+ "'";
		Statement statement = connection.createStatement();
		int status = statement.executeUpdate(query);
		query =
			"DELETE FROM " + volumeTable +
			" WHERE Symbol = '"
			+ symbol + "'";
		statement = connection.createStatement();
		status = statement.executeUpdate(query);
		statement.close();
		return status;
	}

	public static synchronized int deleteAll(Connection connection,String priceTable, String volumeTable) throws SQLException{
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
