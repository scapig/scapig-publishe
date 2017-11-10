package services

import javax.inject.{Inject, Singleton}

import models.APICreateRequest
import raml.{FileRamlLoader, RamlParser, UrlRamlLoader}

import scala.util.Try

@Singleton
class PublishService @Inject()(urlRamlLoader: UrlRamlLoader, fileRamlLoader: FileRamlLoader, ramlParser: RamlParser) {

  def publish(apiCreateRequest: APICreateRequest): Boolean = {
    apiCreateRequest.ramlFileUrls map {
      case ramlFileUrl if ramlFileUrl.startsWith("http") => urlRamlLoader.load(ramlFileUrl).get
      case ramlFileUrl => fileRamlLoader.load(ramlFileUrl).get
    } map { raml =>
      val apiVersionRequest = ramlParser.parseAPIVersion(raml)
      val apiScopes = ramlParser.parseScopes(raml)
    }
    false
  }
}
