package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
	
public class SparkDriver {
	
	public static void main(String[] args) {

		String inputPath;
		String outputPath;
		
		inputPath=args[0];
		outputPath=args[1];

	
		// Create a configuration object and set the name of the application
		SparkConf conf=new SparkConf().setAppName("Spark - Lab 6");
		
		// Create a Spark Context object
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		// Read the content of the input file
		// Each element/string of the logRDD corresponds to one line of the input file  
		JavaRDD<String> logRDD = sc.textFile(inputPath);

		// Generates (user, prod) couple
		JavaPairRDD<String, String> resultRDD = logRDD.mapToPair(new userReviews());

		// Group the JavaPairRDD by key to a <String, List<String>> Pair
		JavaPairRDD<String, Iterable<String>> groupedRDD = resultRDD.groupByKey();
		
		// invert and generate couples
		JavaRDD<Iterable<String>> productsRDD = groupedRDD.values();
		
		// produce couples and frequency 1
		JavaPairRDD<String, Integer> freqRDD = productsRDD.flatMapToPair(new CoupleGen());
		// JavaRDD<String> pairRDD = productsRDD.flatMap(new ConcatProds());
		// JavaPairRDD<String, Integer> freqRDD = productsRDD.MapToPair(new myCouples());
		
		// Counts the frequencies of all the pairs of products reviewed together;
		JavaPairRDD<String, Integer> finalFreqRDD = freqRDD.reduceByKey(new Sum());
		
		// Filter and sort (invert-sort-invert) the frequencies
		JavaPairRDD<String, Integer> filteredRDD = finalFreqRDD.filter(new MoreThanOnce());
		JavaPairRDD<Integer, String> invertedRDD1 = filteredRDD.mapToPair(new InvertPairInteger());
		JavaPairRDD<Integer, String> invertedRDD2 = invertedRDD1.sortByKey();
		JavaPairRDD<String, Integer> sortedRDD = invertedRDD2.mapToPair(new InvertPairInteger2());
		
		// Store the result in the output folder
		sortedRDD.saveAsTextFile(outputPath);

		// Close the Spark context
		sc.close();
	}
}
