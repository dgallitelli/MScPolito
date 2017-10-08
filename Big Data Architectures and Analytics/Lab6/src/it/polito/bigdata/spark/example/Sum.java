package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.function.Function2;

@SuppressWarnings("serial")
public class Sum implements Function2<Integer, Integer, Integer> {

	@Override
	public Integer call(Integer value1, Integer value2) throws Exception {
		// TODO Auto-generated method stub
		return value1+value2;
	}

}
