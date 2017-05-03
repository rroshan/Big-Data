import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.graphx._


object AuthorCollaboration
{
	case class Author(id:Long)
	
  def main(args: Array[String])
  {
    val conf = new SparkConf().setAppName("AuthorCollaboration")
    val sc = new SparkContext(conf)
	
	val defaultAuthor = Author(0)
	
	val rdd = sc.textFile("hdfs:///user/rxr151330/assignment5/CA-HepTh.txt")
	
	val vertexRDD = rdd.map(d => if(d.charAt(0) != '#') {d.split("\t")} else {null}).filter(_ != null).map(x => x(0).toLong).distinct.map(x =>(x, Author(x)))
	
	val edgeRDD = rdd.map(d => if(d.charAt(0) != '#') {d.split("\t")} else {null}).filter(_ != null).map(x => Edge(x(0).toLong, x(1).toLong, 0))
	
	val graph = Graph(vertexRDD, edgeRDD, defaultAuthor)
	
	val highestOutDegree = graph.outDegrees.reduce((va, vb) => if (va._2 > vb._2) va else vb )
	
	val highestInDegree = graph.inDegrees.reduce((va, vb) => if (va._2 > vb._2) va else vb )
	
	val ranks = graph.pageRank(0.0001).vertices
	
	val sortedRank = ranks.sortBy(item => (item._2), ascending=false)
	
	val top5Ranks = sortedRank.collect().take(5)
	
	val cc = graph.connectedComponents().vertices
	
	val ccNodeids = cc.map(c => c._2).distinct
	ccNodeids.saveAsTextFile("hdfs:///user/rxr151330/assignment5/q1d")
	
	val triCounts = graph.triangleCount().vertices
	val sortedTriCounts = triCounts.sortBy(item => (item._2), ascending=false)
	
	val top5Triangles = sortedTriCounts.collect().take(5)
  }
}