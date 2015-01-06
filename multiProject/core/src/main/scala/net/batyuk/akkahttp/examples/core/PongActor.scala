package net.batyuk.akkahttp.examples.core

import akka.actor._

class PongActor extends Actor {
  def receive = {
    case _ => sender ! "PONG! from other project"
  }

}

object PongActor {
  def props = Props[PongActor]
}