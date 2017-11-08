package services

import javax.inject.{Inject, Singleton}

import models.APICreateRequest
import raml.{FileRamlLoader, RamlEndpoint, UrlRamlLoader}

import scala.util.Try

@Singleton
class PublishService @Inject()(urlRamlLoader: UrlRamlLoader, fileRamlLoader: FileRamlLoader) {

  def publish(apiCreateRequest: APICreateRequest): Boolean = {
    val raml = apiCreateRequest.ramlFileUrl match {
      case url if url.startsWith("http") => urlRamlLoader.load(apiCreateRequest.ramlFileUrl).get
      case _ => fileRamlLoader.load(apiCreateRequest.ramlFileUrl).get
    }
    val endpoints = RamlEndpoint(raml)
    false
  }
}
