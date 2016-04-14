name := "final_project"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"


resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.2.1"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.2.1"

libraryDependencies += "org.apache.spark" % "spark-mllib_2.11" % "1.2.1"

libraryDependencies += "org.facebook4j" % "facebook4j-core" % "2.4.2"

libraryDependencies += "com.typesafe.play" % "play_2.11" % "2.5.0"

libraryDependencies += "io.reactivex" %% "rxscala" % "0.26.0"

libraryDependencies += "com.restfb" % "restfb" % "1.20.0"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.2"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.1.3"

libraryDependencies += "eu.fakod" % "neo4j-scala_2.11" % "0.3.3"




