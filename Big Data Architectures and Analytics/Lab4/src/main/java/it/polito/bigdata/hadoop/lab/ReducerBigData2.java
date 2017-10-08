package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Lab - Reducer #2
 */

/* Set the proper data types for the (key,value) pairs */
class ReducerBigData2 extends Reducer<
                Text,           // Input key type
                DoubleWritable,    // Input value type
                Text,           // Output key type
                DoubleWritable> {  // Output value type
    
	private double n_mean = 0;
	private double n_mean_elem = 0;
    
    @Override
    protected void reduce(
        Text key, // Input key type
        Iterable<DoubleWritable> values, // Input value type
        Context context) throws IOException, InterruptedException {

		/* Implement the reduce method */
    	for ( DoubleWritable value : values ){
    		n_mean += value.get();
    		n_mean_elem ++;
    	}
    	
    	n_mean = n_mean / n_mean_elem;
    	
    	context.write(key, new DoubleWritable(n_mean));
    	
    }
}
