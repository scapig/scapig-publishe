package repository

import javax.inject.Singleton

import org.scalatest.BeforeAndAfterEach
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import utils.UnitSpec
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class RamlRepositorySpec extends UnitSpec with BeforeAndAfterEach {

  lazy val fakeApplication: Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> "mongodb://localhost:27017/scapig-publisher-test")
    .build()

  lazy val underTest = fakeApplication.injector.instanceOf[RamlRepository]

  override def afterEach {
    await(await(underTest.repository).drop(failIfNotFound = false))
  }

  val apiContext = "hello"
  val apiVersion = "1.0"
  val raml = "RAML\nversion: 1.0\ntitle:Hello"

  "save" should {
    "insert a new raml" in {

      await(underTest.save(apiContext, apiVersion, raml))

      await(underTest.fetchRAML(apiContext, apiVersion)) shouldBe Some(raml)
    }

    "update an existing raml" in {
      val updatedRaml = "RAML\nversion: 1.0\ntitle:Bye"
      await(underTest.save(apiContext, apiVersion, raml))

      await(underTest.save(apiContext, apiVersion, updatedRaml))

      await(underTest.fetchRAML(apiContext, apiVersion)) shouldBe Some(updatedRaml)
    }
  }
}