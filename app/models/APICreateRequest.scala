package models

case class APICreateRequest (scopes: Seq[Scope],
                             apiName: String,
                             apiDescription: String,
                             ramlFileUrls: Seq[String],
                             serviceUrl: String)

