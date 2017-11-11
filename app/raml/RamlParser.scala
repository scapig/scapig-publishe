package raml

import models.AuthType.{APPLICATION, AuthType, NONE, USER}
import models._
import org.raml.v2.api.model.v10.datamodel.TypeInstance
import org.raml.v2.api.model.v10.methods.Method
import org.raml.v2.api.model.v10.resources.Resource

import scala.util.matching.Regex
import scala.collection.JavaConversions._
import scala.util.Try

class RamlParser {
  def parseAPIVersion(raml: RAML): Try[APIVersionCreateRequest] = {
    Try {
      val context = getValue("(annotations.context)", raml)
      val status = APIStatus.withName(getValue("(annotations.status)", raml))
      val serviceUrl = getValue("(annotations.serviceUrl)", raml)

      if (raml.resources().size() > 1) {
        throw RamlParseException("Only one root segment should be defined in the RAML")
      }
      if (!raml.resources().headOption.map(_.resourcePath()).contains(s"/$context")) {
        throw RamlParseException("The root segment should be the context")
      }

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

      APIVersionCreateRequest(
        context,
        raml.title().value(),
        raml.description().value(),
        raml.version().value(),
        serviceUrl,
        status,
        endpoints)
    }
  }

  def parseScopes(raml: RAML): Try[Seq[Scope]] = {
    Try {
      raml.annotations.toList.filter(_.name == "(annotations.scopes)") flatMap (_.structuredValue().properties() flatMap { annotation =>
        annotation.values() map { scope =>
          Scope(getPropertyValue("key")(scope), getPropertyValue("name")(scope))
        }
      })
    }
  }

  private def getPropertyValue(propertyName: String)(instance: TypeInstance): String = {
    instance.properties().find(_.name() == propertyName)
      .getOrElse(throw RamlParseException(s"Property not found $propertyName in $instance"))
      .value().value().toString
  }

  private def getValue(name: String, raml: RAML): String = {
    raml.annotations.toList
      .find(_.name == name)
      .map(_.structuredValue().value().toString)
      .getOrElse(throw RamlParseException(s"Property not found $name"))
  }

  private def getAuthType(method: Method): AuthType = {
    method.securedBy.toList.map(_.securityScheme.name) match {
      case Seq() => NONE
      case "oauth_2_0" :: _ => USER
      case "x-application" :: _ => APPLICATION
    }
  }

  private def getScope(method: Method): Option[String] = {
    method.securedBy()
      .find(_.name() == "oauth_2_0").flatMap(_.structuredValue().properties()
      .find(_.name() == "scopes").flatMap(_.values().headOption
      .map(_.value().toString)))
  }

  private def getUriPattern(context: String, endpoint: Resource): String = {
    endpoint.resourcePath.replaceFirst(s"^/${Regex.quote(context)}", "")
  }

  private def getQueryParams(method: Method): Seq[Parameter] = {
    method.queryParameters().toList.map(param => Parameter(param.name(), param.required().booleanValue()))
  }
}
