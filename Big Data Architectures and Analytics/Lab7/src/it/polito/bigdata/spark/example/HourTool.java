package it.polito.bigdata.spark.example;

public class HourTool {

	public static String HourFromTime(String timestamp) {
		
		String[] hourSplit = timestamp.split(":");
		
		return hourSplit[0];
	}
}
