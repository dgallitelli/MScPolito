package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.Function;

@SuppressWarnings("serial")
public class SkipFirstStation implements Function<String, Boolean> {

	@Override
	public Boolean call(String arg0) throws Exception {
		
		String[] stationInfo = arg0.split("\t");
		if (stationInfo[0].compareTo("id") != 0)
			return true;
		else 
			return false;
		
	}

}
