package net.batyuk.akkahttp.examples.api

import com.typesafe.config.{ ConfigFactory, Config }

import akka.stream.FlowMaterializer
import akka.actor.ActorSystem
import akka.pattern._
import akka.stream.scaladsl.Flow
import akka.http.Http
import akka.http.server._
import akka.http.model.{HttpResponse, HttpRequest, StatusCodes}
import akka.http.server.directives.AuthenticationDirectives._
import scala.concurrent.duration._
import akka.util.Timeout
import spray.json.DefaultJsonProtocol
import net.batyuk.akkahttp.examples.model.Record
import net.batyuk.akkahttp.examples.core.PongActor

import scala.concurrent.duration.Duration

import scala.collection.JavaConversions._

object TestServer extends App {
  val testConf: Config = ConfigFactory.parseString("""
    akka.loglevel = INFO
    akka.log-dead-letters = off""")
  implicit val system = ActorSystem("ServerTest", testConf)
  import system.dispatcher
  implicit val materializer = FlowMaterializer()
  implicit val timeout = Timeout(5 seconds)

  import akka.http.marshallers.xml.ScalaXmlSupport._
  import Directives._
  import akka.http.marshallers.sprayjson.SprayJsonSupport._


  object RecordProtocol extends DefaultJsonProtocol {
    implicit val recordFormat = jsonFormat3(Record.apply)
  }

  import RecordProtocol._

  def recordList = for(id <- 1 to 5) yield Record(id, "test-"+id, "category-"+id)


  def auth =
    HttpBasicAuthenticator.provideUserName {
      case p @ UserCredentials.Provided(name) ⇒ p.verifySecret(name + "-password")
      case _                                  ⇒ false
    }

  val binding = Http().bind(interface = "localhost", port = 8080)

  val materializedMap = binding startHandlingWith Route.handlerFlow {
    get {
      path("") {
        redirect("web/index.html", StatusCodes.Found)
      } ~ pathPrefix("web") {
        getFromResourceDirectory(".")
      } ~
        path("secure") {
          HttpBasicAuthentication("My very secure site")(auth) { user ⇒
            complete(<html><body>Hello <b>{ user }</b>. Access has been granted!</body></html>)
          }
        } ~
        path("ping") {
          complete("PONG directly from the Route!")
        } ~
        path("coreping") {
          complete((system.actorOf(PongActor.props) ? "ping").mapTo[String])
        } ~
        path("crash") {
          complete(sys.error("BOOM!"))
        } ~
        path("json") {
          complete(recordList)
        }~
        path("shutdown") {
          shutdown
          complete("SHUTDOWN")
        }
      }
    }


  def shutdown(): Unit = binding.unbind(materializedMap).onComplete(_ ⇒ system.shutdown())

}
