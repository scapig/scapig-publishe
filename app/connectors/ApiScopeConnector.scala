package connectors

import javax.inject.Inject

import config.AppConfig
import models.{HasSucceeded, Scope}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import models.JsonFormatters._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class ApiScopeConnector @Inject()(appContext: AppConfig, wsClient: WSClient) {

  val serviceUrl = appContext.serviceUrl("scapig-scope")

  def createScope(scope: Scope): Future[HasSucceeded] = {
    wsClient.url(s"$serviceUrl/scope").post(Json.toJson(scope)) map {
      case response if response.status == Status.NO_CONTENT => HasSucceeded
      case r: WSResponse => throw new RuntimeException(s"Invalid response from scapig-scope ${r.status} ${r.body}")
    }
  }
}
