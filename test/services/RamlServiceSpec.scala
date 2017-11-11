package services

import models._
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.raml.v2.internal.impl.v10.Raml10Builder
import org.scalatest.mockito.MockitoSugar
import raml.{ClasspathRamlLoader, CombinedRamlLoader, RamlParser}
import utils.UnitSpec

import scala.util.{Failure, Success}

class RamlServiceSpec extends UnitSpec with MockitoSugar {

  trait Setup {
    val combinedRamlLoader = mock[CombinedRamlLoader]
    val ramlParser = mock[RamlParser]

    val underTest = new RamlService(combinedRamlLoader, ramlParser)

    val raml = new ClasspathRamlLoader().load("valid.raml").get

    val expectedApi = APIVersionCreateRequest("calendar", "Calendar API", "My Calendar API", "1.0", "http://localhost:8080",
      APIStatus.PUBLISHED, Seq(Endpoint("/today", "Get today's date", HttpMethod.GET, AuthType.USER, Some("read:calendar"))))

    val scope = Scope("read:calendar", "Get calendar")

    given(combinedRamlLoader.load("/ramlurl")).willReturn(Success(raml))
    given(ramlParser.parseAPIVersion(raml)).willReturn(Success(expectedApi))
    given(ramlParser.parseScopes(raml)).willReturn(Success(Seq(scope)))
  }

  "parseRaml" should {
    "return the API version and scope" in new Setup {

      val result = underTest.parseRaml("/ramlurl")

      result shouldBe Right((expectedApi, Seq(scope)))
    }

    "fail when the RAML file can not be loaded" in new Setup {
      val error = new RuntimeException("test error")
      given(combinedRamlLoader.load("/ramlurl")).willReturn(Failure(error))

      val result = underTest.parseRaml("/ramlurl")

      result shouldBe Left(error)
    }

    "fail when the API Version can not be parsed" in new Setup {
      val error = new RuntimeException("test error")
      given(ramlParser.parseAPIVersion(raml)).willReturn(Failure(error))

      val result = underTest.parseRaml("/ramlurl")

      result shouldBe Left(error)
    }

    "fail when the API Version can not be parsed" in new Setup {
      val error = new RuntimeException("test error")
      given(ramlParser.parseScopes(raml)).willReturn(Failure(error))

      val result = underTest.parseRaml("/ramlurl")

      result shouldBe Left(error)
    }

  }
}
