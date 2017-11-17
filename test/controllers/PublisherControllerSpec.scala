package controllers

import models.HasSucceeded
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar
import play.api.http.{HeaderNames, Status}
import play.api.test.{FakeRequest, Helpers}
import services.PublishService
import utils.UnitSpec

import scala.concurrent.Future.successful
import scala.io.Source

class PublisherControllerSpec extends UnitSpec with MockitoSugar {

  val ramlFile = "valid-without-file-dependencies.raml"
  val ramlContent = Source.fromResource(ramlFile).mkString

  trait Setup {
    val request = FakeRequest("POST", "/")
    val publishService = mock[PublishService]

    val underTest = new PublishController(Helpers.stubControllerComponents(), publishService)
  }

  "publishApiVersion" should {
    "publish the API Version and returns a 204 (NoContent)" in new Setup {
      given(publishService.publish(ramlContent)).willReturn(successful(HasSucceeded))

      val result = await(underTest.publishApiVersion()(request.withBody(ramlContent).withHeaders(HeaderNames.CONTENT_TYPE -> "text/plain")))

      status(result) shouldBe Status.NO_CONTENT
      verify(publishService).publish(ramlContent)
    }
  }
}