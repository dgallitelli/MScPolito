package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.Function;

@SuppressWarnings("serial")
public class RemoveNoEval implements Function<String, Boolean> {

	@Override
	public Boolean call(String record) throws Exception {

		String[] fields = record.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		
		if (fields[0].compareTo("Id") == 0)
			return false;
		else {
			double helpD = Double.parseDouble(fields[5]);
			
			if ( helpD == 0 )
				return false;
			else
				return true; 
			
		}
	}

}
