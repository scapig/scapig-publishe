package controllers

import javax.inject.{Inject, Singleton}

import models.{APIPublishRequest, APIVersionCreateRequest}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.PublishService

import scala.concurrent.ExecutionContext.Implicits.global
import models.JsonFormatters._

@Singleton
class PublishController @Inject()(cc: ControllerComponents, publishService: PublishService) extends AbstractController(cc) with CommonControllers {

  def publishApiVersion() = Action.async(parse.json) { implicit request =>
    withJsonBody[APIPublishRequest] { apiPublishRequest: APIPublishRequest =>
      publishService.publish(apiPublishRequest) map (_ => NoContent)
    }
  }

}
