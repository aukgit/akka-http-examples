package net.batyuk.akkahttp.examples.api

import akka.http.marshallers.xml.ScalaXmlSupport
import akka.http.server.directives.AuthenticationDirectives._
import com.typesafe.config.{ ConfigFactory, Config }
import akka.actor.ActorSystem
import akka.pattern._
import scala.concurrent.duration._
import akka.util.Timeout
import akka.stream.FlowMaterializer
import akka.http.Http
import akka.http.server._
import net.batyuk.akkahttp.examples.core.PongActor

import scala.concurrent.duration.Duration

object TestServer extends App {
  val testConf: Config = ConfigFactory.parseString("""
    akka.loglevel = INFO
    akka.log-dead-letters = off""")
  implicit val system = ActorSystem("ServerTest", testConf)
  import system.dispatcher
  implicit val materializer = FlowMaterializer()
  implicit val timeout = Timeout(5 seconds)

  import ScalaXmlSupport._
  import Directives._

  def auth =
    HttpBasicAuthenticator.provideUserName {
      case p @ UserCredentials.Provided(name) ⇒ p.verifySecret(name + "-password")
      case _                                  ⇒ false
    }

  val binding = Http().bind(interface = "localhost", port = 8080)

  val materializedMap = binding startHandlingWith {
    get {
      path("") {
        complete(index)
      } ~ pathPrefix("static") {
        getFromResourceDirectory("static/")
      } ~
        path("secure") {
          HttpBasicAuthentication("My very secure site")(auth) { user ⇒
            complete(<html><body>Hello <b>{ user }</b>. Access has been granted!</body></html>)
          }
        } ~
        path("ping") {
          complete("PONG!")
        } ~
        path("coreping") {
          complete((system.actorOf(PongActor.props) ? "ping").mapTo[String])
        } ~
        path("crash") {
          complete(sys.error("BOOM!"))
        } ~
        path("shutdown") {
          shutdown
          complete("SHUTDOWN")
        }
      }
    }

  def shutdown(): Unit = binding.unbind(materializedMap).onComplete(_ ⇒ system.shutdown())

  lazy val index =
    <html>
      <body>
        <h1>Say hello to <i>akka-http-core</i>!</h1>
        <p>Defined resources:</p>
        <ul>
          <li><a href="/ping">/ping</a></li>
          <li><a href="/coreping">/coreping</a> - ping from actor in another project</li>
          <li><a href="/static/index.html">/static/index.html</a> - serving static content from the src/main/resources</li>
          <li><a href="/secure">/secure</a> Use any username and '&lt;username&gt;-password' as credentials</li>
          <li><a href="/crash">/crash</a></li>
          <li><a href="/shutdown">/shutdown</a> - never do this in production :-)</li>
        </ul>
      </body>
    </html>
}
