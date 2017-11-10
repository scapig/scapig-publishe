package raml

import models.AuthType.{APPLICATION, AuthType, NONE, USER}
import models._
import org.raml.v2.api.model.v10.declarations.AnnotationRef
import org.raml.v2.api.model.v10.methods.Method
import org.raml.v2.api.model.v10.resources.Resource

import scala.util.matching.Regex
import scala.collection.JavaConversions._

object RamlEndpoints {
  def apply(raml: RAML): Seq[Endpoint] = {
    if(raml.resources().size() != 1) {
      throw RamlUnsupportedVersionException("Only one root segment should be defined in the RAML")
    }
    val context = raml.resources().head.resourcePath()
    val endpoints = for {
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
    val status = getValue("(annotations.status)", raml)
    val aContext = getValue("(annotations.context)", raml)
    val scopes = getScopes(raml)
    val scopesNew = getScopesNew(raml)

    /*
    val scopes = raml.annotations.toList.find(_.name == "(annotations.scope)")
    val scopeKey = scopes.flatMap(getPropertyValue("key"))
    val scopeDescription = scopes.flatMap(getPropertyValue("description"))
    val scopeName = scopes.flatMap(getPropertyValue("name"))
*/
    endpoints
  }

  private def getScopesNew(raml: RAML) = {
    val scope1Key = raml.annotations.toList.filter(_.name == "(annotations.scopes)") map (_.structuredValue().properties().head.values().get(0).properties().get(0).value().value())
    scope1Key
  }

  private def getScopes(raml: RAML) = {
    raml.annotations.toList.filter(_.name == "(annotations.scope)").map { item =>
      Scope(getPropertyValue("key", item), getPropertyValue("name", item), getPropertyValue("description", item))
    }
  }

  private def getValue(name: String, raml: RAML) = {
    raml.annotations.toList
      .find(_.name == name)
      .map(_.structuredValue().value().toString)
  }

  private def getPropertyValue(name: String, annotationRef: AnnotationRef): String = {
    annotationRef.structuredValue().properties()
      .find(_.name() == name)
      .map(_.value().value().toString)
      .getOrElse(throw RamlNotFoundException(s"Property not found $name"))
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
