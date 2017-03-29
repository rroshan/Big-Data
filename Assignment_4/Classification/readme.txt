How to run this Section?

Below are the commands. This command should be run from the same directory that contains the .sbt file

Decision Tree without PCA
Command: spark-submit --class "DecisionTree" --master local[4] target/scala-2.11/classification_2.11-1.0.jar

Decision Tree with PCA
Command: spark-submit --class "DecisionTreePCA" --master local[4] target/scala-2.11/classification_2.11-1.0.jar

Random Forest without PCA
Command: spark-submit --class "RandomForest" --master local[4] target/scala-2.11/classification_2.11-1.0.jar

Random Forest with PCA
Command: spark-submit --class "RandomForestPCA" --master local[4] target/scala-2.11/classification_2.11-1.0.jar

The output results will be part of spark's output to standard output.