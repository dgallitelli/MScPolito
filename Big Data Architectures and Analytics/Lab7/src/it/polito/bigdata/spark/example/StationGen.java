package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

@SuppressWarnings("serial")
public class StationGen implements PairFunction<Tuple2<String, Double>, Integer, Tuple2<String, Double>> {

	@Override
	public Tuple2<Integer, Tuple2<String, Double>> call(Tuple2<String, Double> reading) throws Exception {
		
		// Key has format: [stationID]\t[timestamp]
		String[] myKey = reading._1().split("\t");
		int stationID = Integer.parseInt(myKey[0]);
		String timestamp = myKey[1];	//Already in "day - hour" format!
		Double criticality = reading._2();
		
		return new Tuple2<Integer, Tuple2<String, Double>>(stationID, new Tuple2<String, Double>(timestamp, criticality));
	}

}
