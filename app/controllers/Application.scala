package controllers


import javax.inject.Inject

import actors.BasicActor.SayHello
import actors._
import akka.actor._
import models._
import play.api.Play.current
import play.api._
import play.api.i18n.Messages.Implicits._
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps



class Application @Inject() (ws: WSClient)(system: ActorSystem)(implicit ec: ExecutionContext)  extends Controller {

  // **************************************************
  // ACTIONS
  // **************************************************

  /**
    * index [ACTION]
    * @return search.scala.html
    */
  def index = Action { request =>
    Logger.debug("ACTION = index")
    Redirect(routes.Application.search())
  }

  /**
    * search [ACTION]
    * @param phrase
    * @param category
    * @return search.scala.html
    */
  def search(phrase: String="", category: String="") = Action.async {
    Logger.debug("ACTION = search(phrase="+phrase+", category="+category+")")


    TorrentSearchResult.search(ws, phrase, category).map(
      response => Ok(views.html.search(response, phrase, category))
    )
  }

  /**
    * poller [ACTION]
    * @return poller.scala.html
    */
  def poller(message:String="") = Action { request =>
    Logger.debug("ACTION = poller")

    val basicActor = system.actorOf(BasicActor.props, "hello-actor")

    val cancellable =
      system.scheduler.schedule(0 milliseconds,
        1 seconds,
        basicActor,
        SayHello(s"message : $message"))

    Thread sleep 60000

    //This cancels further Ticks to be sent
    cancellable.cancel()

    Ok(views.html.poller(message))


  }


/*
  implicit val timeout: Timeout = 5.seconds

  def sayHello(name: String) = Action.async {
    Logger.debug(s"ACTION = sayHello(name=$name)")


    val helloActor = system.actorOf(HelloActor.props, "hello-actor")

    (helloActor ? SayHello(name)).mapTo[String].map { message =>
      Ok(views.html.poller(message))
    }


  }
*/

}

