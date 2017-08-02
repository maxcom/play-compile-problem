package controllers

import java.io.{PrintWriter, StringWriter}

import play.api.i18n.Messages
import play.api.libs.json.Json._
import play.api.libs.json.{JsPath, JsonValidationError}
import play.api.mvc.Results._

trait ErrorResponses {
  def internalErrorResponse(ex:Option[Throwable] = None, message:Option[String] = None, details:Option[String] = None,
                            jsonErrors: Seq[(JsPath, Seq[JsonValidationError])] = Seq.empty)(implicit messages: Messages) = {
    errorResponse(ex, message, details, jsonErrors = jsonErrors)
  }

  def errorResponse(ex:Option[Throwable] = None, message:Option[String] = None, details:Option[String] = None,
                    status: Status = InternalServerError, jsonErrors: Seq[(JsPath, Seq[JsonValidationError])] = Seq.empty)
                   (implicit messages: Messages) = {
    val t = status match {
      case UnprocessableEntity => "common.unprocessable_entity"
      case BadRequest          => "common.bad_request"
      case NotFound            => "common.not_found"
      case Forbidden           => "common.forbidden"
      case _                   => "common.internal_server_error"
    }
    val msg = message.getOrElse(Messages(t))

    val stack = Option(ex).flatten.map { ex =>
      val writer = new StringWriter()
      val printWriter = new PrintWriter(writer)
      printWriter.print(ex.toString+"\n")
      ex.printStackTrace(printWriter)
      printWriter.flush()
      writer.toString
    }

    val jsonInfo = jsonErrors.headOption.map { _ ⇒
      (for {
        (path, errors) <- jsonErrors
        error <- errors
      } yield {
        Messages("path.error", path, error.message) + s" (${error.args.mkString(",")})"
      }).mkString("\n")
    }

    val allDetails = Seq(details, stack, jsonInfo).flatten.mkString("\n")

    status(
      obj(
        "success" -> false,
        "message" -> msg,
        "details" -> allDetails
      )
    )
  }
}