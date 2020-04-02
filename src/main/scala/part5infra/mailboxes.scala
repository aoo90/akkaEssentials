package part5infra

import akka.actor.AbstractActor.Receive
import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

object mailboxes extends  App{
  val system = ActorSystem("MailboxDemo")
  class SimpleActor extends Actor with ActorLogging{
    override def receive: Receive ={
      case message => log.info(message.toString)
    }
  }

  //interesting case #1 - custom priority mailbox
  //class SupportTicketPriorityMailbox(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(PriorityGenerator)

}
