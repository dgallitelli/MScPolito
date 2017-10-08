package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

@SuppressWarnings("serial")
public class CriticalityGen implements PairFunction<Tuple2<String, Iterable<Integer>>, String, Double> {

	@Override
	public Tuple2<String, Double> call(Tuple2<String, Iterable<Integer>> myTuple) throws Exception {
		
		int nReading = 0, nReadingZero = 0;
		
		for(Integer slot : myTuple._2()){
			nReading ++;
			if(slot == 0)
				nReadingZero ++;
		}
		
		double crit = (double) nReadingZero / (double) nReading;
		
		return new Tuple2<String, Double>(myTuple._1(), crit);
	}

}
