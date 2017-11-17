package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, ControllerComponents}
import services.RamlService
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class RamlController @Inject()(cc: ControllerComponents, ramlService: RamlService) extends AbstractController(cc) with CommonControllers {

  def fetchRaml(context: String, version: String) = Action.async { implicit request =>
    ramlService.fetchRaml(context, version) map {
      case Some(raml) => Ok(raml)
      case None => NotFound
    }
  }

}
