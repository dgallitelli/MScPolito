package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

@SuppressWarnings("serial")
public class InvertPairInteger2 implements PairFunction<Tuple2<Integer, String>, String, Integer> {

	@Override
	public Tuple2<String, Integer> call(Tuple2<Integer, String> pair) throws Exception {
		// TODO Auto-generated method stub
		return new Tuple2<String, Integer>(pair._2(), pair._1());
	}

}
