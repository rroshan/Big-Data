import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Q5 {
	case class Business(business_id: String, full_address: String, categories: String)
	case class Review(review_id: String, user_id: String, business_id: String, stars: Double)
	
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Q5")
    val sc = new SparkContext(conf)
        
	val businessArr = sc.textFile("hdfs:///user/rxr151330/assignment2/business.csv").map(_.split("\\^")).map(attributes => Business(attributes(0), attributes(1), attributes(2)));
	val reviewArr = sc.textFile("hdfs:///user/rxr151330/assignment2/review.csv").map(_.split("\\^")).map(attributes => Review(attributes(0), attributes(1), attributes(2), attributes(3).trim.toDouble));
	
	val businessArrKV = businessArr.map(business => (business.business_id, business))
	val reviewArrKV = reviewArr.map(review => (review.business_id, review))
	
	val businessFilteredArrKV = businessArrKV.filter(business => business._2.full_address.contains("TX"))
	
	val join_data = businessFilteredArrKV.join(reviewArrKV)
	
	val businessReviewCounts = join_data.map(t => (t._1, 1)).reduceByKey((x,y) => (x + y))
	
	businessReviewCounts.saveAsTextFile("hdfs:///user/rxr151330/assignment3/q5")

    sc.stop()
  }
}