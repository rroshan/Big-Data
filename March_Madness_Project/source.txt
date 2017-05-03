import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.tree.configuration.Algo
import org.apache.spark.mllib.tree.impurity.Gini
import org.apache.spark.mllib.tree.configuration.Strategy
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.rdd.MLPairRDDFunctions.fromPairRDD

import org.apache.spark.mllib.tree.GradientBoostedTrees
import org.apache.spark.mllib.tree.configuration.BoostingStrategy
import org.apache.spark.mllib.tree.model.GradientBoostedTreesModel
import org.apache.spark.mllib.util.MLUtils

object NCAAPredictor {
	case class PlayoffSeeds(year: Int, seed: String, teamId: Int)
	case class RegularSeasonCompact(year: Int, daynum: Int, wteam: Int, wscore: Int, lteam: Int, lscore: Int, wloc: String, numot: Int)
	case class TeamA(TEAMID: Int, YEAR: Int, A_WINPCT: Double, A_AWINPCT: Double, A_LAST10W: Int, A_AVGPTSW: Double, A_SEED: Int)
	case class TeamB(TEAMID: Int, YEAR: Int, B_WINPCT: Double, B_AWINPCT: Double, B_LAST10W: Int, B_AVGPTSW: Double, B_SEED: Int)
	
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("NCAAPredictor")
    val sc = new SparkContext(conf)
	
	val playoffTeamsWithHeader = sc.textFile("file:///Users/roshan/Documents/UTD/Spring_2017/Big_Data/Project/Data/TourneySeeds.csv")
	val playoffHeader = playoffTeamsWithHeader.first()
	val playoffTeamsWithoutHeader = playoffTeamsWithHeader.filter(row => row != playoffHeader)
	val playOffRDD = playoffTeamsWithoutHeader.map(_.split(",")).map(attributes => PlayoffSeeds(attributes(0).toInt, attributes(1), attributes(2).toInt));
	val playOffRDDKV = playOffRDD.map(attributes => ((attributes.teamId, attributes.year), attributes))
	
	val regularSeasonWithHeader = sc.textFile("file:///Users/roshan/Documents/UTD/Spring_2017/Big_Data/Project/Data/RegularSeasonCompactResults.csv")
	val regularSeasonHeader = regularSeasonWithHeader.first()
	val regularSeasonWithoutHeader = regularSeasonWithHeader.filter(row => row != regularSeasonHeader)
	val regularSeasonCompactRDD = regularSeasonWithoutHeader.map(_.split(",")).map(attributes => RegularSeasonCompact(attributes(0).toInt, attributes(1).toInt, attributes(2).toInt, attributes(3).toInt, attributes(4).toInt, attributes(5).toInt, attributes(6), attributes(7).toInt))
	
	//calculating win percentage
	val winFreqRDD = regularSeasonCompactRDD.map(rs_row => ((rs_row.wteam, rs_row.year), 1)).reduceByKey((x,y) => (x + y))
	val lossFreqRDD = regularSeasonCompactRDD.map(rs_row => ((rs_row.lteam, rs_row.year), 1)).reduceByKey((x,y) => (x + y))
	val winFreqJoinData = winFreqRDD.join(lossFreqRDD)
	val winFreqCleanedJoinedData = winFreqJoinData.map(t => (t._1._1, t._1._2, t._2._1, t._2._2))
	val regularSeasonWinPctRDD = winFreqCleanedJoinedData.map(t => ((t._1, t._2), (t._3.toDouble/(t._3.toDouble + t._4.toDouble))));
	val final_join_data1 = playOffRDDKV.join(regularSeasonWinPctRDD);
	
	// filtering only Away games
	val filteredRegularSeasonCompactRDD = regularSeasonCompactRDD.filter(rs_row => rs_row.wloc.equals("A"))
	val awayWinFreqRDD = filteredRegularSeasonCompactRDD.map(rs_row => ((rs_row.wteam, rs_row.year), 1)).reduceByKey((x,y) => (x + y))
	val awayWinJoinData = awayWinFreqRDD.join(winFreqRDD)
	val awayWinCleanedJoinedData = awayWinJoinData.map(t => (t._1._1, t._1._2, t._2._1, t._2._2))
	val awayWinPctRDD = awayWinCleanedJoinedData.map(t => ((t._1, t._2), (t._3.toDouble/t._4.toDouble)))
	val final_join_data2 = playOffRDDKV.join(awayWinPctRDD);
	
