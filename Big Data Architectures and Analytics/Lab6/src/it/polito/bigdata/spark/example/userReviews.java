package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

@SuppressWarnings("serial")
public class userReviews implements PairFunction<String, String, String> {

	@Override
	public Tuple2<String, String> call(String review) throws Exception {
		
		String user;
		String product;
		
		// Split on comma, then save the <K,V> pair to a Tuple2 to be returned
		String[] fields = review.split(",");
		user = fields[2];
		product = fields[1];		
		return new Tuple2<String, String>(user, product);
		
	}
	
	
	
}
