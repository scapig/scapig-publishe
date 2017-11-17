package services

import javax.inject.{Inject, Singleton}

import models.{APIVersionCreateRequest, Scope}
import raml.{CombinedRamlLoader, RamlParser, StringRamlLoader}
import repository.RamlRepository

import scala.util.Try

@Singleton
class RamlService @Inject()(stringRamlLoader: StringRamlLoader, ramlParser: RamlParser, ramlRepository: RamlRepository) {

  def parseRaml(ramlContent: String): Try[(APIVersionCreateRequest, Seq[Scope])] = {
    for {
      raml <- stringRamlLoader.load(ramlContent)
      apiVersionRequest <- ramlParser.parseAPIVersion(raml)
      apiScopes <- ramlParser.parseScopes(raml)
    } yield (apiVersionRequest, apiScopes)
  }

  def fetchRaml(context: String, version: String) = {
    ramlRepository.fetchRAML(context, version)
  }
}
