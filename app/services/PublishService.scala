package services

import javax.inject.{Inject, Singleton}

import connectors.{ApiDefinitionConnector, ApiScopeConnector}
import models.HasSucceeded
import repository.RamlRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.failed
import scala.util.{Failure, Success}

@Singleton
class PublishService @Inject()(ramlService: RamlService,
                               apiScopeConnector: ApiScopeConnector,
                               apiDefinitionConnector: ApiDefinitionConnector,
                               ramlRepository: RamlRepository) {

  def publish(ramlContent: String): Future[HasSucceeded] = {

    ramlService.parseRaml(ramlContent) match {
      case Success((apiVersionRequest, apiScopes)) =>
        for {
          _ <- Future.sequence(apiScopes map apiScopeConnector.createScope)
          _ <- apiDefinitionConnector.publishAPIVersion(apiVersionRequest)
          _ <- ramlRepository.save(apiVersionRequest.context, apiVersionRequest.version, ramlContent)
        } yield HasSucceeded
      case Failure(e) => failed(e)
    }
  }
}
