package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ActorCapabilities.BankAccount.{Deposit, Statement, Withdraw}
import part2actors.ActorCapabilities.Counter.{Decrement, Increment, Print}
import part2actors.ActorCapabilities.Person.LiveTheLife

object ActorCapabilities extends App{

  class SimpleActor extends Actor{
    //context has access to ActorSystem and it environment
    override def receive: Receive = {
          //Actor[akka://actorCapabilitiesDemo/user/SimpleActor#-1062519291] with unique identifier
      case "Hi!" => sender() ! "Hello, there!"  //reply to sender
      case message: String => println(s"${context.self}  ${context.self.path}. simple actor I have received $message")
      case number: Int => println(s"simple actor I have receive $number")
      case SpecialMessage(contents) => println(s"Special message: $contents")
      case SendMessageToYourself(content) => self ! content
      case SayHiTo(ref: ActorRef) => ref ! "Hi!"
      case WirelessPhoneMessage(content, ref) => ref forward (content + "s")
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")

  var simpleActor = system.actorOf(Props[SimpleActor], "SimpleActor")

  simpleActor ! "Hello, actor"
  simpleActor ! 42    //who is the sender

  // 1 - messages can be of any type
  // a - messages must be Immutable
  // b - messages must be serializable

  case class SpecialMessage(contents: String)
  simpleActor ! SpecialMessage("some special content")
  // 2 - actors have information about their context and about themselves
  // context.self == this
  // self == context.self



  case class SendMessageToYourself(content:String)
  simpleActor ! SendMessageToYourself("I am an actor and I am proud of it")

  //3 - actors can reply to messages
  var alice = system.actorOf(Props[SimpleActor], "alice")
  var bob = system.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(ref: ActorRef)
  alice ! SayHiTo(bob)

  // 5 - forwarding messages
  case class WirelessPhoneMessage(content: String, ref: ActorRef)
  alice ! WirelessPhoneMessage("Hi", bob)


  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor{
    import Counter._
    var count = 0
    override def receive: Receive = {
      case Increment => count += 1
      case Decrement => count -= 1
      case Print => println(s"[counter] My current count is $count")
    }
  }

  val counter = system.actorOf(Props[Counter], "CounterActor")
  counter!Increment
  counter!Print
  counter!Decrement
  counter!Print


  object BankAccount{
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object Statement
    case class TransactionSuccess(message: String)
    case class TransactionFailure(message: String)
  }

  class BankAccount extends Actor{
    import BankAccount._
    var funds = 0
    override def receive: Receive = {
      case Deposit(amount) => {
        if(amount<0) sender()!TransactionFailure("invalid deposit amount")
        else {
          funds += amount
          sender() ! TransactionSuccess(s"Successfully deposited $amount")
        }
      }
      case Withdraw(amount) => {
        if(amount<0) sender()!TransactionFailure("invalid withdraw amount")
        else if(funds < amount) sender()!TransactionFailure("insufficient funds")
        else {
          funds -= amount
          sender() ! TransactionSuccess(s"Successfully withdraw $amount")
        }
      }
      case Statement=> sender()!s"Your balance is $funds"
    }
  }
  object Person {
    case class LiveTheLife(account: ActorRef)
  }
  class Person extends Actor{
    import Person._

    override def receive: Receive = {
      case LiveTheLife(account) =>{
        account ! Deposit(10000)
        account ! Withdraw(90000)
        account ! Withdraw(500)
        account ! Statement
      }
      case message => println(message.toString)
    }
  }

  val account = system.actorOf(Props[BankAccount])
  val person = system.actorOf(Props[Person], "billionaire")
  person!LiveTheLife(account)
}

