Steps to build the program:
1. Go to the directory where pom.xml is located.
2. Run "mvn clean install"
3. Run "mvn package"

Steps to execute:
From the same directory where pom.xml is located run the below commands:

1. For executing Part 2 of the assignment run
	hadoop jar target/partii-0.0.1-SNAPSHOT.jar assignment1.partii.Part2
	
	This part expects urls_zip.txt to be in the same directory as pom.xml
	
	For part 2 we chose the corpus: http://corpus.byu.edu/glowbetext/samples/text.zip
	
2. Run hdfs dfs -ls /user/rxr151330/assignment1
	to see the files in hdfs.