package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

@SuppressWarnings("serial")
public class PlacemarkerGen implements Function<Tuple2<Integer, Tuple2<Tuple2<String, Double>, String>>, String> {

	@Override
	public String call(Tuple2<Integer, Tuple2<Tuple2<String, Double>, String>> arg0) throws Exception {
		// Need to generate a String for the KML Marker, formatted as such:
				String stationID;
				String timestamp;
				String criticality;
				String coordinates; // TODO Get coordinates from second file 
				
				Integer myStation = arg0._1();
				stationID = myStation.toString();
				
				Tuple2<Tuple2<String, Double>, String> stationInfo = arg0._2();
				Tuple2<String, Double> timeCritTuple = stationInfo._1();
				timestamp = new String(timeCritTuple._1());
				Tuple2<String, String> dayAndHour = TimeStampTool.DayAndHourFromTimestamper(timestamp); 
				String day = dayAndHour._1();
				String hour = dayAndHour._2();
				
				Double crit = timeCritTuple._2();
				criticality = crit.toString();
				
				coordinates = new String(stationInfo._2());
				
				String placemark = new String(
					"<Placemark>"
						+ "<name>"+stationID+"</name>"
						+ "<ExtendedData>"
							+ "<Data name=\"DayWeek\"><value>"+day+"</value></Data>"
							+ "<Data name=\"Hour\"><value>"+hour+"</value></Data>"
							+ "<Data name=\"Criticality\"><value>"+criticality+"</value></Data>"
						+ "</ExtendedData>"
						+ "<Point>"
							+ "<coordinates>"+coordinates+"</coordinates>"
						+ "</Point>"
					+ "</Placemark>");

				return placemark;
	}

}
