import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._

object Bennu {
  
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Bennu").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(1))
    
    println("@> consumerKey: " + System.getProperty("twitter4j.oauth.consumerKey"))
    
  }
}