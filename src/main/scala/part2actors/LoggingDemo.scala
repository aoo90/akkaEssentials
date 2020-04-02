package part2actors

import akka.actor.{Actor, ActorLogging}
import akka.event.Logging

object LoggingDemo extends App {
  //# 1 explicit logging
  class MyLogger extends Actor {
    var logger = Logging(context.system, this)

    override def receive: Receive = {
      case message => logger.info(message.toString())
    }
  }
  // #2 - ActorLogging
  class AnotherLogger extends Actor with ActorLogging{
    override def receive: Receive = {
      case (a, b) => log.info("Two things: {} and {}", a, b)
      case message => log.info(message.toString())
    }
  }
}
