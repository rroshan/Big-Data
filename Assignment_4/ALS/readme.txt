How to run this Section?

Below is a command template. This command should be run from the same directory that contains the .sbt file.

This command template runs the code with one set of parameters
spark-submit --class "ALS" --master local[4] target/scala-2.11/als_2.11-1.0.jar <Rank> <Iteration> <LearningRate> <top10outputpath>

This command is to see the top 10 books
hdfs dfs -cat <top10outputpath>/*

Rank = 10, Number of Iteration = 10, Learning Rate = 0.1, Top 10 Output Path = top10_1
Command: spark-submit --class "ALS" --master local[4] target/scala-2.11/als_2.11-1.0.jar 10 10 0.1 top10_1
Command: hdfs dfs -cat /user/rxr151330/assignment4_ALS_output/top10_1/*

Rank = 10, Number of Iteration = 10, Learning Rate = 0.01, Top 10 Output Path = top10_2
Command: spark-submit --class "ALS" --master local[4] target/scala-2.11/als_2.11-1.0.jar 10 10 0.01 top10_2
Command: hdfs dfs -cat /user/rxr151330/assignment4_ALS_output/top10_2/*

Rank = 20, Number of Iteration = 10, Learning Rate = 0.1, Top 10 Output Path = top10_3
spark-submit --class "ALS" --master local[4] target/scala-2.11/als_2.11-1.0.jar 20 10 0.1 top10_3
hdfs dfs -cat /user/rxr151330/assignment4_ALS_output/top10_3/*

Rank = 20, Number of Iteration = 10, Learning Rate = 0.01, Top 10 Output Path = top10_4
spark-submit --class "ALS" --master local[4] target/scala-2.11/als_2.11-1.0.jar 20 10 0.01 top10_4
hdfs dfs -cat /user/rxr151330/assignment4_ALS_output/top10_4/*

The output results will be part of spark's output to standard output.