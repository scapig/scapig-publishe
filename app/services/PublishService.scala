package services

import javax.inject.{Inject, Singleton}

import models.{APICreateRequest, Endpoint}
import raml.{FileRamlLoader, RamlEndpoints, UrlRamlLoader}

import scala.util.Try

@Singleton
class PublishService @Inject()(urlRamlLoader: UrlRamlLoader, fileRamlLoader: FileRamlLoader) {

  def publish(apiCreateRequest: APICreateRequest): Boolean = {
    val endpoints: Seq[Seq[Endpoint]] = apiCreateRequest.ramlFileUrls map {
      case ramlFileUrl if ramlFileUrl.startsWith("http") => urlRamlLoader.load(ramlFileUrl).get
      case ramlFileUrl => fileRamlLoader.load(ramlFileUrl).get
    } map (RamlEndpoints(_))
    false
  }
}
