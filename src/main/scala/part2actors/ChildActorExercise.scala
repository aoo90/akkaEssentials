package part2actors

import akka.actor.Actor

class ChildActorExercise extends App {
  // distributed word counting
  object WordCounterMaster{
    case class Initialize(nChildren: Int)
    case class WordCountTask(text: String)
    case class WordCountReply(count: Int)
  }
  class WordCounterMaster extends Actor{
    override def receive: Receive = ???
  }

  class WordCounterWorder extends Actor{
    override def receive: Receive = ???
  }

}
