package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

@SuppressWarnings("serial")
public class AboveThreshold implements Function<Tuple2<String, Double>, Boolean> {
	
	private Double threshold;
	
	public AboveThreshold(Double arg_threshold) {
		threshold = arg_threshold;
	}

	@Override
	public Boolean call(Tuple2<String, Double> myTuple) throws Exception {
		
		if(myTuple._2().compareTo(threshold) > 0)
			return true;
		else 
			return false;
	}

}
