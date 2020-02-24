package com.lightbend.atlas.metadata

import com.lightbend.atlas.metadata.MetadataSupport._
import org.apache.atlas.model.instance.AtlasEntity

import scala.collection.JavaConverters._

case class KafkaTopic(
    name : String,                                      // Topic name, has to be unique
    description : String,                               // Description
    owner : String,                                     // owner
    version: String,                                    // Version of streamlet
    schemas : Seq[AtlasEntity],                         // Schemas used by this topic
    classifications : Seq[String],                      // Classifications
  ) {

  def createAtlasKafkaEntity(): AtlasEntity = {
    val entity = new AtlasEntity(KAFKA_TYPE)
    entity.setAttribute(REFERENCEABLE_ATTRIBUTE_NAME, name)
    entity.setAttribute("name", name)
    entity.setAttribute("topic", name)
    entity.setAttribute("description", description)
    entity.setAttribute("owner", owner)
    entity.setAttribute("version", version)
    entity.setAttribute("avroSchema", schemas.asJava)
    entity.setAttribute("createTime", System.currentTimeMillis)
    entity.setAttribute("uri", "")                              // Required parameter
    entity.setClassifications(toAtlasClassifications(classifications).asJava)
    entity
  }
}