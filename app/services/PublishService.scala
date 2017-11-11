package services

import javax.inject.{Inject, Singleton}

import connectors.{ApiDefinitionConnector, ApiScopeConnector}
import models.{APIPublishRequest, APIVersionCreateRequest, HasSucceeded}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.failed
import scala.util.{Failure, Success}

@Singleton
class PublishService @Inject()(ramlService: RamlService,
                               apiScopeConnector: ApiScopeConnector,
                               apiDefinitionConnector: ApiDefinitionConnector) {

  def publish(apiPublishRequest: APIPublishRequest): Future[HasSucceeded] = {

    ramlService.parseRaml(apiPublishRequest.ramlFileUrl) match {
      case Success((apiVersionRequest, apiScopes)) =>
        for {
          _ <- Future.sequence(apiScopes map apiScopeConnector.createScope)
          _ <- apiDefinitionConnector.publishAPIVersion(apiVersionRequest)
        } yield HasSucceeded
      case Failure(e) => failed(e)
    }
  }
}
