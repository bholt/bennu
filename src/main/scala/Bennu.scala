import java.io.FileInputStream
import java.util.Properties

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import StreamingContext._
import org.apache.spark.SparkContext._

object Bennu {

  def loadProperties(filename: String) = {
    val p = new Properties(System.getProperties)
    p.load(new FileInputStream(filename))
    System.setProperties(p)
  }

  def main(args: Array[String]) {
    loadProperties("twitterapi.properties")

    val conf = new SparkConf().setAppName("Bennu").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(1))    
    val stream = TwitterUtils.createStream(ssc, None)
    
    val hashTags = stream.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))

    val topCounts60 = hashTags.map(_ -> 1).reduceByKeyAndWindow(_ + _, Seconds(60))
                              .map(_.swap).transform(_.sortByKey(ascending=false))

    val topCounts10 = hashTags.map(_ -> 1).reduceByKeyAndWindow(_ + _, Seconds(10))
                              .map(_.swap).transform(_.sortByKey(ascending=false))

    // Print popular hashtags
    topCounts60.foreachRDD(rdd => {
      val topList = rdd.take(10)
      println("\nPopular topics in last 60 seconds (%s total):".format(rdd.count()))
      topList.foreach{case (count, tag) => println("%s (%s tweets)".format(tag, count))}
    })

    topCounts10.foreachRDD(rdd => {
      val topList = rdd.take(10)
      println("\nPopular topics in last 10 seconds (%s total):".format(rdd.count()))
      topList.foreach{case (count, tag) => println("%s (%s tweets)".format(tag, count))}
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
