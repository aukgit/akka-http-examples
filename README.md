akka-http-examples
==================

Few examples on using akka-http

* simple - very basic example of a single project build based on https://github.com/akka/akka/blob/release-2.3-dev/akka-http-tests/src/test/scala/akka/http/server/TestServer.scala, with sbt-revolver enabled
* multiProject - two projects, API and Core. Former is to hold only RESTful API logic, latter is for other logic based on Akka

Todo:
* multi-project build, with static content based on sbt-web
