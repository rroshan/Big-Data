import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Q1 {
	case class Business(business_id: String, full_address: String, categories: String)
	case class Review(review_id: String, user_id: String, business_id: String, stars: Double)
	
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Q1")
    val sc = new SparkContext(conf)
        
	val businessArr = sc.textFile("hdfs:///user/rxr151330/assignment2/business.csv").map(_.split("\\^")).map(attributes => Business(attributes(0), attributes(1), attributes(2)));
	val reviewArr = sc.textFile("hdfs:///user/rxr151330/assignment2/review.csv").map(_.split("\\^")).map(attributes => Review(attributes(0), attributes(1), attributes(2), attributes(3).trim.toDouble));
	
	val businessArrKV = businessArr.map(business => (business.business_id, business))
	val reviewArrKV = reviewArr.map(review => (review.business_id, review))
	val join_data = businessArrKV.join(reviewArrKV)
	
	val cleaned_joined_data = join_data.map(t => (t._1, t._2._1.full_address, t._2._1.categories, t._2._2.stars))
	
	val res = cleaned_joined_data.map(t => ((t._1, t._2, t._3), (t._4, 1))).reduceByKey((x,y) => (x._1 + y._1, x._2 + y._2))
	
	val finalResult = res.map(k => (k._1, k._2._1/k._2._2))
	
	val sortedFinalResult = finalResult.sortBy(item => (item._2), ascending=false)
	
	val top10 = sortedFinalResult.collect().take(10)
	
	sc.parallelize(top10).saveAsTextFile("hdfs:///user/rxr151330/assignment3/q1")

    sc.stop()
  }
}