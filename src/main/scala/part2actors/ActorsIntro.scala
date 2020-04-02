package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App{
  // part1 - actor systems
  var actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  // part2 - create actors
  // word count actor
  class WordCountActor extends Actor {
    // internal data
    var totalWorlds = 0

    // behavior
    def receive: PartialFunction[Any, Unit] = {
      case message: String =>
        println(s"I have received:$message")
        totalWorlds += message.split(" ").length
      case msg => println(s"[word counter] I cannot understand ${msg.toString}")
    }
  }

  // part3 - instantiate our actor
  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val anotherWordCounter = actorSystem.actorOf(Props[WordCountActor], "anotherWordCounter")

  // part4 - communicate!
  wordCounter ! "I am learning akka"  // "tell"
  anotherWordCounter ! "I am learning akka from another actor"

  class Person(name: String) extends Actor{
    override def receive: Receive = {
      case "hi" => println(s"Hi, my name is $name")
      case _ =>
    }
  }

  object PersonPropFactory{
    def props(name: String) = {Props(new Person(name))}
  }

  val person = actorSystem.actorOf(Props(new Person("bob")), "person")
  person! "hi"

  val anotherPerson = actorSystem.actorOf(PersonPropFactory.props("jon"), "anotherPerson")
  anotherPerson! "hi"
}
