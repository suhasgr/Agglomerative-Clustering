import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


class tuple
{
	public int age;
	public int occupation;
	public HashMap<String, Integer> genre;
	public double rating;
	tuple llink;
	tuple rlink;
	int totalTuples;
};





public class ReadData 
{
	public static double normalizedAgeHop(int age1,int age2)
	{	
		if(age1 < age2)
		{
			int t = age1;
			age1 = age2;
			age2 = t;
		}
		
		int hop = 0;
		while(age1 < age2)
		{
			hop++;
			switch(age1)
			{
			case 1: age1 = 18;
			break;
			case 18: age1 = 25;
			break;
			case 25: age1 = 35;
			break;
			case 35: age1 = 45;
			break;
			case 45: age1 = 50;
			break;
			case 56: age1 = 56;
			break;
			}
		}
		return hop/6;
	}
	
	public static void agglomerative(ArrayList<tuple> tuples)
	{
		int size = tuples.size()-1;
		for(int i = 0; i < size; ++i)
		{
			//Getting the tuples with minimum distance and getting the distance in terms of Age and occupation
			
			int numberOfTuples = tuples.size();
			double maxCosine = 0.0;
			tuple left = null,right = null;
			
			for(int j = 0; j< numberOfTuples;++j)
			{
				tuple t1 = tuples.get(j);
				for(int k = j+1; k< numberOfTuples;++k)
				{
					tuple t2 = tuples.get(k);
					//double distance = (normalizedAgeHop(t1.age,t2.age) + ((t1.occupation == t2.occupation)?0:1))/2;
					double cosine = ((t1.age * t2.age) + (t1.occupation * t2.occupation))/
									(Math.sqrt(t1.age*t1.age + t1.occupation * t1.occupation)*Math.sqrt(t2.age*t2.age + t2.occupation * t2.occupation));
					if(cosine > maxCosine)
					{
						maxCosine = cosine;
						left = t1;
						right = t2;
					}
				}
			}
			
			
			tuple clusterParent = new tuple();
			clusterParent.totalTuples = left.totalTuples + right.totalTuples;
			clusterParent.age = (left.age * left.totalTuples + right.totalTuples*right.age)/(left.totalTuples + right.totalTuples);
			
			clusterParent.occupation = left.totalTuples>right.totalTuples?left.occupation : right.occupation;

			clusterParent.llink = new tuple();
			clusterParent.rlink = new tuple();
			clusterParent.llink = left;
			clusterParent.rlink = right;
			
			String[] S = left.genre.keySet().toArray(new String[left.genre.size()]);
			clusterParent.genre = new HashMap<String,Integer>();
			for(int loc =0 ;loc< S.length; ++loc)
			{
				if(!clusterParent.genre.containsKey(S[loc]))
					clusterParent.genre.put(S[loc],left.genre.get(S[loc]));
				else
					clusterParent.genre.put(S[loc], clusterParent.genre.get(S[loc]) + left.genre.get(S[loc]));
			}

			String[] S1 = right.genre.keySet().toArray(new String[right.genre.size()]);
			for(int loc =0 ;loc< S1.length; ++loc)
			{
				if(!clusterParent.genre.containsKey(S1[loc]))
					clusterParent.genre.put(S1[loc],right.genre.get(S1[loc]));
				else
					clusterParent.genre.put(S1[loc], clusterParent.genre.get(S1[loc]) + right.genre.get(S1[loc]));
			}
			
			clusterParent.rating = (left.rating * left.totalTuples + right.rating * right.totalTuples)/(left.totalTuples + right.totalTuples);
			
			tuples.remove(left);
			tuples.remove(right);
			
			tuples.add(clusterParent);
//			System.out.println("Iteration = "+i+"  size of tuples = "+tuples.size());
		}
		
		System.out.println("Done");
	}
	
	public static void SimilarityGenresandRating(ArrayList<tuple> tuples)
	{
		//Travelling 3 levels for checking the similarity
		
		Queue q = new LinkedList<tuple>();
		q.add(tuples.get(0));
		
		int level = 1;
		while(!q.isEmpty())
		{
			tuple t= (tuple) q.remove();
			System.out.println("*****************At Level = "+Math.ceil(Math.log(level))+" Age  ="+t.age+" Occupation = "+t.occupation);
			System.out.println("Genres of movie they watch will be in the ratio of =");
			
			int totalCount = 0;
			
			Iterator it = t.genre.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry pairs = (Map.Entry)it.next();
				
				totalCount += (int) pairs.getValue();
			}
			it = t.genre.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry pairs = (Map.Entry)it.next();
				
				System.out.println("Ratio of watching   "+pairs.getKey()+" movie is = "+(Double.parseDouble(pairs.getValue().toString())/totalCount));
			}
			
			System.out.println("Average rating of the users in this age group id = "+t.rating);
			if(level<8)
			{
				++level;
				if(t.llink != null)
					q.add(t.llink);
				if(t.rlink != null)
					q.add(t.rlink);
			}
		}
	}
	
	public static void main(String[] args)
	{
		String sql;
	    sql = "SELECT u.age,u.occupation,m.genre,r.rating FROM users1 u, movies1 m, ratings1 r where " +
	      		"u.userid = r.userid and m.movieid = r.movieid";
	    ArrayList<tuple> tuples = new ArrayList<tuple>();
	    
	    MySQLConnection mysqlConnection = new MySQLConnection("suhas_db", "root", "root");
	    boolean status = mysqlConnection.getData(sql, tuples);
		
		if(status == false)
		{
			System.out.println("Wrong in data fetch");
			System.exit(0);
		}
		else
		{
			System.out.println("Total Tuple Strength = "+tuples.size());
		}
		
		agglomerative(tuples);
		
		mysqlConnection.stop();
		
		SimilarityGenresandRating(tuples);
		
	}

}
