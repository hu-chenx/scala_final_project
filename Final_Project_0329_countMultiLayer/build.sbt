name := "Final"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
	"org.facebook4j" % "facebook4j-core" % "2.4.2",
	"com.typesafe.play" % "play_2.11" % "2.5.0",
	"io.reactivex" %% "rxscala" % "0.26.0",
	"com.restfb" % "restfb" % "1.20.0",
	"com.typesafe.akka" % "akka-actor_2.11" % "2.4.2",
	"eu.fakod" % "neo4j-scala_2.11" % "0.3.3"
)

