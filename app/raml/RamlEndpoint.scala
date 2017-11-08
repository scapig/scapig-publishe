package raml

import models.AuthType.{APPLICATION, AuthType, NONE, USER}
import models._
import org.raml.v2.api.model.v10.methods.Method
import org.raml.v2.api.model.v10.resources.Resource

import scala.util.matching.Regex
import scala.collection.JavaConversions._

object RamlEndpoint {
  def apply(raml: RAML): Seq[Endpoint] = {
    if(raml.resources().size() != 1) {
      throw RamlUnsupportedVersionException("Only one root segment should be defined in the RAML")
    }
    val context = raml.resources().head.resourcePath()
    for {
      endpoint <- raml.flattenedResources
      method <- endpoint.methods
    } yield {
      Endpoint(
        getUriPattern(context, endpoint),
        method.displayName.value,
        HttpMethod.withName(method.method.toUpperCase),
        getAuthType(method),
        getScope(method),
        getQueryParams(method)
      )
    }
  }

  private def getAuthType(method: Method): AuthType = {
    method.securedBy.toList.map(_.securityScheme.name) match {
      case Seq() => NONE
      case "oauth_2_0" :: _ => USER
      case "x-application" :: _ => APPLICATION
    }
  }

  private def getScope(method: Method): Option[String] = {
    method.annotations.toList.filter(_.name.matches("\\((.*\\.)?scope\\)")).map(a =>
      a.structuredValue.value.toString
    ).headOption
  }

  private def getUriPattern(context: String, endpoint: Resource): String = {
    endpoint.resourcePath.replaceFirst(s"^/${Regex.quote(context)}", "")
  }

  private def getQueryParams(method: Method): Seq[Parameter] = {
    method.queryParameters().toList.map(param => Parameter(param.name(), param.required().booleanValue()))
  }
}
