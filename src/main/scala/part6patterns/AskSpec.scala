package part6patterns

import akka.actor.Status.Success
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.Span

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
//step 1
import akka.pattern.ask
/*
class AskSpec extends TestKit (ActorSystem("AskSpec")) with ImplicitSender with AnyWordSpecLike with BeforeAndAfterAll{
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}


object AskSpec{
  //this code is somewhere else in your app
  case class Read(key: String)
  case class Write(key: String, value: String)
  class KVActor extends Actor with ActorLogging{
    override def receive: Receive = online(Map())

    def online(kv: Map[String, String]): Receive = {
      case Read(key) =>
        log.info(s"Trying to read the value at the key $key")
        sender() ! kv.get(key)  //Option[String]

      case Write(key, value) =>
        log.info(s"Writing the value $value for the key $key")
        context.become(online(kv + (key -> value)))
    }

    //user authenticator actor
    case class RegisterUser(username: String, password: String)
    case class Authenticate(username: String, password: String)
    case class AuthFailure(message: String)
    case object AuthSuccess

    class AuthManager extends Actor with ActorLogging{
      //step 2 - logistics
      implicit val timeout: Timeout = Timeout(1 second)
      implicit val executionContext: ExecutionContext = context.dispatcher

      private val authDb = context.actorOf((Props[KVActor]))
      override def receive : Receive = {
        case RegisterUser(username, password) => authDb ! Write(username, password)
        case Authenticate(username, password) =>
          val future = authDb ?  Read(username)
          future.onComplete{
            case Success(None) => sender() ! AuthFailure("password not found")
            case Success(Some(password)) =>

          })
      }
    }
  }
}

 */