package controllers

import play.api.http.{HeaderNames, HttpChunk, HttpEntity}
import play.api.i18n.Messages
import play.api.libs.ws.WSResponse
import play.api.mvc.{Request, Result, Results}

import scala.concurrent.Future

object ForwarderUtils extends ErrorResponses {
  private val hopByHopHeaders = Set(HeaderNames.CONNECTION, "KEEP-ALIVE", HeaderNames.PROXY_AUTHENTICATE,
    HeaderNames.TRANSFER_ENCODING, HeaderNames.UPGRADE, HeaderNames.CONTENT_LENGTH, HeaderNames.CONTENT_TYPE,
    HeaderNames.SERVER).map(_.toLowerCase)

  def responseMapper(response: WSResponse) = {
    val contentType = response.headers.get(HeaderNames.CONTENT_TYPE).flatMap(_.headOption)

    val entity = response.headers.get(HeaderNames.CONTENT_LENGTH).flatMap(_.headOption).map(_.toLong) match {
      case Some(contentLength) ⇒
        HttpEntity.Streamed(response.bodyAsSource, Some(contentLength), contentType)
      case None ⇒
        HttpEntity.Chunked(response.bodyAsSource.map(HttpChunk.Chunk), contentType)
    }

    Results
      .Status(response.status)
      .sendEntity(entity)
      .withHeaders((for {
        (header, values) <- response.headers.toSeq if !(hopByHopHeaders contains header.toLowerCase)
        value <- values
      } yield header -> value): _*)
  }

  def notFoundResult(service: String)(implicit messages: Messages): Future[Result] = {
    Future successful internalErrorResponse(message = Some(Messages("error.service.unreachable", service)))
  }

  def params(request: Request[_]): String = (for {
    (name, values) <- request.queryString.toSeq
    value <- values
  } yield s"$name=$value").mkString("&") match {
    case "" => ""
    case other => "?" + other
  }
}
