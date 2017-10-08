package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

@SuppressWarnings("serial")
public class TimeStamper implements Function<String, String> {

	@Override
	public String call(String reading) throws Exception {
		
		String[] readingElements = reading.split("\t");
		
		Tuple2<String,String> dayAndHour = TimeStampTool.DayAndHour(readingElements[1]);
		
		String myDay = new String(dayAndHour._1());
		String myHour = new String(dayAndHour._2());
		
		String myString = myDay + " - " + myHour;
		
		return new String(readingElements[0]+"\t"+myString+"\t"+readingElements[2]+"\t"+readingElements[3]);
		
	}

}
