package mt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class PatternBuilder {
	public static void main(String[] args)throws SQLException{
		PatternBuilder pb = new PatternBuilder();
		double[][] array = {
				{5,40,34,0,65,45,0,30,50,2,20,110,-1,6,90,5,//1. good flagpole
					3,40,34,0,95,1000,0,20,400,0},
				{4,65,45,0,30,50,1,20,110,-1,6,90,5,//2. short pole
					2,95,1000,0,20,400,0},
				{4,65,45,0,30,50,3,20,110,-1,6,90,5,//3. tall pole
					2,95,1000,0,20,400,0},
				{6,35,45,0,1,60,0,29,45,0,30,50,2,20,110,-1,6,90,5,//4.highInPrePeriod
					2,95,1000,0,20,400,0},
				{4,50,45,0,30,50,2,20,110,-1,6,90,5,//5. short prePeriod
					2,95,1000,0,20,400,0},
				{4,65,45,0,19,50,3,20,110,-1,6,90,5,//6.  rise too fast
					2,84,1000,0,20,400,0},
				{4,65,45,0,41,50,2,20,110,-1,6,90,5,//7. rise too slow
					2,106,1000,0,20,400,0},
				{6,65,45,0,15,50,2,1,100,0,14,82,2,20,110,-1,6,90,5,//8. pole deviates high
					2,95,1000,0,20,400,0},
				{6,65,45,0,15,50,2,1,60,0,14,82,2,20,110,-1,6,90,5,//9.  pole deviates low
					2,95,1000,0,20,400,0},
				{6,65,45,0,30,50,2,10,110,-1,1,110,0,9,100,-1,6,90,5,//10.  handleDevHigh
					2,95,1000,0,20,400,0},
				{6,65,45,0,30,50,2,10,110,-1,1,90,0,9,100,-1,6,90,5,//11. handleDevLow
					2,95,1000,0,20,400,0},
				{4,65,45,0,30,50,2,20,110,-0.4,6,90,5,//12. shallow handle
					2,95,1000,0,20,400,0},
				{4,65,45,0,30,50,2,20,110,-2,6,90,5,//13. deep handle
					2,95,1000,0,20,400,0},
				{4,65,45,0,30,50,2,20,110,-1,6,90,5,//14. no volume dry up
					2,95,1000,0,20,800,0},
				{4,65,45,0,30,50,2,20,110,-1,6,90,2,//15. no buy yet
					2,95,1000,0,20,400,0},
				{4,65,45,0,30,50,2,20,110,-1,12,90,1,//16. buy failure
					2,95,1000,0,20,400,0},
				{4,65,45,0,30,50,2,20,110,-1,8,90,5,//17. too late
					2,95,1000,0,20,400,0},
				{7,20,75,0,88,200,1,15,285,-6,12,195,0,15,195,5.5,10,278,-5,7,220,10,//18. StandardCWH
					5,123,1000,0,12,450,0,15,1000,0,10,450,0,7,1000,0}
				
				};
		String[] names = {"standardPole","shortPole","tallPole","highInPrePeriod","shortPrePeriod",
				"riseTooFast","riseTooSlow","poleDevHigh","poleDevLow","handleDevHigh",
				"handleDevLow","shallowHandle","deepHandle","noVolDryUp","noBuyYet","buyFailure",
				"tooLate",
				"StandardCWH"};
		pb.buildPatterns(array,names);
	}
	public void buildPatterns(double[][] array, String[] names)throws SQLException{

		Connection con = DataRetriever.getConnection();
		Statement statement;
		String query;
		//UserDB.deleteAll(con, "Testprices", "Testvolumes");
		for(int i = 0;i < array.length;++i){
			boolean skip = false;
			try{
				query = "Insert into Testprices (Ranking, Symbol) " +
				"Values ("+ (i+1) + ", '" + names[i] + "')"; 
				statement = con.createStatement();
				statement.executeUpdate(query);
			}catch (MySQLIntegrityConstraintViolationException e){
				System.out.println("Test case " + names[i] + " already entered in database");
				skip = true;
			}
			int arrayIndex = 1;
			int dbIndex = 1;
			if(!skip)
				for(int j = 0;j < array[i][0];++j){
					double currentValue = (int)array[i][arrayIndex + 1];
					int endPoint = dbIndex + (int)array[i][arrayIndex];
					for (int k = dbIndex;k < endPoint;++k){
						query = "UPDATE Testprices SET Day" + k + " = " + currentValue + " WHERE Ranking = " + (i+1); 
						currentValue += array[i][arrayIndex + 2];
						System.out.println(++dbIndex);
						statement = con.createStatement();
						statement.executeUpdate(query);
					}
					arrayIndex += 3;
				}
			query = "Insert into Testvolumes (Ranking, Symbol) " +
			"Values (" + (i+1) + ", '" + names[i] + "')"; 
			statement = con.createStatement();
			try{
				statement.executeUpdate(query);
			} catch (MySQLIntegrityConstraintViolationException e){}
			dbIndex = 1;
			int volumeSteps = (int)array[i][arrayIndex];
			++arrayIndex;
			if(!skip)
				for(int j = 0;j < volumeSteps;++j){
					int currentValue = (int)array[i][arrayIndex + 1];
					int endPoint = dbIndex + (int)array[i][arrayIndex];
					for (int k = dbIndex;k < endPoint;++k){
						query = "UPDATE Testvolumes SET Day" + k + " = " + currentValue + " WHERE Ranking = " + (i+1); 
						currentValue += array[i][arrayIndex + 2];
						System.out.println(++dbIndex);
						statement = con.createStatement();
						statement.executeUpdate(query);
					}
					arrayIndex += 3;
				}
		}
	}
}
