import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.ServiceFactory
import com.twitter.finagle.stream.{Stream, StreamResponse}
import com.twitter.conversions.time._
import com.twitter.io.Charsets
import com.twitter.util.{Base64StringEncoder => Base64, Future}
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpVersion, HttpMethod, DefaultHttpRequest}


object Client {

  def main(args: Array[String]) {
    // val (key,secret,token,tokenSecret,path) = args.slice(0, 5)
    val (password, path) = (args(0), args(1))

    val clientFactory: ServiceFactory[HttpRequest, StreamResponse] =
      ClientBuilder()
        .codec(Stream())
        .hosts("api.twitter.com:80")
        .tcpConnectTimeout(1.microsecond)
        .hostConnectionLimit(1)
        .buildFactory()

    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path)
    request.headers.set("Authorization", "Basic " +
      Base64.encode("holtbg:$password".getBytes("UTF-8")))
    request.headers.set("User-Agent", "Finagle 0.0")
    request.headers.set("Host", "api.twitter.com:80")

    println(request)

    for { client <- clientFactory(); streamResponse <- client(request) } {
      val httpResponse = streamResponse.httpResponse
      if (httpResponse.getStatus.getCode != 200) {
        println(httpResponse.toString)
        client.close()
        clientFactory.close()
      } else {
        var messageCount = 0 // Wait for 1000 messages then shut down.
        streamResponse.messages foreach { buffer =>
          messageCount += 1
          println(buffer.toString(Charsets.Utf8))
          println("--")
          if (messageCount == 1000) {
            client.close()
            clientFactory.close()
          }
          // We return a Future indicating when we've completed processing the message.
          Future.Done
        }
      }
    }

//    val client = clientFactory()
//    val stream = client(request)
//
//    stream onSuccess { resp =>
//      resp.messages foreach { buff =>
//        println(buff.toString(Charsets.Utf8))
//      }
//    }

  }

}