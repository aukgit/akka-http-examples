lazy val commonSettings = Seq(
  organization := "net.batyuk",
  version := "0.0.1",
  scalaVersion := "2.11.4"
)

lazy val root = (project in file(".")) aggregate (core, api)

lazy val model = (project in file("model"))
  .settings(commonSettings: _*)

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.8"
  )
  .dependsOn(model)

lazy val webui = (project in file("webui"))
  .settings(commonSettings: _*)
  .enablePlugins(SbtWeb)
  .settings(
  	libraryDependencies ++= Seq(
  		"org.webjars" % "react" % "0.12.2",
  		"org.webjars" % "request" % "2.36.1-1"
	  ),
    WebKeys.packagePrefix in Assets := "public/"
  )
  .settings(JsEngineKeys.engineType := JsEngineKeys.EngineType.Node)
  .settings((managedClasspath in Runtime) += (packageBin in Assets).value)

lazy val api = (project in file("api"))
  .settings(commonSettings: _*)
  .settings(Revolver.settings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-M2",
      "com.typesafe.akka" %% "akka-http-experimental" % "1.0-M2",
      "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-M2",
      "com.typesafe.akka" %% "akka-http-xml-experimental" % "1.0-M2",
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "1.0-M2",
      "io.spray" %%  "spray-json" % "1.3.1"
    )
  )
  .dependsOn(webui % "compile;test->test;runtime", model, core)




