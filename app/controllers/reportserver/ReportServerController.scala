package controllers.reportserver

import javax.inject.Inject

import controllers.{AbstractController, ForwarderUtils}
import play.api.libs.ws.WSClient
import play.api.mvc.ControllerComponents

class ReportServerController @Inject() (cc: ControllerComponents, wsClient: WSClient)
  extends AbstractController(cc) {

  private val serviceName = "report-server"
  private val url = "http://some-server:4444/"

  def forwardPut(path: String) = Action.async(parse.json) { implicit request =>
    val params = ForwarderUtils.params(request)
    wsClient
      .url(url + params)
      .withMethod("PUT")
      .withBody(request.body)
      .stream()
      .map(ForwarderUtils.responseMapper)
      .fallbackTo(ForwarderUtils.notFoundResult(serviceName))
  }
}
