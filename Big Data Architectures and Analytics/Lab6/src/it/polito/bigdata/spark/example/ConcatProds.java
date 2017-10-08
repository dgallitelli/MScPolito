package it.polito.bigdata.spark.example;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.spark.api.java.function.FlatMapFunction;

@SuppressWarnings("serial")
public class ConcatProds implements FlatMapFunction<Iterable<String>, String> {

	@Override
	public Iterable<String> call(Iterable<String> products) throws Exception {

		int i = 0;
		String couple;
		String[] fields = new String[1000];
		ArrayList<String> combinedFields = new ArrayList<String>();
		Iterator<String> it = products.iterator();
		
		while (it.hasNext()){
			fields[i] = it.next();
		}
		
		for (i = 0; i < fields.length-1; i++){
			for (int j=i+1; j<fields.length; j++){
				couple = "";
				couple.concat(fields[i]+","+fields[j]);
				combinedFields.add(couple);
			}
		}
		
		return combinedFields;
	}

}
