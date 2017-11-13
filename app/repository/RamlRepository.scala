package repository

import javax.inject.{Inject, Singleton}

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.api.gridfs.Implicits.DefaultReadFileReader
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@Singleton
class RamlRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi)  {

  def repository: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection[BSONCollection]("raml"))

  def save(context: String, version: String, raml: String): Future[UpdateWriteResult] = {
    val ramlDocument = BSONDocument(
      "context" -> context,
      "version" -> version,
      "raml" -> raml)

    repository flatMap {  collection =>
      collection.update(BSONDocument("context" -> context, "version" -> version), ramlDocument, upsert = true)
    }
  }

  def fetchRAML(context: String, version: String): Future[Option[String]] = {
    repository flatMap { collection =>
      collection.find(BSONDocument("context" -> context, "version" -> version)).one[BSONDocument] map (_ flatMap (_.getAs[String]("raml")))
    }
  }
}
