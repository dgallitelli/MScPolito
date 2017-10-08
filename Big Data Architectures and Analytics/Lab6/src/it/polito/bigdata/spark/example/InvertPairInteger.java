package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

@SuppressWarnings("serial")
public class InvertPairInteger implements PairFunction<Tuple2<String, Integer>, Integer, String> {

	@Override
	public Tuple2<Integer, String> call(Tuple2<String, Integer> pair) throws Exception {
		// TODO Auto-generated method stub
		return new Tuple2<Integer, String>(pair._2(), pair._1());
	}

}
