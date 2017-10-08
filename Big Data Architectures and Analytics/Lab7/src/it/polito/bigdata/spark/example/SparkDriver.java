package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.*;

import scala.Tuple2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
// import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;

public class SparkDriver {

	public static void main(String[] args) {

		String inputPath;
		String inputPath2;
		Double threshold;
		String outputNameFileKML;

		inputPath = args[0];
		inputPath2 = args[1];
		threshold = new Double(args[2]);
		outputNameFileKML = args[3];

		// Create a configuration object and set the name of the application
		SparkConf conf = new SparkConf().setAppName("Spark Lab #7");

		// Create a Spark Context object
		JavaSparkContext sc = new JavaSparkContext(conf);

		// Read the content of the input file
		JavaRDD<String> inputRDD = sc.textFile(inputPath);
		JavaRDD<String> inputCoordRDD = sc.textFile(inputPath2);

		//Filter the register.csv file to remove first line and lines with free-slots and used slots equal 0
		JavaRDD<String> filteredRDD = inputRDD.filter(new RemoveProblems());
		
		//Using the DateTool to obtain the day corresponding to the timestamp
		JavaRDD<String> timeStampRDD = filteredRDD.map(new TimeStamper());
		
		//I'm generating tuple: ((StationID, Timestamp), FreeSlots)
		JavaPairRDD<String, Integer> pairSTRDD = timeStampRDD.mapToPair(new CoupleGen());
		
		// GroupByKey transformation to obtain a List of FreeSlots per (S,T)
		JavaPairRDD<String, Iterable<Integer>> reducedRDD = pairSTRDD.groupByKey();
		// Then compute Criticality per (S,T)
		JavaPairRDD<String, Double> critRDD = reducedRDD.mapToPair(new CriticalityGen());
		
		// Only keep records with criticality above user-specified threshold
		JavaPairRDD<String, Double> filtCritRDD = critRDD.filter(new AboveThreshold(threshold));

		// It is now necessary to check the timestamp with highest criticality per stationID
		// Change the format of the record to (StationID, (TimeStamp, Criticality))
		JavaPairRDD<Integer, Tuple2<String, Double>> stationRDD = filtCritRDD.mapToPair(new StationGen());
		
		// Group over StationID, obtaining a list of (T, C) tuples
		JavaPairRDD<Integer, Iterable<Tuple2<String, Double>>> groupedStationRDD = stationRDD.groupByKey();
		
		// Obtain the timestamp with highest criticality per stationID
		JavaPairRDD<Integer, Tuple2<String, Double>> mostCritRDD = groupedStationRDD.mapToPair(new MostCrit());
		
		
		
		//TODO join stationID with its coordinates
		
		// Map to Pair to obatin (stationID, Coordinates)
		JavaRDD<String> noFirstCoordRDD = inputCoordRDD.filter(new SkipFirstStation());
		JavaPairRDD<Integer, String> coordRDD = noFirstCoordRDD.mapToPair(new CoordGen());
		
		// Joining the two JavaPairRdd
		JavaPairRDD<Integer,Tuple2<Tuple2<String,Double>,String>> joinRDD = mostCritRDD.join(coordRDD);

		// Store in resultKML one String, representing a KML marker, for each station 
		// with a critical timeslot 
		JavaRDD<String> resultKML = joinRDD.map(new PlacemarkerGen());
		
		// There is at most one string for each station. We can use collect and
		// store the returned list in the main memory of the driver.
		List<String> localKML = resultKML.collect();
		
		// Store the result in one single file stored in the distributed file
		// system
		// Add header and footer, and the content of localKML in the middle
		Configuration confHadoop = new Configuration();

		try {
			URI uri = URI.create(outputNameFileKML);

			FileSystem file = FileSystem.get(uri, confHadoop);
			FSDataOutputStream outputFile = file.create(new Path(uri));

			BufferedWriter bOutFile = new BufferedWriter(new OutputStreamWriter(outputFile, "UTF-8"));

			// Header
			bOutFile.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\"><Document>");
			bOutFile.newLine();

			// Markers
			for (String lineKML : localKML) {
				bOutFile.write(lineKML);
				bOutFile.newLine();
			}

			// Footer
			bOutFile.write("</Document></kml>");
			bOutFile.newLine();

			bOutFile.close();
			outputFile.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Close the Spark context
		sc.close();
	}
}
