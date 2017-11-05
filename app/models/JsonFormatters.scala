package models

import org.joda.time.DateTime
import play.api.libs.json._

object JsonFormatters {

  implicit val errorResponseWrites = new Writes[ErrorResponse] {
    def writes(e: ErrorResponse): JsValue = Json.obj("code" -> e.errorCode, "message" -> e.message)
  }
}
