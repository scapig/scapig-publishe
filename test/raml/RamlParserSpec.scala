package raml

import models.{Endpoint, _}
import org.mockito.BDDMockito.given
import org.mockito.{BDDMockito, Mockito}
import org.mockito.Mockito.spy
import org.raml.v2.api.model.v10.declarations.AnnotationRef
import org.raml.v2.api.model.v10.resources.Resource
import org.scalatest.mockito.MockitoSugar
import utils.UnitSpec

import scala.collection.JavaConversions._
import scala.util.{Failure, Success}

class RamlParserSpec extends UnitSpec with MockitoSugar {

  trait Setup {
    val underTest = new RamlParser()

    val urlRamlLoader = new ClasspathRamlLoader()
    val raml = urlRamlLoader.load("helloworld.raml").getOrElse(throw new RuntimeException("Invalid raml helloworld.raml"))
  }

  "parseAPIVersion" should {
    "return the API Version from the RAML" in new Setup {
      val apiVersion = underTest.parseAPIVersion(raml)

      apiVersion shouldBe Success(
        APIVersionCreateRequest(
          context = "hello",
          apiName = "Hello World",
          apiDescription = "Hello World API",
          version = "2.0",
          serviceBaseUrl = "http://localhost:8080",
          status = APIStatus.PUBLISHED,
          endpoints = Seq(
            Endpoint("/world", "Say hello world", HttpMethod.GET, AuthType.NONE, None,
              Seq(Parameter("myRequiredParam", required = true), Parameter("myOptionalParam"))),
            Endpoint("/user", "Say hello user", HttpMethod.POST, AuthType.USER, Some("hello")),
            Endpoint("/application", "Say hello application", HttpMethod.PUT, AuthType.APPLICATION))
        )
      )
    }

    "fail when there is no context" in new Setup {
      val ramlWithoutContext = withoutAnnotation(raml, "(annotations.context)")

      val result = underTest.parseAPIVersion(ramlWithoutContext)

      result shouldBe Failure(RamlParseException("Property not found (annotations.context)"))
    }

    "fail when there is no status" in new Setup {
      val ramlWithoutContext = withoutAnnotation(raml, "(annotations.status)")

      val result = underTest.parseAPIVersion(ramlWithoutContext)

      result shouldBe Failure(RamlParseException("Property not found (annotations.status)"))
    }

    "fail when there is no serviceUrl" in new Setup {
      val ramlWithoutContext = withoutAnnotation(raml, "(annotations.serviceUrl)")

      val result = underTest.parseAPIVersion(ramlWithoutContext)

      result shouldBe Failure(RamlParseException("Property not found (annotations.serviceUrl)"))
    }

    "fail when there is no root segment" in new Setup {
      val mockRaml = spy(raml)
      given(mockRaml.resources()).willReturn(Seq.empty)

      val result = underTest.parseAPIVersion(mockRaml)

      result shouldBe Failure(RamlParseException("The root segment should be the context"))
    }

    "fail when there are multiple root segment" in new Setup {
      val mockRaml = spy(raml)
      given(mockRaml.resources()).willReturn(raml.resources() ++ raml.resources())

      val result = underTest.parseAPIVersion(mockRaml)

      result shouldBe Failure(RamlParseException("Only one root segment should be defined in the RAML"))
    }

    "fail when the root segment is different from the context" in new Setup {
      val mockResource = mock[Resource]
      given(mockResource.resourcePath()).willReturn("/anothercontext")
      val mockRaml = withResource(raml, mockResource)

      val result = underTest.parseAPIVersion(mockRaml)

      result shouldBe Failure(RamlParseException("The root segment should be the context"))
    }
  }

  "parseScopes" should {
    "return the scopes from the RAML file" in new Setup {
      val result = underTest.parseScopes(raml)

      result shouldBe Success(Seq(Scope("hello", "Say hello")))
    }

    "fail when some scopes are missing from the annotations" in new Setup {
      val mockRaml = withoutAnnotation(raml, "(annotations.scopes)")

      val result = underTest.parseScopes(mockRaml)

      result shouldBe Failure(RamlParseException("Scopes [hello] are not defined in the RAML"))
    }
  }

  private def withoutAnnotation(raml: RAML, annotationName: String): RAML = {
    val mockRaml = spy(raml)
    given(mockRaml.annotations()).willReturn(raml.annotations.toList.filterNot(_.name() == annotationName))
    mockRaml
  }

  private def withResource(raml: RAML, resource: Resource): RAML = {
    val mockRaml = spy(raml)
    given(mockRaml.resources()).willReturn(Seq(resource))
    mockRaml
  }

}
