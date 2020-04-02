package part2actors
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object IntroAkka extends App {


  class SimpleLogginActor extends Actor with ActorLogging{
    override def receive: Receive = {
      case message => log.info(message.toString())
    }
  }

  //class SimpleLoggingActor

  val configString =
    """
      | akka {
      |   loglevel = "ERROR"
      |   #loglevel = "INFO"
      |   #loglevel = "DEBUG"
      | }
      |""".stripMargin
  //1 string config
  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("ConfigurationDemo", ConfigFactory.load(config))
  val actor = system.actorOf(Props[SimpleLogginActor])
  actor ! "A message to remember"

  //2 default config file
  val defaultConfigFileSystem = ActorSystem("DefaultConfigFileDemo")
  val defaultConfigActor = defaultConfigFileSystem.actorOf(Props[SimpleLogginActor])
  defaultConfigActor ! "Remember me"

  //3 special separate config in the same file
  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialConfigSystem = ActorSystem("SpecialConfigDemo", specialConfig)
  val specialConfigActor = specialConfigSystem.actorOf(Props[SimpleLogginActor])
  specialConfigActor ! "Remember me"

  //4 separate config in another file
  val separateConfig = ConfigFactory.load("secretFolder/secretConfigration.conf")
}
