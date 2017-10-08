package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Lab  - Mapper #2
 */

/* Set the proper data types for the (key,value) pairs */
class MapperBigData2 extends Mapper<
                    Text, // Input key type
                    Text,         // Input value type
                    Text,         // Output key type
                    DoubleWritable> {// Output value type
    
    protected void map(
            Text key,   // Input key type
            DoubleWritable value,         // Input value type
            Context context) throws IOException, InterruptedException {

    		/* Implement the map method */ 
		
		// Step 2: emit (PID,score) pair
    	context.write(key, new DoubleWritable(Double.parseDouble(value.toString())));
		
    }
}
