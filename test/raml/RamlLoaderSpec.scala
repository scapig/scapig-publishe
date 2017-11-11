package raml

import models.{RamlNotFoundException, RamlParseException, RamlUnsupportedVersionException}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Failure

class RamlLoaderSpec extends WordSpec with Matchers {
  "failure to find the file" should {
    "result in a not found exception" in {
      new UrlRamlLoader().load("http://zzz-imnotreal-zzz/") match {
        case Failure(e: RamlNotFoundException) => e.getMessage shouldBe "Raml does not exist at: http://zzz-imnotreal-zzz/"
        case _ => throw new IllegalStateException("should not reach here")
      }
    }
  }

  "Not RAML" should {
    "result in a parse exception" in {
      new UrlRamlLoader().load("https://google.com") match {
        case Failure(e: RamlParseException) => e.getMessage should include("Invalid header declaration <!doctype html><html")
        case _ => throw new IllegalStateException("should not reach here")
      }
    }
  }

  "Bad RAML" should {
    "result in a parse exception" in {
      new ClasspathRamlLoader().load("bad_yaml.raml") match {
        case Failure(e: RamlParseException) =>
          e.getMessage should include ("Underlying error while parsing YAML syntax: 'while parsing a block mapping")
        case _ => throw new IllegalStateException("should not reach here")
      }
    }
  }

  "Unsupported RAML spec version" should {
    "result in an unsupported exception" in {
      new ClasspathRamlLoader().load("jukebox_0.8.raml") match {
        case Failure(e: RamlUnsupportedVersionException) => e.getMessage shouldBe "Only RAML1.0 is supported"
        case _ => throw new IllegalStateException("should not reach here")
      }
    }
  }
}
