name := "Bennu"

version := "0.1.0"

resolvers += "twitter" at "http://maven.twttr.com"

val finagleVersion = "6.16.0"
val summingbirdVersion = "0.4.2"
val tormentaVersion = "0.7.0"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-core" % finagleVersion,
  "com.twitter" %% "finagle-http" % finagleVersion,
  "com.twitter" %% "finagle-stream" % finagleVersion,
//  "com.twitter" %% "summingbird-core" % summingbirdVersion,
  "com.twitter" %% "summingbird-storm" % summingbirdVersion,
//  "com.twitter" %% "summingbird-client" % summingbirdVersion,
//  "com.twitter" %% "tormenta-core" % tormentaVersion,
  "com.twitter" %% "tormenta-twitter" % tormentaVersion,
//  "org.apache.storm" % "storm-core" % "0.9.2",
  "storm" % "storm" % "0.9.0-wip15" % "provided",
  "org.twitter4j" % "twitter4j-stream" % "4.0.2"
)
