package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, ControllerComponents}
import services.PublishService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class PublishController @Inject()(cc: ControllerComponents, publishService: PublishService) extends AbstractController(cc) with CommonControllers {

  def publishApiVersion() = Action.async(parse.text) { implicit request =>
    publishService.publish(request.body) map (_ => NoContent)
  }

}
