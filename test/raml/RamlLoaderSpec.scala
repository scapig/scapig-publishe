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
        case Failure(e: RamlParseException) => e.getMessage shouldBe
          """Underlying error while parsing YAML syntax: 'while parsing a block mapping
            | in 'reader', line 3, column 1:
            |    title: Employers PAYE Service
            |    ^
            |expected <block end>, but found BlockEntry
            | in 'reader', line 6, column 1:
            |    - bad mix
            |    ^
            |' --  [line=6, col=1]""".stripMargin
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

  "UrlRewriter" should {

    trait Setup {
      val underTest = new UrlRewriter {
        val rewrites = Map(
          "https://developer\\.service\\.hmrc\\.gov\\.uk" -> "http://api-documentation-frontend.public.mdtp",
          "http://api-documentation-raml-frontend\\.service" -> "http://api-documentation-frontend.public.mdtp")
      }
    }

    "modify public dev hub URL to internal one for doc frontend" in new Setup {
      val url = "https://developer.service.hmrc.gov.uk/api-documentation/assets/common/docs/errors.md"
      val internalUrl = "http://api-documentation-frontend.public.mdtp/api-documentation/assets/common/docs/errors.md"

      underTest.rewriteUrl(url) shouldBe internalUrl
    }

    "modify raml doc frontend URL to one for doc frontend" in new Setup {
      val url = "http://api-documentation-raml-frontend.service/api-documentation/assets/common/docs/errors.md"
      val internalUrl = "http://api-documentation-frontend.public.mdtp/api-documentation/assets/common/docs/errors.md"

      underTest.rewriteUrl(url) shouldBe internalUrl
    }

    "not modify other URLs" in new Setup {
      val url = "http://www.bbc.co.uk/news"

      underTest.rewriteUrl(url) shouldBe url
    }
  }
}
