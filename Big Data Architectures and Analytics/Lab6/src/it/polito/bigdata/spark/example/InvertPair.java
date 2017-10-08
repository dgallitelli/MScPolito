package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

@SuppressWarnings("serial")
public class InvertPair implements PairFunction<Tuple2<String, String>, String, String> {

	@Override
	public Tuple2<String, String> call(Tuple2<String, String> pair) throws Exception {
		// TODO Auto-generated method stub
		return new Tuple2<String, String>(pair._2(), pair._1());
	}

}
