package mt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DBSetup
{
	public static void main(String[] args)throws FileNotFoundException, IOException, URISyntaxException, SQLException, ClassNotFoundException
	{
		final int days = 391;
		Connection connection = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			String dbURL = "jdbc:mysql://localhost:3307/moneytree";
			String username = "root";
			String password = "password";
			connection = DriverManager.getConnection(dbURL, username, password);
		}
		catch(ClassNotFoundException e){
			System.out.println("Database driver not found.");
		}
		catch(SQLException e){
			System.out.println("Error loading database driver: " + e.getMessage());
		}

		String query =
			"create table Stockprices (Ranking int not null, Symbol varchar(6), primary key(Ranking));";
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
		for(int x = 1;x < days;++x){    
			query = "alter table Stockprices add column Day" + x + " varchar (20);";
			System.out.println(x + " " + statement.executeUpdate(query));
		}

		query =
			"create table Stockvolumes (Ranking int not null, Symbol varchar(6), primary key(Ranking));";
		statement.executeUpdate(query);
		for(int x = 1;x < days;++x){    
			query = "alter table Stockvolumes add column Day" + x + " varchar (20);";
			System.out.println(x + " " + statement.executeUpdate(query));
		}
		
		query =
			"create table Stocklows (Ranking int not null, Symbol varchar(6), primary key(Ranking));";
		statement.executeUpdate(query);
		for(int x = 1;x < days;++x){    
			query = "alter table Stocklows add column Day" + x + " varchar (20);";
			System.out.println(x + " " + statement.executeUpdate(query));
		}
		statement.close();
	}
}