import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Q2 {
	case class User(user_id: String, name: String, url: String)
	case class Review(review_id: String, user_id: String, business_id: String, stars: Double)
	
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Q2")
    val sc = new SparkContext(conf)
    
    val username = args(0).toString
        
	val userArr = sc.textFile("hdfs:///user/rxr151330/assignment2/user.csv").map(_.split("\\^")).map(attributes => User(attributes(0), attributes(1), attributes(2)));
	val reviewArr = sc.textFile("hdfs:///user/rxr151330/assignment2/review.csv").map(_.split("\\^")).map(attributes => Review(attributes(0), attributes(1), attributes(2), attributes(3).trim.toDouble));
	
	val userArrKV = userArr.map(user => (user.user_id, user))
	
	val filteredUserArrKV = userArrKV.filter(user => user._2.name.startsWith(username))
	
	val reviewArrKV = reviewArr.map(review => (review.user_id, review))
	val join_data = filteredUserArrKV.join(reviewArrKV)
	
	val cleaned_joined_data = join_data.map(t => (t._1, t._2._1.name, t._2._2.stars))
	
	val res = cleaned_joined_data.map(t => ((t._1, t._2), (t._3, 1))).reduceByKey((x,y) => (x._1 + y._1, x._2 + y._2))
	
	val finalResult = res.map(k => (k._1, k._2._1/k._2._2))
	
	finalResult.saveAsTextFile("hdfs:///user/rxr151330/assignment3/q2")

    sc.stop()
  }
}