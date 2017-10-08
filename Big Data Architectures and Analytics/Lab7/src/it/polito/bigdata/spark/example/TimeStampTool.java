package it.polito.bigdata.spark.example;

import scala.Tuple2;

public class TimeStampTool {

	public static Tuple2<String, String> DayAndHour(String timestamp) {
		
		String[] dayHourTemp = timestamp.split(" ");
		String day = new String(DateTool.DayOfTheWeek(dayHourTemp[0]));
		String hour = HourTool.HourFromTime(dayHourTemp[1]);
		
		Tuple2<String,String> dayHour = new Tuple2<String, String>(day, hour);
		
		return dayHour;
		
	}
	
	public static Tuple2<String,String> DayAndHourFromTimestamper(String timestamp){
		
		String[] dayHourTemp = timestamp.split(" - ");
		
		Tuple2<String,String> dayHour = new Tuple2<String, String>(dayHourTemp[0], dayHourTemp[1]);
		
		return dayHour;		
		
		
	}

}
