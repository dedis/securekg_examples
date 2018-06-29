name := "securekg-example"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "ch.epfl.dedis" % "cothority" % "2.0.1",

  // Test dependencies:
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
)

