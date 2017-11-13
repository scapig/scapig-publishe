package connectors

import javax.inject.Inject

import config.AppConfig
import models.{APIVersionCreateRequest, HasSucceeded}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.JsonFormatters._

class ApiDefinitionConnector @Inject()(appContext: AppConfig, wsClient: WSClient) {

  val serviceUrl = appContext.serviceUrl("tapi-definition")

  def publishAPIVersion(api: APIVersionCreateRequest): Future[HasSucceeded] = {
    wsClient.url(s"$serviceUrl/api-definition").post(Json.toJson(api)) map {
      case response if response.status == Status.OK => HasSucceeded
      case r: WSResponse => throw new RuntimeException(s"Invalid response from tapi-definition ${r.status} ${r.body}")
    }
  }
}