	//calculating a teams win % in the last 10 games of regular season.
	val wintemp = regularSeasonCompactRDD.map(rs_row => ((rs_row.wteam, rs_row.year), (rs_row.daynum, 'W')));
	val losstemp = regularSeasonCompactRDD.map(rs_row => ((rs_row.lteam, rs_row.year), (rs_row.daynum, 'L')));
	val winPlayoffJoin = playOffRDDKV.join(wintemp);
	val lossPlayoffJoin = playOffRDDKV.join(losstemp);
	//((team, year), (daynum, 'W/L'))
	val winPlayoffJoinClean = winPlayoffJoin.map(t => ((t._1._1, t._1._2), (t._2._2._1, t._2._2._2)));
	val lossPlayoffJoinClean = lossPlayoffJoin.map(t => ((t._1._1, t._1._2), (t._2._2._1, t._2._2._2)));
	val unionRDD = winPlayoffJoinClean.union(lossPlayoffJoinClean)
	val sortedRDD = unionRDD.sortBy(x => (x._1._2, x._1._1, x._2._1), false)
	val top10RDD = sortedRDD.topByKey(10)
	
	//write a map to calculate number of W in the array
	val finaltop10RDD = top10RDD.map { tuple =>
		val recordArr = tuple._2
		var count = 0
		for (ele <- recordArr) {
			val res = ele
			if(res._2 == 'W') {
				count = count + 1
			}
		}
		(tuple._1, count)
	}
	
	//averpoints win by
	val ptsWonByRDD = regularSeasonCompactRDD.map(rs_row => ((rs_row.wteam, rs_row.year), ((rs_row.wscore - rs_row.lscore), 1)))
	val avgPtsWonByTempRDD = ptsWonByRDD.reduceByKey((u, v) => ((u._1 + v._1), (u._2 + v._2)))
	val avgPtsWonByRDD = avgPtsWonByTempRDD.map(t => (t._1, (t._2._1.toDouble / t._2._2.toDouble)))
	
	//calculate seed too
	val playoffSeedsTempRDD = playOffRDDKV.map(t => (t._1, t._2.seed))
	val playoffSeedsRDD = playoffSeedsTempRDD.map { t =>
		var str = t._2
		str = str.substring(1, 3)
		val s = Integer.parseInt(str)
		(t._1, s)
	}
	
	val joins = final_join_data1.join(final_join_data2).join(finaltop10RDD).join(avgPtsWonByRDD).join(playoffSeedsRDD)
	
	val teamARDD = joins.map(attributes => ((attributes._1._1, attributes._1._2), TeamA(attributes._1._1, attributes._1._2, attributes._2._1._1._1._1._2, attributes._2._1._1._1._2._2, attributes._2._1._1._2, attributes._2._1._2, attributes._2._2)));
	val teamBRDD = joins.map(attributes => ((attributes._1._1, attributes._1._2), TeamB(attributes._1._1, attributes._1._2, attributes._2._1._1._1._1._2, attributes._2._1._1._1._2._2, attributes._2._1._1._2, attributes._2._1._2, attributes._2._2)));
	
	//constructing matchup table
	val tourneyResWithHeader = sc.textFile("file:///Users/roshan/Documents/UTD/Spring_2017/Big_Data/Project/Data/TourneyCompactResults.csv")
	val header = tourneyResWithHeader.first()
	val tourneyResWithoutHeader = tourneyResWithHeader.filter(row => row != header)
	val tourneyResRDD = tourneyResWithoutHeader.map(_.split(",")).map { tuple => 
		val year = tuple(0).toInt
		val wteam = tuple(2).toInt
		val lteam = tuple(4).toInt
		if(wteam < lteam) {
			(year, wteam, lteam, 1)
		}
		else {
			(year, lteam, wteam, 0)
		}
	}
	
