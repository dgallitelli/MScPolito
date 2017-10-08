package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

@SuppressWarnings("serial")
public class MoreThanOnce implements Function<Tuple2<String, Integer>, Boolean> {

	@Override
	public Boolean call(Tuple2<String, Integer> pair) throws Exception {
		
		if (pair._2() > 1)
			return true;
		else 
			return false;
	}

}
