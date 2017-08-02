name := "PlayTest"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws-standalone" % "1.0.4",
  "com.typesafe.play" %% "play-ws-standalone-xml" % "1.0.4",
  "com.typesafe.play" %% "play-ws-standalone-json" % "1.0.4",
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.4",
  "com.typesafe.play" % "shaded-asynchttpclient" % "1.0.4",
  "com.typesafe.play" % "shaded-oauth" % "1.0.4"
)

libraryDependencies += ws

