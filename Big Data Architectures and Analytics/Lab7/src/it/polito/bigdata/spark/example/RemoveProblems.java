package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.Function;

@SuppressWarnings("serial")
public class RemoveProblems implements Function<String, Boolean> {

	@Override
	public Boolean call(String reading) throws Exception {
		String[] readingElements = reading.split("\t");
		if (readingElements[2].compareTo("0") == 0 && readingElements[2].compareTo("0") == 0)
			return false;
		else if (readingElements[0].compareTo("station")==0)
			return false;
		else
			return true;
		
	}

}
