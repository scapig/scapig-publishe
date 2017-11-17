package services

import javax.inject.{Inject, Singleton}

import models.{APIVersionCreateRequest, Scope}
import raml.{CombinedRamlLoader, RamlParser, StringRamlLoader}

import scala.util.Try

@Singleton
class RamlService @Inject()(stringRamlLoader: StringRamlLoader, ramlParser: RamlParser) {

  def parseRaml(ramlContent: String): Try[(APIVersionCreateRequest, Seq[Scope])] = {
    for {
      raml <- stringRamlLoader.load(ramlContent)
      apiVersionRequest <- ramlParser.parseAPIVersion(raml)
      apiScopes <- ramlParser.parseScopes(raml)
    } yield (apiVersionRequest, apiScopes)
  }
}
