package controllers


import play.api._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import models._
import javax.inject.Inject
import scala.concurrent.Future
import play.api.mvc._
import play.api.libs.ws._


class Application @Inject() (ws: WSClient) extends Controller {

  // ACTIONS
  def index = Action { request =>
    Logger.debug("ACTION = index")
    Redirect(routes.Application.search())
  }

  def search(phrase: String="", category: String="") = Action.async {
    Logger.debug("ACTION = search(phrase="+phrase+", category="+category+")")

    implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

    val result:Future[TorrentSearchResult] = TorrentSearchResult.search(ws, phrase, category)

    result.map(
      response => Ok(views.html.search(response, phrase, category))
    )
  }

  def poller() = Action { request =>
    Ok(views.html.poller())
  }

}
            
