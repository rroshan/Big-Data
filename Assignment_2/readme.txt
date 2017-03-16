Assignment 2

Lab 4
------------------------------------------------------------------------------------------------------------------
Directory Structure

.
├── CS6350_CoverPage.docx
├── output
│   ├── lab4output1
│   │   ├── _SUCCESS
│   │   └── part-r-00000
│   ├── lab4output2
│   │   ├── _SUCCESS
│   │   └── part-m-00000
│   ├── lab4output3
│   │   ├── _SUCCESS
│   │   └── part-r-00000
│   ├── lab4output4
│   │   ├── _SUCCESS
│   │   └── part-r-00000
│   └── lab4output5
│       ├── _SUCCESS
│       └── part-r-00000
├── pom.xml
├── readme.txt
├── src
│   ├── main
│   │   └── java
│   │       └── Lab4
│   │           └── Lab4
│   │               ├── MeanPairWritable.java
│   │               ├── Q1.java
│   │               ├── Q2.java
│   │               ├── Q3.java
│   │               ├── Q4.java
│   │               └── Q5.java

Steps to build the program:

1. Go to the directory where pom.xml is located.

2. Run "mvn clean install"

3. Run "mvn package"

Steps to execute:

From the same directory where pom.xml is located run the below commands:


Question 1
hadoop jar target/Lab4-0.0.1-SNAPSHOT.jar Lab4.Lab4.Q1 /user/rxr151330/assignment2/business.csv /user/rxr151330/lab4output1
The output will be in the file part-r-00000 inside the /user/rxr151330/lab4output1 directory in HDFS. 

Question 2
hadoop jar target/Lab4-0.0.1-SNAPSHOT.jar Lab4.Lab4.Q2 /user/rxr151330/assignment2/business.csv /user/rxr151330/lab4output2
The output will be in the file part-m-00000 inside the /user/rxr151330/lab4output2 directory in HDFS. 

Question 3
hadoop jar target/Lab4-0.0.1-SNAPSHOT.jar Lab4.Lab4.Q3 /user/rxr151330/assignment2/business.csv /user/rxr151330/lab4output3
The output will be in the file part-r-00000 inside the /user/rxr151330/lab4output3 directory in HDFS. 

Question 4
hadoop jar target/Lab4-0.0.1-SNAPSHOT.jar Lab4.Lab4.Q4 /user/rxr151330/assignment2/review.csv /user/rxr151330/lab4output4
The output will be in the file part-r-00000 inside the /user/rxr151330/lab4output4 directory in HDFS.

Question 5
hadoop jar target/Lab4-0.0.1-SNAPSHOT.jar Lab4.Lab4.Q5 /user/rxr151330/assignment2/review.csv /user/rxr151330/lab4output5
The output will be in the file part-r-00000 inside the /user/rxr151330/lab4output5 directory in HDFS.



Sample Outputs:
The output generated while testing has also been included with the submission for your reference.