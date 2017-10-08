package it.polito.bigdata.spark.example;

import java.sql.Date;
import java.time.LocalDate;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

@SuppressWarnings("serial")
public class LabeledRecordGen implements Function<String, LabeledPoint> {

	@Override
	public LabeledPoint call(String record) throws Exception {
		
		/*
		 * 1. Compute label 
		 * 1.1 Get helpfulness numerator
		 * 1.2 Get helpfulness denominator
		 * 1.3 Compute double by division
		 * 1.4 If (>0.9) --> label 1 else 0
		 * 2. Compute Features
		 * 2.1 Get Lenght of Text 
		 * 2.2 parse to Double 
		 * 3. Create Vector
		 * 4. Return Labeled Point from Vector
		 * */
		
		// Step 1 - Compute Label
		
		String[] fields = record.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		
		double helpN = Double.parseDouble(fields[4]);
		double helpD = Double.parseDouble(fields[5]);
		double helpF = helpN/helpD;
		double classLabel;
		
		if (helpF > 0.9)
			classLabel = (double) 1;
		else 
			classLabel = 0;
		
		// Step 2 - Compute Features
		
		double[] features = new double[fields.length-1];
		// Feature 1 - Text length
		features[0] = fields[9].length();
		// Feature 2 - Distance in time, normalized by current timestamp
		double today = Double.parseDouble(LocalDate.now().toString());
		features[1] = ( today) - Double.parseDouble(fields[7]) )/today;
 		
		
		// Step 3 - Create Vector
		
		Vector attrValues = Vectors.dense(features);
		
		// Step 4 - Return Labeled Point
		
		return new LabeledPoint(classLabel, attrValues);

		
	}

}
