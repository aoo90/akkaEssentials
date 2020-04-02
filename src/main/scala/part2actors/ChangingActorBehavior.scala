package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChangingActorBehavior.Mom.MomStart

object ChangingActorBehavior extends App {

  object FussyKid{
    case object KidAccept
    case object KidReject //do you want to play?
    val HAPPY = "happy"
    val SAD = "sad"
  }

  class FussyKid extends Actor{
    import FussyKid._
    import Mom._
    var state = HAPPY
    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>{
        if(state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
      }
    }
  }

  class StatelessFussyKid extends Actor{
    import FussyKid._
    import Mom._
    override def receive: Receive = happyReceive
    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false) // change my receive handler to SadReceive
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }
    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) => context.unbecome() //context.become(happyReceive)
      case Ask(_) => sender() ! KidReject
    }
  }

  object Mom{
    case class MomStart(kidRef: ActorRef)
    case class Food(food: String)
    case class Ask(message: String) //do you want to play?
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }

  class Mom extends Actor{
    import Mom._
    import FussyKid._
    override def receive: Receive = {
      case MomStart(kidRef) =>
      // test our interaction
      kidRef ! Food(VEGETABLE)
      kidRef ! Food(VEGETABLE)
      kidRef ! Food(CHOCOLATE)
     // kidRef ! Food(CHOCOLATE)
     // kidRef ! Food(CHOCOLATE)
      kidRef ! Ask("do you want to play")
      case KidAccept => println("Yay, my kid is happy")
      case KidReject => println("My kid is sad but he is healthy")
    }
  }
  val system = ActorSystem("changingActorBehaviorDemo")
  val fussyKid = system.actorOf(Props[FussyKid], "FussyKid")
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid], "StatelessFussyKid")
  val mom = system.actorOf(Props[Mom], name = "Mom")
  mom!MomStart(statelessFussyKid)
}
