Assignment 1b

Part 2
------------------------------------------------------------------------------------------------------------------

Directory Structure of PartOfSpeech
.
├── mpos
│   ├── mobyposi.i
│   └── readme
├── pom.xml
├── readme.txt
├── src
│   ├── main
│   │   └── java
│   │       └── Assignment1b
│   │           └── PartOfSpeech
│   │               ├── POSOutputFormat.java
│   │               ├── POSWritable.java
│   │               └── PartOfSpeech.java
│   └── test
│       └── java
│           └── Assignment1b
│               └── PartOfSpeech
│                   └── AppTest.java
└── target
    ├── PartOfSpeech-0.0.1-SNAPSHOT.jar
    ├── classes
    │   └── Assignment1b
    │       └── PartOfSpeech
    │           ├── POSOutputFormat$MyCustomPOSWriter.class
    │           ├── POSOutputFormat.class
    │           ├── POSWritable.class
    │           ├── PartOfSpeech$PartOfSpeechMapper.class
    │           ├── PartOfSpeech$PartOfSpeechReducer.class
    │           └── PartOfSpeech.class
    ├── maven-archiver
    │   └── pom.properties
    ├── surefire-reports
    │   ├── Assignment1b.PartOfSpeech.AppTest.txt
    │   └── TEST-Assignment1b.PartOfSpeech.AppTest.xml
    └── test-classes
        └── Assignment1b
            └── PartOfSpeech
                └── AppTest.class
                
Steps to build the program:

1. Go to the directory where pom.xml is located.

2. Run "mvn clean install"

3. Run "mvn package"

Steps to execute:

From the same directory where pom.xml is located run the below commands:

1. For executing Part 2 of the assignment run
hadoop jar target/PartOfSpeech-0.0.1-SNAPSHOT.jar Assignment1b.PartOfSpeech.PartOfSpeech <HDFS Input Path> <HDFS Output Path> -pos <HDFS Parts of Speech FilePath>

For example:
hadoop jar target/PartOfSpeech-0.0.1-SNAPSHOT.jar Assignment1b.PartOfSpeech.PartOfSpeech /user/rxr151330/assignment1/ /user/rxr151330/output4 -pos /user/rxr151330/posfile/mobyposi.i

2. The part of speech file download from http://icon.shef.ac.uk/Moby/mpos.html needs to be placed in HDFS.
- The "mpos" directory contains the part of speech file "mobyposi.i". The ascii character delimiter "×" used in the file originally was replaced by a "|" for convenience.
- Please use the "mobyposi.i" file inside the "mpos" directory while running this program. Else it will not run.


Output file can be located by the name 'result.txt' on the HDFS Output Path. The content should look like below:

Length:5
Count of Words: 406551
Distribution of POS: {noun: 121028; plural: 3948; noun_phrase: 1; verb_participle: 53651; verb_transitive: 42806; verb_intransitive: 25553; adjective: 44455; adverb: 44320; conjunction: 7982; preposition: 13254; interjection: 11406; pronoun: 15605; definite_article: 21936; }
Number of Palindromes: 606

Length:6
Count of Words: 232298
Distribution of POS: {noun: 88218; plural: 2558; noun_phrase: 77; verb_participle: 27123; verb_transitive: 30949; verb_intransitive: 15198; adjective: 29686; adverb: 20639; conjunction: 3879; preposition: 6359; interjection: 3187; pronoun: 1650; definite_article: 2771; }
Number of Palindromes: 4