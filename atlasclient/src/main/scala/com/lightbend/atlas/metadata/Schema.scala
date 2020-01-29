package com.lightbend.atlas.metadata

import com.google.common.collect.ImmutableSet
import com.lightbend.atlas.metadata.MetadataSupport._
import org.apache.atlas.`type`.AtlasTypeUtil
import org.apache.atlas.model.instance.AtlasEntity
import org.apache.atlas.model.typedef._

import scala.collection.JavaConverters._

case class Schema(
    name : String,                                        // Schema name, has to be unique
    encoding : String,                                    // Schema encoding, for example, proto, Avro, JSON, etc
    description : String,                                 // Description
    owner : String,                                       // Maintainer
    namespace : String,                                   // Schema namespace
    record: String,                                       // Type name
    version: Int,                                         // Version
    schema : String,                                      // Content of schema as a string
    relatedSchemas : Seq[AtlasEntity],                    // Related schemas (for example schemas used in this one)
    classifications : Seq[String]                         // Classifications used for this schema{
  ) {
  def createAtlasSchemaEntity(): AtlasEntity = {
    val entity = new AtlasEntity(SCHEMA_TYPE)
    entity.setAttribute("name", name)
    entity.setAttribute(REFERENCEABLE_ATTRIBUTE_NAME, name)
    entity.setAttribute("encoding", encoding)
    entity.setAttribute("description", description)
    entity.setAttribute("owner", owner)
    entity.setAttribute("namespace", namespace)
    entity.setAttribute("dataType", record)
    entity.setAttribute("schemaContent", schema)
    entity.setAttribute("version", version)
    entity.setAttribute("createTime", System.currentTimeMillis())
    relatedSchemas.size match {
      case len if len > 0 => entity.setAttribute("relatedSchemas", relatedSchemas.asJava)
      case _ =>
    }
    classifications.size match {
      case len if len > 0 => entity.setClassifications(toAtlasClassifications(classifications).asJava)
      case _ =>
    }
    entity
  }
}

object Schema{

  def createType() : AtlasTypesDef = {

    // name and description are inherited from DataSet

    val schemaType = AtlasTypeUtil.createClassTypeDef(SCHEMA_TYPE, "Data Schema Type", "1.0", ImmutableSet.of("DataSet"),
      AtlasTypeUtil.createRequiredAttrDef("owner", "string"),
      AtlasTypeUtil.createRequiredAttrDef("createTime", "long"),
      AtlasTypeUtil.createRequiredAttrDef("encoding", "string"),
      AtlasTypeUtil.createRequiredAttrDef("namespace", "string"),
      AtlasTypeUtil.createRequiredAttrDef("dataType", "string"),
      AtlasTypeUtil.createRequiredAttrDef("version", "int"),
      AtlasTypeUtil.createRequiredAttrDef("schemaContent", "string"),
      AtlasTypeUtil.createOptionalListAttrDef("relatedSchemas", AtlasBaseTypeDef.getArrayTypeName(SCHEMA_TYPE)))

    AtlasTypeUtil.getTypesDef(schemaType)
  }
}
