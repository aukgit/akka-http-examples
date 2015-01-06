lazy val commonSettings = Seq(
  organization := "net.batyuk",
  version := "0.0.1",
  scalaVersion := "2.11.4"
)

lazy val root = (project in file(".")) aggregate (core, api)

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.8"
  )

lazy val api = (project in file("api"))
  .settings(commonSettings: _*)
  .settings(Revolver.settings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-M2",
      "com.typesafe.akka" %% "akka-http-experimental" % "1.0-M2",
      "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-M2",
      "com.typesafe.akka" %% "akka-http-xml-experimental" % "1.0-M2"
    )
  )
  .dependsOn(core)




