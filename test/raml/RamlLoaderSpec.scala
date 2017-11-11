package raml

import models.{RamlNotFoundException, RamlParseException, RamlUnsupportedVersionException}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Failure

class RamlLoaderSpec extends WordSpec with Matchers {
  "UrlRamlLoader.load" should {
    "fail with RamlNotFoundException when the file does not exist" in {
      new UrlRamlLoader().load("http://zzz-imnotreal-zzz/") match {
        case Failure(e: RamlNotFoundException) => e.getMessage shouldBe "Raml does not exist at: http://zzz-imnotreal-zzz/"
        case _ => throw new IllegalStateException("should not reach here")
      }
    }

    "fail with RamlParseException when the file is not a RAML" in {
      new UrlRamlLoader().load("https://google.com") match {
        case Failure(e: RamlParseException) => e.getMessage should include("Invalid header declaration <!doctype html><html")
        case _ => throw new IllegalStateException("should not reach here")
      }
    }

    "fail with RamlParseException  when the file can not be parsed" in {
      new ClasspathRamlLoader().load("bad_yaml.raml") match {
        case Failure(e: RamlParseException) =>
          e.getMessage should include ("Underlying error while parsing YAML syntax: 'while parsing a block mapping")
        case _ => throw new IllegalStateException("should not reach here")
      }
    }

    "fail with RamlUnsupportedVersionException when the file is not RAML 1.0" in {
      new ClasspathRamlLoader().load("jukebox_0.8.raml") match {
        case Failure(e: RamlUnsupportedVersionException) => e.getMessage shouldBe "Only RAML1.0 is supported"
        case _ => throw new IllegalStateException("should not reach here")
      }
    }
  }
}
