package controllers

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext

abstract class AbstractController(components: => ControllerComponents) extends BaseController
  with ErrorResponses with I18nSupport {

  override lazy val controllerComponents = components

  /**
    * The default ActionBuilder. Used to construct an action, for example:
    *
    * {{{
    *   def foo(query: String) = Action {
    *     Ok
    *   }
    * }}}
    *
    * This is meant to be a replacement for the now-deprecated Action object, and can be used in the same way.
    */
  override def Action: ActionBuilder[Request, AnyContent] = controllerComponents.actionBuilder

  override def parse: PlayBodyParsers = controllerComponents.parsers

  implicit def bodyParsers: PlayBodyParsers = controllerComponents.parsers

  override implicit def defaultExecutionContext: ExecutionContext = controllerComponents.executionContext

  override def messagesApi: MessagesApi = controllerComponents.messagesApi
}
