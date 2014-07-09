import java.util.{Map => JMap}

import backtype.storm.Config
import backtype.storm.spout.SpoutOutputCollector
import backtype.storm.task.TopologyContext
import com.twitter.scalding.Args
import com.twitter.summingbird._
import com.twitter.summingbird.batch.Batcher
import com.twitter.summingbird.storm._
import com.twitter.tormenta.spout.TwitterSpout
import twitter4j._
import twitter4j.conf.{ConfigurationBuilder, Configuration}
import com.twitter.util.Future

//class MyTwitterSpout[+T](config: Configuration)
//  extends TwitterSpout[T](new TwitterStreamFactory(config),
//    TwitterSpout.QUEUE_LIMIT,
//    TwitterSpout.FIELD_NAME) {
//  override def open(conf: JMap[_, _], context: TopologyContext, coll: SpoutOutputCollector) {
//    collector = coll
//    stream = factory.getInstance
//    stream.addListener(listener)
//
//    // TODO: Add support beyond "sample". (GardenHose, for example.)
//    stream.sample()
//  }
//}

object BennuStorm {

  def JInt(i: Int) = new java.lang.Integer(i)

  implicit val timeOf: TimeExtractor[Status] = TimeExtractor(_.getCreatedAt.getTime)
  implicit val batcher = Batcher.ofHours(1)

  lazy val config = new ConfigurationBuilder()
    .setOAuthConsumerKey("ulFxJfWnhDMEa47Ua3WWlzBBd")
    .setOAuthConsumerSecret("qAAsr15yiMherHpPer2f9hUmqgcme4HcmQu61D4RShneywhFyt")
    .setOAuthAccessToken("133557422-Ja0bKaoX34e1b14r3ZeHebuS4CMb4C9p47U0c3l1")
    .setOAuthAccessTokenSecret("tF1HxKLH8PGx7JR8MOfKrlGnNAUnRfvBnfBVEHFztP4qD")
    .setJSONStoreEnabled(true) // required for JSON serialization
    .build

  val spout = TwitterSpout(new TwitterStreamFactory(config))

  val sink: StormSink[String] = Storm.sink { text: String =>
    println("@> " + text)
    Future.Unit
  }

  def wordCount[P <: Platform[P]](source: Producer[P, Status], sink: P#Sink[String]) =
    source
      // .filter(_.getText != null)
      .map { tweet: Status => tweet.getText }
      .write[String](sink)


  def main(args: Array[String]) {
    Executor(args, { args: Args =>
      new StormExecutionConfig {
        override val name = "Bennu"
        override def transformConfig(config: Map[String,AnyRef]) =
          config ++ List(Config.TOPOLOGY_ACKER_EXECUTORS -> JInt(0))
        override def getNamedOptions = Map("DEFAULT" -> Options())
        override def graph = wordCount[Storm](spout, sink)
      }
    })
  }
}
