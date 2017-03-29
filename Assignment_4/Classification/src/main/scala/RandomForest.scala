import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.tree.configuration.Algo
import org.apache.spark.mllib.tree.impurity.Gini
import org.apache.spark.mllib.tree.configuration.Strategy
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.evaluation.MulticlassMetrics

object RandomForest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("RandomForest")
    val sc = new SparkContext(conf)
	
	val rdd = sc.textFile("hdfs:///user/rxr151330/assignment4_classification/breast-cancer-wisconsin.data")
	
	//droping first column since id
	val data = rdd.map(d => d.split(",")).filter(_(6) != "?").map(d => d.drop(1)).map(_.map(_.toDouble))
	
	//4 depicts malignant and 2 depicts benign. 4 => 1, 2 => 0
	val labeledPoints = data.map(x => LabeledPoint(if (x.last == 4) 1 else 0, Vectors.dense(x.init)))
	
	val Array(trainingData, testData) = labeledPoints.randomSplit(Array(0.8, 0.2))
	
	val algorithm = Algo.Classification
	val impurity = Gini
	val maximumDepth = 3
	val treeCount = 20
	val featureSubsetStrategy = "auto"
	val seed = 5043

	val model = org.apache.spark.mllib.tree.RandomForest.trainClassifier(trainingData, new Strategy(algorithm, impurity, maximumDepth), treeCount, featureSubsetStrategy, seed)
	
	val labeledPredictions = testData.map { labeledPoint =>
	    val predictions = model.predict(labeledPoint.features)
	    (labeledPoint.label, predictions)
	}
	
	val evaluationMetrics = new MulticlassMetrics(labeledPredictions.map(x => (x._1, x._2)))
	
	//precision
	println("evaluationMetrics.precision:: " + evaluationMetrics.precision)
	
	//confusionMatrix
	println("evaluationMetrics.confusionMatrix:: ")
	println(evaluationMetrics.confusionMatrix)
	
	//False Positive Rate
	val labels = evaluationMetrics.labels
	labels.foreach { l =>
	  println(s"FPR($l) = " + evaluationMetrics.falsePositiveRate(l))
	}

	//True Positive Rate
	labels.foreach { l =>
	  println(s"TPR($l) = " + evaluationMetrics.truePositiveRate(l))
	}
	
	val recall = evaluationMetrics.recall // same as true positive rate
	
	val f1Score = evaluationMetrics.fMeasure
	
	println(s"Recall = $recall")
	
	println(s"fMeasure = $f1Score")
	
    sc.stop()
  }
}