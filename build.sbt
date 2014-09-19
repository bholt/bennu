name := "Bennu"

version := "0.1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-streaming_2.10" % "1.1.0",
  "org.apache.spark" % "spark-streaming-twitter_2.10" % "1.1.0"
)
