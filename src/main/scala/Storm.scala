import com.twitter.scalding.Args
import com.twitter.summingbird._
import com.twitter.summingbird.storm._
import com.twitter.tormenta.spout.TwitterSpout
import twitter4j._
import twitter4j.conf.ConfigurationBuilder
import com.twitter.util.Future

object BennuStorm {

  import Serialization._, StatusStreamer._

  def main(args: Array[String]) {
    Executor(args, { args: Args =>
      new StormExecutionConfig {
        override val name = "Bennu"
        override def transformConfig(config: Map[String,AnyRef]) = config
        override def getNamedOptions = Map("DEFAULT" -> Options())
        override def graph = wordCount[Storm](spout, sink)
      }
    })
  }

  lazy val config = new ConfigurationBuilder()
    .setOAuthConsumerKey("7d4EplXcJkDkyV9rjFkfafdAF")
    .setOAuthConsumerSecret("RX1P8N4KyO95R9BL7ziDaXOzedB7QQS9TRjikxXzQjjZYrfZUf")
    .setOAuthAccessToken("133557422-Ja0bKaoX34e1b14r3ZeHebuS4CMb4C9p47U0c3l1")
    .setOAuthAccessTokenSecret("tF1HxKLH8PGx7JR8MOfKrlGnNAUnRfvBnfBVEHFztP4qDg")
    .setJSONStoreEnabled(true) // required for JSON serialization
    .build

  val spout = TwitterSpout(new TwitterStreamFactory(config))

  val sink: StormSink[String] = Storm.sink { text: String => Future { println(text) } }

  def wordCount[P <: Platform[P]](source: Producer[P, Status], sink: P#Sink[String]) =
    source
      .filter(_.getText != null)
      .map { tweet: Status => tweet.getText }
      .write[String](sink)
}
