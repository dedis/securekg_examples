organization := "ch.epfl.dedis"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.6"
name := "securekg-example"

lazy val root = project in file(".")

libraryDependencies += "com.typesafe" % "config" % "1.3.2"
libraryDependencies += "ch.epfl.dedis" % "cothority" % "master-181211"

// Use when testing new releases (also uncomment the 4 below)
//libraryDependencies += "ch.epfl.dedis" % "cothority" % "master-181207" from "file:///Users/jallen/.m2/repository/ch/epfl/dedis/cothority/master-181207/cothority-master-181207.jar"

// Dependencies normally brought in by Ivy automatically
//libraryDependencies += "com.google.protobuf" % "protobuf-java" % "3.6.1"
//libraryDependencies += "com.moandjiezana.toml" % "toml4j" % "0.7.2"
//libraryDependencies += "org.java-websocket" % "Java-WebSocket" % "1.3.9"
//libraryDependencies += "net.i2p.crypto" % "eddsa" % "0.2.0"

libraryDependencies += "com.typesafe.play" %% "play" % "2.6.15"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.3"
libraryDependencies += "com.typesafe.play" %% "play-logback" % "2.6.15"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
