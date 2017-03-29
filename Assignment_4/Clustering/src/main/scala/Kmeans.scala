import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.Vectors

object Kmeans {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Kmeans")
    val sc = new SparkContext(conf)
	  
	val data = sc.textFile("hdfs:///user/rxr151330/assignment4_clustering/turkiye-student-evaluation_generic.csv")

	val header = data.first()

	val dataWithoutHeader = data.filter(row => row != header)

	val parsedData = dataWithoutHeader.map(s => Vectors.dense(s.split(',').map(_.toDouble))).cache()

	val numClusters = args(0).toInt

	val numIterations = args(1).toInt

	val clusters = org.apache.spark.mllib.clustering.KMeans.train(parsedData, numClusters, numIterations)

	val WSSSE = clusters.computeCost(parsedData)
	println("Within Set Sum of Squared Errors = " + WSSSE)

	// clusters.save(sc, "target/org/apache/spark/KMeansExample/KMeansModel")

	// val sameModel = KMeansModel.load(sc, "target/org/apache/spark/KMeansExample/KMeansModel")

    sc.stop()
  }
}