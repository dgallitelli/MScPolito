package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

@SuppressWarnings("serial")
public class CoordGen implements PairFunction<String, Integer, String> {

	@Override
	public Tuple2<Integer, String> call(String arg0) throws Exception {

		Tuple2<Integer, String> myCoords;
		
		String[] coordSplit = arg0.split("\t");
		
		int stationID = Integer.parseInt(coordSplit[0]);
		String coords = new String(coordSplit[1]+", "+coordSplit[2]);
		
		myCoords = new Tuple2<Integer, String>(stationID, coords);
		
		return myCoords;
		
	}

}
