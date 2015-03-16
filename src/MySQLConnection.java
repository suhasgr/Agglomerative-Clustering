import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class MySQLConnection 
{
	private Statement _stmt;
	private Connection _conn;
	
	public MySQLConnection(String dataBase,String username,String password) 
	{
		String JDBCDRIVER = "com.mysql.jdbc.Driver";
		String DB_URL = "jdbc:mysql://localhost/"+dataBase;
		
		_conn = null;
		
		try{
		      
		      Class.forName("com.mysql.jdbc.Driver");
		      //System.out.println("Connecting to database...");
		      _conn = DriverManager.getConnection(DB_URL,username,password);

		      //System.out.println("Creating statement...");
		      _stmt = _conn.createStatement();
		}
		catch(SQLException se)
		   {
		      
		      se.printStackTrace();
		   }catch(Exception e){
		      
		      e.printStackTrace();
		   }
	}
	
	public boolean getData(String sql,ArrayList<tuple> tuples)
	{
		boolean status  = false;
		ResultSet rs=null;
		
		try {
			rs = _stmt.executeQuery(sql);
			
			status = (rs == null)?false:true;
			
			int limitedTo = 10000;
			while(rs.next() && limitedTo-- > 0)
			{
				tuple t = new tuple();
				t.age = rs.getInt(1);
				t.occupation = rs.getInt(2);
				
				t.genre = new HashMap<String,Integer>();
				String gen= rs.getString(3);
				
				for(String S: gen.split("\\|"))
				{
 					if(!t.genre.containsKey(S))
						t.genre.put(S,1);
					else
						t.genre.put(S, t.genre.get(S) + 1);
				}
				
				t.rating = rs.getDouble(4);
				t.totalTuples = 1;
				tuples.add(t);
			}
			rs.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return status;
	}
	
	void stop()
	{
	      try {
			_stmt.close();
			_conn.close();
	      } catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	
};
