package controllers

import org.mockito.BDDMockito
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.test.{FakeRequest, Helpers}
import services.{PublishService, RamlService}
import utils.UnitSpec

import scala.concurrent.Future
import scala.concurrent.Future.successful
import scala.io.Source

class RamlControllerSpec extends UnitSpec with MockitoSugar {

  val ramlFile = "valid-without-file-dependencies.raml"
  val ramlContent = Source.fromResource(ramlFile).mkString

  trait Setup {
    val request = FakeRequest()
    val ramlService = mock[RamlService]

    val underTest = new RamlController(Helpers.stubControllerComponents(), ramlService)
  }

  "fetchRaml" should {
    "return 200 (Ok) with the raml when it is in the database" in new Setup {
      BDDMockito.given(ramlService.fetchRaml("aContext", "aVersion")).willReturn(successful(Some(ramlContent)))

      val result = await(underTest.fetchRaml("aContext", "aVersion")(request))

      status(result) shouldBe Status.OK
      bodyOf(result) shouldBe ramlContent
    }

    "return 404 (NotFound) when the RAML does not exist" in new Setup {
      BDDMockito.given(ramlService.fetchRaml("aContext", "aVersion")).willReturn(successful(None))

      val result = await(underTest.fetchRaml("aContext", "aVersion")(request))

      status(result) shouldBe Status.NOT_FOUND
    }
  }
}
