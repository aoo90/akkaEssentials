package part6patterns

import akka.actor.Status.Success
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Cancellable, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.Span
import part6patterns.FSMSpec.{Initialize, Instruction, RequestProduct, VendingError, VendingMachine}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class FSMSpec extends TestKit(ActorSystem("FSMSpec")) with ImplicitSender with AnyWordSpecLike with BeforeAndAfterAll {
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A vending machine" should{
    "error when not initialized" in {
      val vendingMachine = system.actorOf(Props[VendingMachine])
      vendingMachine ! RequestProduct("coke")
      expectMsg(VendingError("MachineNotInitiazlied"))
    }

    "report a product not available" in {
      val vendingMachine = system.actorOf(Props[VendingMachine])
      vendingMachine ! Initialize(Map("coke" -> 10), Map("coke" -> 1))
      vendingMachine ! RequestProduct("sandwich")
      expectMsg(VendingError("ProductNotAvailable"))
    }

    "throw a timeout if I don't insert money" in{
      val vendingMachine = system.actorOf(Props[VendingMachine])
      vendingMachine ! Initialize(Map("coke" -> 10), Map("coke" -> 1))
      vendingMachine ! RequestProduct("coke")
      expectMsg(Instruction("Please insert 1 dollars"))
      within(1.5 seconds){
        expectMsg(VendingError("RequestMoneyTimeOut"))
      }
    }
  }
}


object FSMSpec {

  //vending machine
  case class Initialize(inventory: Map[String, Int], prices: Map[String, Int])

  case class RequestProduct(product: String)

  case class Instruction(instruction: String) //message the VM will show on its "screen
  case class ReceiveMoney(amount: Int)

  case class Deliver(product: String)

  case class GiveBackChange(amount: Int)

  case class VendingError(reason: String)

  case object ReceiveMoneyTimeout

  class VendingMachine extends Actor with ActorLogging {
    implicit val executionContext: ExecutionContext = context.dispatcher

    override def receive: Receive = idle

    def idle: Receive = {
      case Initialize(inventory, prices) => context.become(operational(inventory, prices))
      case _ => sender() ! VendingError("MachineNotInitiazlied")
    }

    def operational(inventory: Map[String, Int], prices: Map[String, Int]): Receive = {
      case RequestProduct(product) => inventory.get(product) match {
        case None | Some(0) =>
          sender() ! VendingError("ProductNotAvailable")
        case Some(_) =>
          val price = prices(product)
          sender() ! Instruction(s"Please insert $price dollars")
          context.become(waitForMoney(inventory, prices, product, price,
            startReceiveMoneyTimeoutSchedule, sender()))
      }
    }

    def waitForMoney(inventory: Map[String, Int],
                     prices: Map[String, Int],
                     product: String,
                     money: Int,
                     moneyTimeoutSchedule: Cancellable,
                     requester: ActorRef
                    ): Receive = {
      case ReceiveMoneyTimeout =>
        requester ! VendingError("RequestMoneyTimeOut")
        if(money > 0) requester ! GiveBackChange(money)
        context.become(operational(inventory, prices))
      case ReceiveMoney(amount) =>
        moneyTimeoutSchedule.cancel()
        val price = prices(product)
        if(money > price){
          requester ! Deliver(product)

        }
    }

    def startReceiveMoneyTimeoutSchedule = context.system.scheduler.scheduleOnce(1 second) {
      self ! ReceiveMoneyTimeout
    }
  }

  //step 1 - define the states and the data of the actor
  trait VendingState
  case object Idle extends VendingState
  case object Operational extends VendingState
  case object WaitForMoney extends VendingState


  trait VendingData
  case object Uninitialized extends VendingData
  case class Initialized(inventory: Map[String, Int], prices: Map[String, Int]) extends VendingData

}
