package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.Matrix;
// import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.DataFrame;
/* import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;*/

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
//import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
/*import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.*;*/

public class SparkDriver {
	
	public static void main(String[] args) {

		String inputPath;

		inputPath=args[0];
		int algorithm = Integer.parseInt(args[1]);
	
		// Create a configuration object and set the name of the application
		SparkConf conf=new SparkConf().setAppName("Spark Lab8");
		
		// Create a Spark Context object
		JavaSparkContext sc = new JavaSparkContext(conf);

		// Create a Spark SQL Context object
		SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
		
		
        	//EX 1: READ AND FILTER THE DATASET AND STORE IT INTO A DATAFRAME
		
		// To avoid parsing the comma escaped within quotes, you can use the following regex:
		// line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		// instead of the simpler
		// line.split(",");
		// this will ignore the commas followed by an odd number of quotes.
		
		JavaRDD<String> myTrainingRDD = sc.textFile(inputPath);
		JavaRDD<String> filteredRDD = myTrainingRDD.filter(new RemoveNoEval());
		JavaRDD<LabeledPoint> trainDataRDD = filteredRDD.map(new LabeledRecordGen());

		DataFrame schemaReviews = sqlContext.createDataFrame(trainDataRDD, LabeledPoint.class);
				
		// Display 5 example rows.
    	schemaReviews.show(5);


        // Split the data into training and test sets (30% held out for testing)
    	DataFrame[] splits = schemaReviews.randomSplit(new double[]{0.7, 0.3});
    	DataFrame trainingData = splits[0];
    	DataFrame testData = splits[1];
    	
    	Pipeline pipeline = new Pipeline();
    	    	
    	// EX 1.5: Define Algorithm
    	if (algorithm == 1){
	    	// LOGISTIC REGRESSION
    		LogisticRegression lr = new LogisticRegression();
	    	lr.setMaxIter(30);
	    	lr.setRegParam(0.01);
	    	//EX 2: CREATE THE PIPELINE THAT IS USED TO BUILD THE CLASSIFICATION MODEL
			pipeline.setStages(new PipelineStage[]{lr});		

    	} else if (algorithm == 2) {
    		// DECISION TREE
    		StringIndexerModel labeledIndex = new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(trainingData);
    		DecisionTreeClassifier dc = new DecisionTreeClassifier();
    		dc.setImpurity("gini");
    		dc.setLabelCol("indexedLabel");
    		IndexToString labelConverter = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labeledIndex.labels());
    		pipeline.setStages(new PipelineStage[]{labeledIndex, dc, labelConverter});
    	}

    	
		// Train model. Use the training set 
		PipelineModel model = pipeline.fit(trainingData);
			
				
		/*==== EVALUATION ====*/


		// Make predictions for the test set.
		DataFrame predictions = model.transform(testData);

		// Select example rows to display.
		predictions.show(5);

		// Retrieve the quality metrics. 
		String cols = new String();
		if (algorithm == 1)
			cols = "label";
		else if (algorithm == 2)
			cols = "indexedLabel";
		MulticlassMetrics metrics = new MulticlassMetrics(predictions.select("prediction", cols));
    	

        // Confusion matrix
    	Matrix confusion = metrics.confusionMatrix();
    	System.out.println("Confusion matrix: \n" + confusion);

    	double precision = metrics.precision();
		System.out.println("Precision = " + precision);
        
        // Close the Spark context
		sc.close();
	}
}
