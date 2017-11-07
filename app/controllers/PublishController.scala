package controllers

import javax.inject.{Inject, Singleton}

import models.APICreateRequest
import play.api.mvc.{AbstractController, ControllerComponents}
import services.PublishService
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import models.JsonFormatters._

@Singleton
class PublishController @Inject()(cc: ControllerComponents, publishService: PublishService) extends AbstractController(cc) with CommonControllers {

  def publishApi() = Action.async(parse.json) { implicit request =>
    withJsonBody[APICreateRequest] { apiCreateRequest: APICreateRequest =>
      publishService.publish(apiCreateRequest)
      Future(Ok(""))
    }
  }

}
