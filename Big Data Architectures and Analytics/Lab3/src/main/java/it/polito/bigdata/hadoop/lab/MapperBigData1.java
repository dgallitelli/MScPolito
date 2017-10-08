package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Lab  - Mapper
 */

/* Set the proper data types for the (key,value) pairs */
class MapperBigData1 extends Mapper<
                    LongWritable, 	// Input key type
                    Text,       	// Input value type
                    Text,       	// Output key type
                    IntWritable> {	// Output value type
    protected void map(
            LongWritable key,  				// Input key type- always LongWritable because it's an offset
            Text value,         	// Input value type
            Context context) throws IOException, InterruptedException {

    		/* Implement the map method for the WordCount problem */
    	
    	// Extract items from review files
    	String[] itemsFull = value.toString().split(",");
    	//Arrays.sort(items);
    	String[] items = new String[itemsFull.length-1];
    	for (int i=0; i<items.length; i++){
    		items[i]=itemsFull[i+1];
    	}
    		
    	
    	// Compute WordCount for couples
    	for (int i=1; i<items.length; i++){
    		for (int j=1; j<items.length-i-1; j++){
        		String values = new String();
        		values = items[i];
        		values = values.concat(",");
        		values += items[i+j];
        		
        		context.write(new Text(values), new IntWritable(1));
    		}
    	}
    		
    }
}
