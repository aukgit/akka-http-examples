akka-http-examples
==================

Few examples on using akka-http

* simple - very basic example of a single project build based on https://github.com/akka/akka/blob/release-2.3-dev/akka-http-tests/src/test/scala/akka/http/server/TestServer.scala, with sbt-revolver enabled
* multiProject - two projects, API and Core. Former is to hold only RESTful API logic, latter is for other logic based on Akka
* multiProjectWebPlugin - Skeleton for a simple single page application using 4 subprojects: Model to store domain model and logic, Core - business logic based on Akka, WebUI - static content using SBT-Web, API - based on akka-http

How to use
----------
```
$ sbt
> update
> ~reStart
```
Start hakking

test from mobile