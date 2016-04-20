name := """Final_FB_recommend_UI"""

version := "1.0-SNAPSHOT"

lazy val root = project.in(file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq( 
    
"org.scalatest" %% "scalatest" % "2.2.4" % "test",
"org.apache.spark" % "spark-core_2.11" % "1.6.1",

"org.apache.spark" % "spark-mllib_2.11" % "1.6.1",

"com.restfb" % "restfb" % "1.20.0",

"eu.fakod" % "neo4j-scala_2.11" % "0.3.3",

"com.typesafe.akka" % "akka-actor_2.11" % "2.4.4",


"org.sorm-framework" % "sorm" % "0.3.8",

jdbc,
  cache,
  ws

).map(_.exclude("org.slf4j", "slf4j-log4j12"))




fork in run := true