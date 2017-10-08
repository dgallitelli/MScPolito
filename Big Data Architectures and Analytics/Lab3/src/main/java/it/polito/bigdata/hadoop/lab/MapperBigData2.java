package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Lab  - Mapper
 */

/* Set the proper data types for the (key,value) pairs */
class MapperBigData2 extends Mapper<
                    Text, 						// Input key type
                    Text,		        		// Input value type
                    NullWritable,        		// Output key type
                    WordCountWritable> {		// Output value type
	
	// Create an object that is used to store/manage a top-100 vector
	// containing objects of type WordCountWritable
	private TopKVector<WordCountWritable> topk;
	private Integer k = 100;
	
	protected void setup(Context context){
		topk = new TopKVector<WordCountWritable>(k);
	}
    
    protected void map(
            Text key,   			// Input key type
            Text value,   // Input value type
            Context context) throws IOException, InterruptedException {
    	
    		// key = pair of products
    		// value = #occurrences

    		/* Implement the map method */
    		WordCountWritable WCW = new WordCountWritable(key.toString(), new Integer(Integer.parseInt(value.toString())));	    
    		topk.updateWithNewElement(WCW);
    			    	
    }
    
    protected void cleanup(Context context) throws IOException, InterruptedException {
    	// Print the content of the top-100 selected objects on the standard output
    	for (WordCountWritable myObj : topk.getLocalTopK()) {
			context.write(NullWritable.get(), new WordCountWritable(myObj));
    	}
    }
}
