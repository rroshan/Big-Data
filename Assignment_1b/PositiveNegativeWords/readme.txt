Assignment 1b

Part 1
------------------------------------------------------------------------------------------------------------------

Directory structure of PositiveNegativeWords folder
.
├── opinion-lexicon-English
│   ├── negative-words.txt
│   └── positive-words.txt
├── pom.xml
├── readme.txt
├── src
│   ├── main
│   │   └── java
│   │       └── Assignment1b
│   │           └── PositiveNegativeWords
│   │               └── PositiveNegativeWords.java
│   └── test
│       └── java
│           └── Assignment1b
│               └── PositiveNegativeWords
│                   └── AppTest.java
└── target
    ├── PositiveNegativeWords-0.0.1-SNAPSHOT.jar
    ├── classes
    │   └── Assignment1b
    │       └── PositiveNegativeWords
    │           ├── PositiveNegativeWords$PositiveNegativeWordsMapper.class
    │           ├── PositiveNegativeWords$PositiveNegativeWordsReducer.class
    │           └── PositiveNegativeWords.class
    ├── maven-archiver
    │   └── pom.properties
    ├── surefire-reports
    │   ├── Assignment1b.PositiveNegativeWords.AppTest.txt
    │   └── TEST-Assignment1b.PositiveNegativeWords.AppTest.xml
    └── test-classes
        └── Assignment1b
            └── PositiveNegativeWords
                └── AppTest.class
                
Steps to build the project:

1. Go to the directory where pom.xml is located.

2. Run "mvn clean install"

3. Run "mvn package"

Steps to execute:

From the same directory where pom.xml is located run the below commands:

1. For executing Part 1 of the assignment run

hadoop jar target/PositiveNegativeWords-0.0.1-SNAPSHOT.jar Assignment1b.PositiveNegativeWords.PositiveNegativeWords <HDFS Input Path> <HDFS Output Path> -good <HDFS Positive Words Filepath> -bad <HDFS Negative Words Filepath>

For example:
hadoop jar target/PositiveNegativeWords-0.0.1-SNAPSHOT.jar Assignment1b.PositiveNegativeWords.PositiveNegativeWords /user/rxr151330/assignment1 /user/rxr151330/PN_Ouput -good /user/rxr151330/words_file/positive-words.txt -bad /user/rxr151330/words_file/negative-words.txt

2. The positive and negative words files need to be in HDFS.

 - The "opinion-lexicon-English" directory contains the text files containing the positive and negative words.
 - These files need to be uploaded to HDFS and those paths need to be substituted in the command mentioned above.


Output file can be located by the name 'part-r-00000' on the HDFS Output Path. The content should look like below:

NEGATIVE	14249

POSITIVE	17298