package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Lab  - Mapper #1
 */

/* Set the proper data types for the (key,value) pairs */
class MapperBigData1 extends Mapper<
                    LongWritable, // Input key type
                    Text,         // Input value type
                    Text,         // Output key type
                    Text> {// Output value type
    
    protected void map(
            LongWritable key,   // Input key type
            Text value,         // Input value type
            Context context) throws IOException, InterruptedException {

    		/* Implement the map method */ 
    		
    	// Step 1: collect all scores given by a UID
    	String[] items = value.toString().split(",");
    	
    	if (items[0].compareTo("Id")!=0){
    		if (items[1] != "" && items[2] != "") {
    	    	String UID = items[2];
    	    	String PID = items[1];
    	    	int score = Integer.parseInt(items[6]);
    	    	
    	    	// Step 2: pass the (UID,score) couple to the reducer
    	    	context.write(new Text(UID) , new Text(PID+":"+score));
        	}
    	}
    	
    }
}
