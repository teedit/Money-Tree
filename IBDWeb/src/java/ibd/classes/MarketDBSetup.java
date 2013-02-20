package ibd.classes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MarketDBSetup {

    public static void main(String[] args) throws FileNotFoundException, IOException, URISyntaxException, SQLException, ClassNotFoundException {

	Connection connection = MarketDB.getConnection();
       
        Statement statement = null;
        String query;
        int status = 0;
	String[] list = {"`^SML`","`^MID`","`^GSPC`","`^DJI`","`^IXIC`"};

	for(int i=0;i<list.length;i++){
	    query = "create table"+list[i]+" (Date DATE, Open FLOAT(20), High FLOAT(20), Low FLOAT(20), Close FLOAT(20), Volume BIGINT(50), primary key(Date));";
	    statement = connection.createStatement();
	    status = statement.executeUpdate(query);
	}
        statement.close();
        System.out.println(status);
    }
}