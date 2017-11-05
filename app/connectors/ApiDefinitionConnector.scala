package connectors

import javax.inject.Inject

import config.AppContext
import models.{APIDefinition, HasSucceeded}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import models.JsonFormatters._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApiDefinitionConnector @Inject()(appContext: AppContext, wsClient: WSClient) {

  val serviceUrl = appContext.serviceUrl("tapi-definition")

  def createAPI(api: APIDefinition): Future[HasSucceeded] = {
    wsClient.url(s"$serviceUrl/api-definition").post(Json.toJson(api)) map {
      case response if response.status == Status.NO_CONTENT => HasSucceeded
      case r: WSResponse => throw new RuntimeException(s"Invalid response from tapi-definition ${r.status} ${r.body}")
    }
  }
}
