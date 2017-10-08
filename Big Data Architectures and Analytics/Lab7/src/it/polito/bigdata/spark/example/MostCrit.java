package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

@SuppressWarnings("serial")
public class MostCrit
		implements PairFunction<Tuple2<Integer, Iterable<Tuple2<String, Double>>>, Integer, Tuple2<String, Double>> {

	@Override
	public Tuple2<Integer, Tuple2<String, Double>> call(Tuple2<Integer, Iterable<Tuple2<String, Double>>> arg0)
			throws Exception {

		Tuple2<String,Double> mostCritPair		= new Tuple2<String,Double>("", (double) 0);
		
		for (Tuple2<String, Double> pair : arg0._2()){
			
			// Step 1: compare criticalities of current couple with highest criticality
			//			and the new pair to be analyzed
			
			if (mostCritPair._2().compareTo(pair._2()) < 0){
				// New MostCritPair found!
				// mostCritPair.copy(pair._1(), pair._2());
				mostCritPair = pair;
			} else if (mostCritPair._2().compareTo(pair._2()) == 0) {
				// The pair just read has the same criticality than our mostCritPair
				
				String timestamp = new String(pair._1());
				Tuple2<String,String> dayAndHour = TimeStampTool.DayAndHourFromTimestamper(timestamp); 
				String dayPair = new String(dayAndHour._1());
				int hourPair = Integer.parseInt(dayAndHour._2());
				
				// Use new class TimeStampTool to get day and hour
				String mostCritTimestamp = new String(mostCritPair._1());
				//Tuple2<String, String> mostCritDayAndHour = TimeStampTool.DayAndHour(mostCritTimestamp);
				Tuple2<String, String> mostCritDayAndHour = TimeStampTool.DayAndHourFromTimestamper(mostCritTimestamp);
				String dayMostCrit = mostCritDayAndHour._1();
				int hourMostCrit = Integer.parseInt(mostCritDayAndHour._2());				
				
				if (hourPair < hourMostCrit ){
					// New MostCritPair found!
					// mostCritPair.copy(pair._1(), pair._2());
					mostCritPair = pair;
				} else if (hourPair == hourMostCrit ){
					// The pair just read has the same hour && criticality than our mostCritPair
					// Check lexicographically the timestamp - take the "earliest" day 
					
					if (dayPair.compareTo(dayMostCrit) < 0){
						// New MostCritPair found!
						//mostCritPair.copy(pair._1(), pair._2());
						mostCritPair = pair;
					}
				}
			}
		}
		
		Tuple2<Integer, Tuple2<String, Double>> finalPair = 
				new Tuple2<Integer, Tuple2<String, Double>>(
						arg0._1(), // StationID
						new Tuple2<String,Double>(
								mostCritPair._1(), 	// Timestamp
								mostCritPair._2() 	// Criticality
							)
						); 
		
		
		return finalPair;
	}

}
