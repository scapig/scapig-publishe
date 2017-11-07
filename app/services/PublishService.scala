package services

import javax.inject.{Inject, Singleton}

import models.APICreateRequest
import raml.UrlRamlLoader

import scala.util.Try

@Singleton
class PublishService @Inject()(urlRamlLoader: UrlRamlLoader) {

  def publish(apiCreateRequest: APICreateRequest): Boolean = {
    val raml = urlRamlLoader.load(apiCreateRequest.ramlFileUrl).get
    false
  }
}
