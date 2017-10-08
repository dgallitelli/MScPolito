package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.Function2;

@SuppressWarnings("serial")
public class sameUserRev implements Function2<String, String, String> {

	@Override
	public String call(String arg0, String arg1) throws Exception {
		
		arg0.concat(","+arg1);
		
		return arg0;
	}

}
