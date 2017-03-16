import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Q4 {
	case class User(user_id: String, name: String, url: String)
	case class Review(review_id: String, user_id: String, business_id: String, stars: Double)
	
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Q4")
    val sc = new SparkContext(conf)
        
	val userArr = sc.textFile("hdfs:///user/rxr151330/assignment2/user.csv").map(_.split("\\^")).map(attributes => User(attributes(0), attributes(1), attributes(2)));
	val reviewArr = sc.textFile("hdfs:///user/rxr151330/assignment2/review.csv").map(_.split("\\^")).map(attributes => Review(attributes(0), attributes(1), attributes(2), attributes(3).trim.toDouble));
	
	val userArrKV = userArr.map(user => (user.user_id, user))
	
	val reviewArrKV = reviewArr.map(review => (review.user_id, review))
	val join_data = userArrKV.join(reviewArrKV)
	
	val userReviewCounts = join_data.map(t => ((t._1, t._2._1.name), 1)).reduceByKey((x,y) => (x + y))
	
	val sortedFinalResult = userReviewCounts.sortBy(item => (item._2), ascending=false)
	
	val top10 = sortedFinalResult.collect().take(10)
	
	val top10RDD = sc.parallelize(top10)
	
	val top10UserReviewDetails = top10RDD.map(t => (t._1._1, t._1._2))
	
	top10UserReviewDetails.saveAsTextFile("hdfs:///user/rxr151330/assignment3/q4")

    sc.stop()
  }
}