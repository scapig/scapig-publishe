package models

import org.joda.time.DateTime
import play.api.libs.json._

object JsonFormatters {
  implicit val formatAuthType: Format[AuthType.Value] = EnumJson.enumFormat(AuthType)
  implicit val formatHttpMethod: Format[HttpMethod.Value] = EnumJson.enumFormat(HttpMethod)
  implicit val formatAPIStatus: Format[APIStatus.Value] = EnumJson.enumFormat(APIStatus)
  implicit val formatParameter: OFormat[Parameter] = Json.format[Parameter]
  implicit val formatEndpoint: OFormat[Endpoint] = Json.format[Endpoint]
  implicit val formatAPIPublishRequest: OFormat[APIPublishRequest] = Json.format[APIPublishRequest]
  implicit val formatScope: OFormat[Scope] = Json.format[Scope]

  implicit val formatAPIVersionCreateRequest: OFormat[APIVersionCreateRequest] = Json.format[APIVersionCreateRequest]

  implicit val errorResponseWrites = new Writes[ErrorResponse] {
    def writes(e: ErrorResponse): JsValue = Json.obj("code" -> e.errorCode, "message" -> e.message)
  }
}

object EnumJson {

  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = new Reads[E#Value] {
    def reads(json: JsValue): JsResult[E#Value] = json match {
      case JsString(s) => {
        try {
          JsSuccess(enum.withName(s))
        } catch {
          case _: NoSuchElementException =>
            JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not contain '$s'")
        }
      }
      case _ => JsError("String value expected")
    }
  }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = new Writes[E#Value] {
    def writes(v: E#Value): JsValue = JsString(v.toString)
  }

  implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(enumReads(enum), enumWrites)
  }

}