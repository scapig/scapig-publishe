package connectors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import models._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.mvc.Http.Status
import utils.UnitSpec
import models.JsonFormatters._

class ApiScopeConnectorSpec extends UnitSpec with BeforeAndAfterAll with BeforeAndAfterEach {
  val port = 7001

  val playApplication = new GuiceApplicationBuilder()
    .configure("services.tapi-scope.port" -> "7001")
    .build()
  val wireMockServer = new WireMockServer(wireMockConfig().port(port))

  val scope = Scope("scope1", "scopeName", "scopeDescription")

  override def beforeAll {
    configureFor(port)
    wireMockServer.start()
  }

  override def afterAll: Unit = {
    wireMockServer.stop()
  }

  override def beforeEach(): Unit = {
    WireMock.reset()
  }

  trait Setup {
    val apiScopeConnector = playApplication.injector.instanceOf[ApiScopeConnector]
  }

  "createScope" should {
    "create the scope in tapi-scope" in new Setup {
      stubFor(post(urlPathEqualTo("/scope"))
        .withRequestBody(equalToJson(Json.toJson(scope).toString()))
        .willReturn(aResponse()
          .withStatus(Status.NO_CONTENT)))

      val result = await(apiScopeConnector.createScope(scope))

      result shouldBe HasSucceeded
    }
  }
}