package actors

import actors.BasicActor.SayHello
import akka.actor._
import play.api.Logger

object BasicActor {
  def props = Props[BasicActor]

  case class SayHello(name: String)
}

class BasicActor extends Actor {

  def receive = {
    case SayHello(name: String) => {
      Logger.info(s"SayHello : $name")
    }
  }
}

