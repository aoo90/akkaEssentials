package part6patterns

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}

object StashDemo extends App {
  /*
    ResourceActor
      - open => it can receive read/write requests to the resource
      - otherwise it will postpone all read/write requests until the state is open

      ResourceActor is closed
        - Open => switch to open state
        - Read, Write messages are POSTPONED

      ResourceActor is open
        - Read, Write are handled
        - Close => switch to the closed state


  */
  case object Open
  case object Close
  case object Read
  case class Write(data: String)

  //step 1 - mix-in the stash trait
  class ResourceActor extends Actor with ActorLogging with Stash{
    private var innerData: String = ""

    override def receive: Receive = closed

    def closed: Receive ={
      case Open =>
        log.info("Opening resource")
        //step 3 - unstashAll when you switch the message handler
        unstashAll()
        context.become(open)
      case message =>
        log.info(s"Stashing $message because I cannot handle it in the closed state")
        //step 2 - stash away what you cannot handle
        stash()
    }

    def open: Receive = {
      case Read =>
        // do some actual computation
        log.info(s"I have read $innerData")
      case Write(data) =>
        log.info(s"I am writing $data")
        innerData = data
      case Close =>
        log.info("Closing resource")
        context.become(closed)
      case message =>
        log.info(s"Stashing $message because I cannot handle it in the open state")
        stash()
    }
  }

  val system = ActorSystem("StashDemo")
  val resourceActor = system.actorOf(Props[ResourceActor])

  //resourceActor ! Write("I love stash")
  //resourceActor ! Read
  //resourceActor ! Open


  resourceActor ! Read  //stashed
  resourceActor ! Open  //switch to open
  resourceActor ! Open  //stashed, don't know how to handle
  resourceActor ! Write("I have stash")
  resourceActor ! Close
  resourceActor ! Open
  resourceActor ! Read
}
