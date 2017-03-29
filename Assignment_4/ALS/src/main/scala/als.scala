import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.recommendation.Rating

object ALS {
	case class User(id: Int, location: String, age: Int)
	case class Book(isbn: String, book_title: String, book_author: String, year_of_publication: String, publisher: String, img_url_s: String, img_url_m: String, img_url_l: String)
	case class UserRating(user_id: Int, isbn: String, book_rating: Float)

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("ALS")
    val sc = new SparkContext(conf)
	
	val rank = args(0).toInt
	val numIterations = args(1).toInt
	val learningRate = args(2).toDouble
	val top10filename = args(3).toString
    
	val booksRDD = sc.textFile("hdfs:///user/rxr151330/assignment4/BX-Books.csv").map(_.split(";")).mapPartitionsWithIndex((idx, iter) => if (idx == 0) iter.drop(1) else iter).map(attributes => Book(attributes(0).replaceAll("\"",""), attributes(1).replaceAll("\"",""), attributes(2).replaceAll("\"",""), attributes(3).replaceAll("\"",""), attributes(4).replaceAll("\"",""), attributes(5).replaceAll("\"",""), attributes(6).replaceAll("\"",""), attributes(7).replaceAll("\"","")));

	val usersRDD = sc.textFile("hdfs:///user/rxr151330/assignment4/BX-Users.csv").map(_.split(";")).mapPartitionsWithIndex((idx, iter) => if (idx == 0) iter.drop(1) else iter).map(attributes => User(attributes(0).replaceAll("\"","").toInt, attributes(1).replaceAll("\"",""), attributes(2).replaceAll("\"","").toInt));
	
	val ratingsRDD = sc.textFile("hdfs:///user/rxr151330/assignment4/BX-Book-Ratings.csv").map(_.split(";")).mapPartitionsWithIndex((idx, iter) => if (idx == 0) iter.drop(1) else iter).map(attributes => UserRating(attributes(0).replaceAll("\"","").toInt, attributes(1).replaceAll("\"",""), attributes(2).replaceAll("\"","").toFloat));
	
	val ratingsRDDGroupByISBN = ratingsRDD.map(r => (r.isbn, (r.book_rating, 1))).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))
	
	val finalResult = ratingsRDDGroupByISBN.map(k => (k._1, k._2._1/k._2._2))
	
	val sortedFinalResult = finalResult.sortBy(item => (item._2), ascending=false)
	
	val top10 = sortedFinalResult.collect().take(10)
	
	sc.parallelize(top10).saveAsTextFile("hdfs:///user/rxr151330/assignment4_ALS_output/" + top10filename)
	
	//converting isbn to int since spark als api requires everything to be numeric
	val isbnToInt: RDD[(String, Long)] = ratingsRDD.map(_.isbn).distinct().zipWithUniqueId()
	
	val ratingsRDDISBNKey = ratingsRDD.map(r => (r.isbn, (r.user_id, r.book_rating))).join(isbnToInt)
	
	val cleanedRatingsRDD = ratingsRDDISBNKey.map(t => (t._1, t._2._1._1, t._2._2, t._2._1._2))

	val ratings = cleanedRatingsRDD.map(t => Rating(t._2.toInt, t._3.toInt, t._4.toDouble))
	
	val Array(training, test) = ratings.randomSplit(Array(0.80, 0.20))
	
	val model = org.apache.spark.mllib.recommendation.ALS.train(training, rank, numIterations, learningRate)
	
	val testUsersProducts = test.map { case Rating(user, product, rate) =>
		(user, product)
	}

	val predictions =
	model.predict(testUsersProducts).map { case Rating(user, product, rate) =>
		((user, product), rate)
	}

	val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
		((user, product), rate)
	}.join(predictions)

	val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
		val err = (r1 - r2)
		err * err
	}.mean()

	val rmse = math.sqrt(MSE)
	println("Root Mean Squared Error = " + rmse)

    sc.stop()
  }
}