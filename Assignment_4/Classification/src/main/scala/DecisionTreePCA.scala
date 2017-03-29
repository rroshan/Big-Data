import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.tree.impurity.Gini
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.feature.PCA

object DecisionTreePCA {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("DecisionTreePCA")
    val sc = new SparkContext(conf)
	
	val rdd = sc.textFile("hdfs:///user/rxr151330/assignment4_classification/breast-cancer-wisconsin.data")
	
	//droping first column since id
	val data = rdd.map(d => d.split(",")).filter(_(6) != "?").map(d => d.drop(1)).map(_.map(_.toDouble))
	
	//4 depicts malignant and 2 depicts benign. 4 => 1, 2 => 0
	val labeledPoints = data.map(x => LabeledPoint(if (x.last == 4) 1 else 0, Vectors.dense(x.init)))
	
	//Choosing K as 5
	val pca = new PCA(5).fit(labeledPoints.map(_.features))
	
	val projected = labeledPoints.map(p => p.copy(features = pca.transform(p.features)))
	
	val Array(trainingData, testData) = projected.randomSplit(Array(0.8, 0.2))
	
	val numClasses = 2
	val categoricalFeaturesInfo = Map[Int, Int]()
	val impurity = "gini"
	val maxDepth = 5
	val maxBins = 32

	val model = org.apache.spark.mllib.tree.DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo, impurity, maxDepth, maxBins)
	
	val projectedLabeledPredictions = testData.map { projectedLabeledPoint =>
	    val predictions = model.predict(projectedLabeledPoint.features)
	    (projectedLabeledPoint.label, predictions)
	}
	
	val evaluationMetrics = new MulticlassMetrics(projectedLabeledPredictions.map(x => (x._1, x._2)))
	
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