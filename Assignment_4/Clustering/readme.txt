How to run this Section?

Below is the command. This command should be run from the same directory that contains the .sbt file.

spark-submit --class "Kmeans" --master local[4] target/scala-2.11/kmeans_2.11-1.0.jar <Number of Clusters> <Number of Interations>

Based on the elbow graph presented, 3 was chosen as the number of clusters.

spark-submit --class "Kmeans" --master local[4] target/scala-2.11/kmeans_2.11-1.0.jar 3 15

The output results will be part of spark's output to standard output.

WSSE (Within Set Sum of Squared Errors) = 180375.76707539958