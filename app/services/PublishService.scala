package services

import javax.inject.{Inject, Singleton}

import connectors.{ApiDefinitionConnector, ApiScopeConnector}
import models.{APIVersionCreateRequest, HasSucceeded}
import raml.{FileRamlLoader, RamlParser, UrlRamlLoader}

import scala.concurrent.Future
import scala.util.Try

@Singleton
class PublishService @Inject()(urlRamlLoader: UrlRamlLoader,
                               fileRamlLoader: FileRamlLoader,
                               ramlParser: RamlParser,
                               apiScopeConnector: ApiScopeConnector,
                               apiDefinitionConnector: ApiDefinitionConnector) {

  def publish(apiCreateRequest: APIVersionCreateRequest): Future[HasSucceeded] = {
    val raml = apiCreateRequest.ramlFileUrl match {
      case ramlFileUrl if ramlFileUrl.startsWith("http") => urlRamlLoader.load(ramlFileUrl).get
      case ramlFileUrl => fileRamlLoader.load(ramlFileUrl).get
    }
    val apiVersionRequest = ramlParser.parseAPIVersion(raml)
    val apiScopes = ramlParser.parseScopes(raml)
    for {
      scopes <- Future.sequence(apiScopes map apiScopeConnector.createScope)
      api <- apiDefinitionConnector.publishAPIVersion(apiVersionRequest)
    } yield api
  }
}
