# Remove folders of the previous run
hdfs dfs -rm criticalStations.kml


# Run application
spark-submit  --class it.polito.bigdata.spark.example.SparkDriver --deploy-mode client --master local target/Lab7.jar "/data/students/bigdata-01QYD/Lab7/registers.csv" "/data/students/bigdata-01QYD/Lab7/stations.csv" 0.5 "criticalStations.kml"  

