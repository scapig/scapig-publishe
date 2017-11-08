import org.raml.v2.api.model.v10.api.Api
import org.raml.v2.api.model.v10.resources.Resource

import scala.collection.JavaConversions._

package object raml {
  type RAML = Api

  implicit class EnrichRaml(raml: RAML) {
    def flattenedResources: Seq[Resource] = flatten(raml.resources().toList)
  }

  private def flatten(resources: List[Resource], acc: List[Resource]=Nil): List[Resource] = resources match {
    case head :: tail => flatten(head.resources.toList ++ tail, acc :+ head)
    case _ => acc
  }
}
