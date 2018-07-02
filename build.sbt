organization := "ch.epfl.dedis"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.6"
name := "securekg-example"

lazy val root = project in file(".")

//libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
//libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"



libraryDependencies += "com.typesafe" % "config" % "1.3.2"

libraryDependencies += "ch.epfl.dedis" % "cothority" % "2.0.1"

libraryDependencies += "com.typesafe.play" %% "play" % "2.6.15"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.3"

libraryDependencies += "com.typesafe.play" %% "play-logback" % "2.6.15"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
