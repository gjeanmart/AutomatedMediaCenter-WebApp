package models

import org.joda.time.DateTime
import play.api.Play.current
import play.api._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.Future
import scala.language.postfixOps

case class Torrent(hash: String, title: String,category: String, seeds: Int, leeches: Int, size: Int, date: DateTime, imDbId: Option[String])
case class TorrentSearchResult(nbResult  : Int, time: Double, code: Int, torrents  : Seq[Torrent])


object TorrentSearchResult {

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  implicit val timeStampReader = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      new DateTime(dtString.toLong*1000)
    )
  )

  implicit val torrentReads: Reads[Torrent] = (
    (JsPath \ "torrent_hash").read[String] and
      (JsPath \ "torrent_title").read[String] and
      (JsPath \ "torrent_category").read[String] and
      (JsPath \ "seeds").read[Int] and
      (JsPath \ "leeches").read[Int] and
      (JsPath \ "size").read[Int] and
      (JsPath \ "upload_date").read[DateTime](timeStampReader) and
      (JsPath \ "imdbid").readNullable[String]
    )(Torrent.apply _)

  implicit val torrentSearchResultReads: Reads[TorrentSearchResult] = (
    (JsPath \ "results").read[Int] and
      (JsPath \ "responsetime").read[Double] and
      (JsPath \ "statuscode").read[Int] and
      (JsPath \ "torrents").read[Seq[Torrent]]
    )(TorrentSearchResult.apply _)

  def search(ws: WSClient, phrase: String, category: String, page: Int = 0, pageSize: Int = 10, orderBy: Int = 1): Future[TorrentSearchResult] = {

    Logger.debug(s"[START] TorrentSearchResult.search(phrase=$phrase, category=$category, page=$page, pageSize=$pageSize, orderBy=$orderBy)")

    val url = current.configuration.getString("services.getstrike.url")
    Logger.debug(s"GetStrike API url = ${url.get}")
    val timeout = current.configuration.getInt(" services.getstrike.timeoutSec")
    Logger.debug(s"GetStrike API timeout = ${timeout.get}")

    var request: WSRequest = null
    if(!category.isEmpty) {
      request = ws.url(url.get)
        .withHeaders("Accept" -> "application/json")
        .withRequestTimeout(timeout.get * 1000)
        .withQueryString("phrase" -> phrase)
        .withQueryString("category" -> category)
    } else {
      request = ws.url(url.get)
        .withHeaders("Accept" -> "application/json")
        .withRequestTimeout(timeout.get * 1000)
        .withQueryString("phrase" -> phrase)
    }

    val result:Future[TorrentSearchResult]  = request.get()
      .map {
        response =>
          response.status match {
            case 200 => {
              val asynchResult: JsResult[TorrentSearchResult] = response.json.validate[TorrentSearchResult]
              Logger.debug(s"[END] TorrentSearchResult.search(phrase=$phrase, category=$category, page=$page, pageSize=$pageSize, orderBy=$orderBy) : ${asynchResult.get}")

              asynchResult.get
            }
            case _ => {
              Logger.debug(s"[END] TorrentSearchResult.search(phrase=$phrase, category=$category, page=$page, pageSize=$pageSize, orderBy=$orderBy) : Not OK: ${response.status} - body is: ${response.body}")

              val asynchResult = new TorrentSearchResult(0, 0, response.status, null)

              asynchResult
            }
          }
      }

    return result
  }

}


