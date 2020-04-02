package part5infra
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object Dispatchers extends App{
  class Counter extends Actor with ActorLogging{
    var count = 0

    override def receive: Receive = {
      case message =>
        count += 1
        log.info(s"[$count] $message")
    }
  }

  val system = ActorSystem("DispatcherDemo") //ActorSystem("DispatcherDemo", ConfigFactory.load().getConfig("dispatcherDemo"))

  //method 1 programmatically
  //my-dispatcher from application.conf
  val actors = for(i <- 1 to 10) yield system.actorOf(Props[Counter].withDispatcher("my-dispatcher"), s"counter_$i")

  /*val r = new Random()
  for(i <- 1 to 1000){
    actors(r.nextInt(10)) ! i
  }*/

  //method 2 from config
  val rtjvmActor = system.actorOf(Props[Counter], "rtjvm")
  /*val r = new Random()
  for(i <- 1 to 1000){
    rtjvmActor ! i
  }*/

  //Dispatchers implement the executionContext trait
  //receive message in future
  class DBActor extends Actor with ActorLogging{
    implicit val executionContext: ExecutionContext = context.dispatcher
    override def receive : Receive = {
      case message => Future {
        //waiting/blocking, not good to use
        Thread.sleep(5000)
        log.info(s"Success: $message")
      }
    }
  }

  val dbActor = system.actorOf(Props[DBActor], "dbActor")
  dbActor ! "the meaning of life is 42"

  val nonblockingActor = system.actorOf(Props[Counter])
  for(i <- 1 to 1000)
  {
    val message = s"important message $i"
    dbActor ! message
    nonblockingActor ! message
  }


}
