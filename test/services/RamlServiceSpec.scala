package services

import models._
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.raml.v2.internal.impl.v10.Raml10Builder
import org.scalatest.mockito.MockitoSugar
import raml.{ClasspathRamlLoader, CombinedRamlLoader, RamlParser, StringRamlLoader}
import utils.UnitSpec

import scala.io.Source
import scala.util.{Failure, Success}

class RamlServiceSpec extends UnitSpec with MockitoSugar {

  trait Setup {
    val ramlLoader = mock[StringRamlLoader]
    val ramlParser = mock[RamlParser]

    val underTest = new RamlService(ramlLoader, ramlParser)

    val ramlFile = "valid-without-file-dependencies.raml"
    val ramlContent = Source.fromResource(ramlFile).mkString
    val raml = new ClasspathRamlLoader().load(ramlFile).get

    val expectedApi = APIVersionCreateRequest("calendar", "Calendar API", "My Calendar API", "1.0", "http://localhost:8080",
      APIStatus.PUBLISHED, Seq(Endpoint("/today", "Get today's date", HttpMethod.GET, AuthType.USER, Some("read:calendar"))))

    val scope = Scope("read:calendar", "Get calendar")

    given(ramlLoader.load(ramlContent)).willReturn(Success(raml))
    given(ramlParser.parseAPIVersion(raml)).willReturn(Success(expectedApi))
    given(ramlParser.parseScopes(raml)).willReturn(Success(Seq(scope)))
  }

  "parseRaml" should {
    "return the API version and scope" in new Setup {

      val result = underTest.parseRaml(ramlContent)

      result shouldBe Success((expectedApi, Seq(scope)))
    }

    "fail when the RAML file can not be loaded" in new Setup {
      val error = new RuntimeException("test error")
      given(ramlLoader.load(ramlContent)).willReturn(Failure(error))

      val result = underTest.parseRaml(ramlContent)

      result shouldBe Failure(error)
    }

    "fail when the API Version can not be parsed" in new Setup {
      val error = new RuntimeException("test error")
      given(ramlParser.parseAPIVersion(raml)).willReturn(Failure(error))

      val result = underTest.parseRaml(ramlContent)

      result shouldBe Failure(error)
    }

    "fail when the Scopes can not be parsed" in new Setup {
      val error = new RuntimeException("test error")
      given(ramlParser.parseScopes(raml)).willReturn(Failure(error))

      val result = underTest.parseRaml(ramlContent)

      result shouldBe Failure(error)
    }
  }
}