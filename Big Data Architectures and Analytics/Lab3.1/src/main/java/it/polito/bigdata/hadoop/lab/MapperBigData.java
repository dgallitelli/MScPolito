package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Lab  - Mapper
 */

/* Set the proper data types for the (key,value) pairs */
class MapperBigData extends Mapper<
                    LongWritable, // Input key type
                    Text,         // Input value type
                    Text,         // Output key type
                    Text> {// Output value type
    
    protected void map(
            LongWritable key,   // Input key type
            Text value,         // Input value type
            Context context) throws IOException, InterruptedException {

	        // Split each sentence in words. Use whitespace(s) as delimiter (=a space, a tab, a line break, or a form feed)
			// The split method returns an array of strings
	        String[] words = value.toString().split(",");
	        // value 1 will have ProdID
	        String prodID = words[1];
	        // value 2 will have UserID
	        String userID = words[2];
	        
	        context.write(new Text(userID), new Text(prodID));
    }
}
