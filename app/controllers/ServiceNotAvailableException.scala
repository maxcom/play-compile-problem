package controllers

import play.api.i18n.Messages

case class ServiceNotAvailableException(service: String, url: String, ex: Throwable)
  extends RuntimeException(s"Service $service not available at $url", ex) {
  def message(implicit messages: Messages) = Messages("error.service.unreachable", service)
}


