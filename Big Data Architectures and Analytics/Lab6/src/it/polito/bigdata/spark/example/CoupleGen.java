package it.polito.bigdata.spark.example;

import java.util.ArrayList;
// import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;

@SuppressWarnings("serial")
public class CoupleGen implements PairFlatMapFunction<Iterable<String>, String, Integer> {

	@Override
	public Iterable<Tuple2<String, Integer>> call(Iterable<String> products) throws Exception {
		
		List<Tuple2<String,Integer>> myList = new ArrayList<>();

		/* int i = 0;
		String couple;
		String[] fields = new String[1000];
		Iterator<String> it = products.iterator();
		// HashMap<String, Integer> myList = new HashMap<String, Integer>();
		*/
		
		/*while (it.hasNext()){
			fields[i] = it.next();
		}
		
		for (i = 0; i < fields.length-1; i++){
			for (int j=i+1; j<fields.length; j++){
				couple = "";
				couple.concat(fields[i]+","+fields[j]);
				myList.add(new Tuple2<String, Integer>(couple, 1));
			}
		}*/
		
		// Alternative:
		for (String p1 : products) {
			for (String p2 : products) {
				if (p1.compareTo(p2) > 0)
					myList.add(new Tuple2<>(p1 + " " + p2, 1));
			}
		}
		
		return myList;
	}

}
