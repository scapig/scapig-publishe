package services

import javax.inject.{Inject, Singleton}

import models.{APIVersionCreateRequest, Scope}
import raml.{CombinedRamlLoader, RamlParser}

import scala.util.Try

@Singleton
class RamlService @Inject()(combinedRamlLoader: CombinedRamlLoader, ramlParser: RamlParser) {

  def parseRaml(ramlUri: String): Try[(APIVersionCreateRequest, Seq[Scope])] = {
    for {
      raml <- combinedRamlLoader.load(ramlUri)
      apiVersionRequest <- ramlParser.parseAPIVersion(raml)
      apiScopes <- ramlParser.parseScopes(raml)
    } yield (apiVersionRequest, apiScopes)
  }
}
