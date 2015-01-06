scalaVersion := "2.11.4"

Revolver.settings

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-stream-experimental_2.11" % "1.0-M2",
  "com.typesafe.akka" % "akka-http-experimental_2.11" % "1.0-M2",
  "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "1.0-M2",
  "com.typesafe.akka" % "akka-http-xml-experimental_2.11" % "1.0-M2"
)