	val yaRDD = tourneyResRDD.map(t => ((t._2, t._1), t))
	val ajoin = teamARDD.join(yaRDD)
	val ybRDD = ajoin.map(t => ((t._2._2._3, t._1._2), (t._2._2._2, t._2._2._4, t._2._1)))
	val bjoin = teamBRDD.join(ybRDD)
	
	val completeRDD = bjoin.map(t => (t._1._2, t._2._2._1, t._1._1, t._2._2._2, t._2._2._3.A_WINPCT, t._2._2._3.A_LAST10W, t._2._2._3.A_AVGPTSW, t._2._2._3.A_SEED, t._2._1.B_WINPCT, t._2._1.B_LAST10W, t._2._1.B_AVGPTSW, t._2._1.B_SEED));
	
	//in training data remove years 2013 to 2017
	val trainRDD = completeRDD.filter(t => t._1 < 2015)
	
	val trainingData = trainRDD.map(x => LabeledPoint(x._4, Vectors.dense(x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12)))
	
	val testRDD = completeRDD.filter(t => t._1 > 2014)
	val testData = testRDD.map(x => LabeledPoint(x._4, Vectors.dense(x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12)))
	
	val algorithm = Algo.Classification
	val rfImpurity = Gini
	val maximumDepth = 4
	val treeCount = 25
	val featureSubsetStrategy = "auto"
	val seed = 5043
	val rfModel = org.apache.spark.mllib.tree.RandomForest.trainClassifier(trainingData, new Strategy(algorithm, rfImpurity, maximumDepth), treeCount, featureSubsetStrategy, seed)

	val rfLabeledPredictions = testData.map { labeledPoint =>
	    val predictions = rfModel.predict(labeledPoint.features)
	    (labeledPoint.label, predictions)
	}
	
	val rfEvaluationMetrics = new MulticlassMetrics(rfLabeledPredictions.map(x => (x._1, x._2)))
	println("Random Forest evaluationMetrics.precision:: " + rfEvaluationMetrics.precision)

	//confusionMatrix
	println("Random Forest evaluationMetrics.confusionMatrix:: ")
	println(rfEvaluationMetrics.confusionMatrix)
	
	
	val numClasses = 2
	val categoricalFeaturesInfo = Map[Int, Int]()
	val dtImpurity = "gini"
	val maxDepth = 5
	val maxBins = 32
	val dtModel = org.apache.spark.mllib.tree.DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo, dtImpurity, maxDepth, maxBins)


	val dtLabeledPredictions = testData.map { labeledPoint =>
	    val predictions = dtModel.predict(labeledPoint.features)
	    (labeledPoint.label, predictions)
	}
	
	val dtEvaluationMetrics = new MulticlassMetrics(dtLabeledPredictions.map(x => (x._1, x._2)))
	println("Decision Tree evaluationMetrics.precision:: " + dtEvaluationMetrics.precision)

	//confusionMatrix
	println("Decision Tree evaluationMetrics.confusionMatrix:: ")
	println(dtEvaluationMetrics.confusionMatrix)
	
	val boostingStrategy = BoostingStrategy.defaultParams("Classification")
	boostingStrategy.numIterations = 3 // Note: Use more iterations in practice.
	boostingStrategy.treeStrategy.numClasses = 2
	boostingStrategy.treeStrategy.maxDepth = 5
	val gbModel = GradientBoostedTrees.train(trainingData, boostingStrategy)
	
	val gbLabeledPredictions = testData.map { labeledPoint =>
	    val predictions = gbModel.predict(labeledPoint.features)
	    (labeledPoint.label, predictions)
	}
	
	val gbEvaluationMetrics = new MulticlassMetrics(gbLabeledPredictions.map(x => (x._1, x._2)))
	println("Gradient Boosted Tree evaluationMetrics.precision:: " + gbEvaluationMetrics.precision)

	//confusionMatrix
	println("Gradient Boosted Tree evaluationMetrics.confusionMatrix:: ")
	println(gbEvaluationMetrics.confusionMatrix)
	
    sc.stop()
  }
}