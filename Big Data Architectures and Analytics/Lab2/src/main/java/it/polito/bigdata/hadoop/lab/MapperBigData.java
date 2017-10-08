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
class MapperBigData extends Mapper<
                    LongWritable, // Input key type
                    Text,         // Input value type
                    Text,         // Output key type
                    IntWritable> {// Output value type
 
    protected void map(
            LongWritable key,   // Input key type
            Text value,         // Input value type
            Context context) throws IOException, InterruptedException {
 
    	/* Implement the map method */
    	//separating strings on tabulation character
    	String[] words = value.toString().split("\\t+");
    	for (int i=0; i< words.length-1; i= i+2)
    		{
    		String cleanword = words[i].toLowerCase();
    		//if words begin with "ho" then
    		if(cleanword.startsWith("ho"))
    			context.write(new Text(cleanword), new IntWritable(Integer.parseInt(words[i+1])));
 
    		}
    }
}