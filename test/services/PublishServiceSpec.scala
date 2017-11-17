package services

import connectors.{ApiDefinitionConnector, ApiScopeConnector}
import models._
import org.mockito.BDDMockito.given
import org.mockito.Mockito.{verify, verifyZeroInteractions, when}
import org.scalatest.mockito.MockitoSugar
import utils.UnitSpec

import scala.concurrent.Future.{failed, successful}
import scala.io.Source
import scala.util.{Failure, Success}

class PublishServiceSpec extends UnitSpec with MockitoSugar {

  val apiVersionCreateRequest = APIVersionCreateRequest("calendar", "Calendar API", "My Calendar API", "1.0",
    "http://localhost:8080", APIStatus.PUBLISHED,
    Seq(Endpoint("/today", "Get today's date", HttpMethod.GET, AuthType.USER, Some("read:calendar"))))
  val scope = Scope("read:calendar", "View Calendar")

  val ramlFile = "valid-without-file-dependencies.raml"
  val ramlContent = Source.fromResource(ramlFile).mkString

  trait Setup {
    val ramlService = mock[RamlService]
    val apiScopeConnector = mock[ApiScopeConnector]
    val apiDefinitionConnector= mock[ApiDefinitionConnector]

    val underTest = new PublishService(ramlService, apiScopeConnector, apiDefinitionConnector)

    when(ramlService.parseRaml(ramlContent)).thenReturn(Success(apiVersionCreateRequest, Seq(scope)))
    when(apiScopeConnector.createScope(scope)).thenReturn(successful(HasSucceeded))
    when(apiDefinitionConnector.publishAPIVersion(apiVersionCreateRequest)).thenReturn(successful(HasSucceeded))
  }

  "publish" should {
    "parse the raml and publish the scopes and API Version" in new Setup {
      val result = await(underTest.publish(ramlContent))

      result shouldBe HasSucceeded
      verify(apiScopeConnector).createScope(scope)
      verify(apiDefinitionConnector).publishAPIVersion(apiVersionCreateRequest)
    }

    "fail when parsing the RAML fail" in new Setup {
      given(ramlService.parseRaml(ramlContent))
        .willReturn(Failure(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.publish(ramlContent))}

      verifyZeroInteractions(apiScopeConnector)
      verifyZeroInteractions(apiDefinitionConnector)
    }

    "fail when publishing the scope fail" in new Setup {
      given(apiScopeConnector.createScope(scope))
        .willReturn(failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.publish(ramlContent))}

      verifyZeroInteractions(apiDefinitionConnector)
    }

    "fail when publishing the API fail" in new Setup {
      given(apiDefinitionConnector.publishAPIVersion(apiVersionCreateRequest))
        .willReturn(failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.publish(ramlContent))}
    }

  }
}
