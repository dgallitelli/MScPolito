package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Lab - Reducer
 */

/* Set the proper data types for the (key,value) pairs */
class ReducerBigData2 extends Reducer<
                NullWritable,           // Input key type
                WordCountWritable,    // Input value type
                Text,           // Output key type
                IntWritable> {  // Output value type
	
	private Integer k = 100;
    
    @Override
    protected void reduce(
        NullWritable key, // Input key type
        Iterable<WordCountWritable> values, // Input value type
        Context context) throws IOException, InterruptedException {

		/* Implement the reduce method */
    	// Create an object that is used to store/manage a top-100 vector
    	// containing objects of type WordCountWritable
    	TopKVector<WordCountWritable> topk = new TopKVector<WordCountWritable>(k);

		/* Implement the map method */
    	for (WordCountWritable myWCW : values){   
    		topk.updateWithNewElement(new WordCountWritable(myWCW));		
    	}
    	
    	// Print the content of the top-100 selected objects on the standard output
    	for (WordCountWritable myObj : topk.getLocalTopK()) {
    		context.write(new Text(myObj.getWord()), new IntWritable(myObj.getCount()));
    	}
    	
    }
}
