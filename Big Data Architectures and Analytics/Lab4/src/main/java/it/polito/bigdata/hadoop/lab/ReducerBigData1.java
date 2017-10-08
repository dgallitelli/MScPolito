package it.polito.bigdata.hadoop.lab;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Lab - Reducer #1
 */

/* Set the proper data types for the (key,value) pairs */
class ReducerBigData1 extends Reducer<
                Text,           // Input key type
                Text,    // Input value type
                Text,           // Output key type
                DoubleWritable> {  // Output value type
    
	private double n_mean = 0;
	private double n_mean_elem = 0;
	HashMap<String,Double> productsRatings = new HashMap<String,Double>(); // as global variable
	
    @Override
    protected void reduce(
        Text key, // Input key type
        Iterable<Text> values, // Input value type
        Context context) throws IOException, InterruptedException {

		/* Implement the reduce method */
    	for (Text value : values){
    		// value = PID:rating
    		String[] prodInfo = value.toString().split(":");
    		double rating = Double.parseDouble(prodInfo[1]);
			productsRatings.put(prodInfo[0], rating);
    		n_mean += rating;
    		n_mean_elem ++;
    	}
    	
    	n_mean = n_mean/n_mean_elem;
    	
    	// Is it possible to iterate on values a second time? NO.
    	/*for (Text value : values){
    		String[] prodInfo = value.toString().split(":");
        	context.write(new Text(prodInfo[0]), new DoubleWritable(Double.parseDouble(prodInfo[1])-n_mean));
    	}*/
    	
    	for (Iterator<Entry<String, Double>> i = productsRatings.entrySet().iterator(); i.hasNext(); ){
			Entry<String, Double> pair = i.next();
		    String productId = pair.getKey();
		    double rating = pair.getValue();
		    
		    context.write(new Text(productId), new DoubleWritable(rating-n_mean));
		}
    	
    }
}
