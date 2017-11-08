package models

case class APICreateRequest (scopes: Seq[Scope],
//                             apiName: String,
//                             apiDescription: String,
                             ramlFileUrl: String,
                             serviceUrl: String)

