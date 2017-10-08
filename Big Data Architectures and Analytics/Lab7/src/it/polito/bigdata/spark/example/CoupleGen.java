package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

@SuppressWarnings("serial")
public class CoupleGen implements PairFunction<String, String, Integer> {

	@Override
	public Tuple2<String, Integer> call(String reading) throws Exception {
		String[] readingElements = reading.split("\t");
		
		String stationID = readingElements[0];
		String timestamp = readingElements[1];
		int freeslots = Integer.parseInt(readingElements[3]);
		
		Tuple2<String, Integer> myTuple = 
				new Tuple2<String, Integer>(stationID+"\t"+timestamp, freeslots);
		
		return myTuple;
		
	}

}
