name := "bennu"

version := "0.1.0"

resolvers += "twitter" at "http://maven.twttr.com"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-core" % "6.16.0",
  "com.twitter" %% "finagle-http" % "6.16.0",
  "com.twitter" %% "finagle-stream" % "6.16.0"
)
