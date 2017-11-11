package controllers

import models.{APIPublishRequest, HasSucceeded}
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json.toJson
import play.api.test.{FakeRequest, Helpers}
import services.PublishService
import utils.UnitSpec
import models.JsonFormatters._

import scala.concurrent.Future.successful

class PublisherControllerSpec extends UnitSpec with MockitoSugar {

  val apiPublishRequest = APIPublishRequest("http://path/file.raml")

  trait Setup {
    val request = FakeRequest()
    val publishService = mock[PublishService]

    val underTest = new PublishController(Helpers.stubControllerComponents(), publishService)
  }

  "publishApiVersion" should {
    "publish the API Version and returns a 204 (NoContent)" in new Setup {
      given(publishService.publish(apiPublishRequest)).willReturn(successful(HasSucceeded))

      val result = await(underTest.publishApiVersion()(request.withBody(toJson(apiPublishRequest))))

      status(result) shouldBe Status.NO_CONTENT
      verify(publishService).publish(apiPublishRequest)
    }

  }
}