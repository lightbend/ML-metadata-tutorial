package com.lightbend.atlas.metadata

import com.google.common.collect.ImmutableSet
import com.lightbend.atlas.metadata.MetadataSupport._
import org.apache.atlas.`type`.AtlasTypeUtil
import org.apache.atlas.model.instance.AtlasEntity
import org.apache.atlas.model.typedef.AtlasTypesDef

import scala.collection.JavaConverters._

case class Schema(
    name : String,                                        // Schema name, has to be unique
    encoding : String,                                    // Schema encoding, for example, proto, Avro, etc
    description : String,                                 // Description
    owner : String,                                       // Maintainer
    namespace : String,                                   // Schema namespace
    record: String,                                       // Type name
    version: Int,                                         // Version
    schema : String,                                      // Content of schema as a string
    relatedSchemas : Seq[AtlasEntity],                    // Related schemas (for example schemas used in this one)
    classifications : Seq[String]                         // Classifications used for this schema{
  ){
  def createAtlasSchemaEntity() : AtlasEntity = {
    val entity = new AtlasEntity(SCHEMA_TYPE)
    entity.setAttribute("name", name)
    entity.setAttribute(REFERENCEABLE_ATTRIBUTE_NAME, name)
    entity.setAttribute("encoding", encoding)
    entity.setAttribute("description", description)
    entity.setAttribute("owner", owner)
    entity.setAttribute("namespace", namespace)
    entity.setAttribute("type", record)
    entity.setAttribute("schema", schema)
    entity.setAttribute("versionId", version)
    entity.setAttribute("createTime", System.currentTimeMillis)
    entity.setAttribute("relatedSchemas", relatedSchemas.asJava)
    entity.setClassifications(toAtlasClassifications(classifications).asJava)
    entity
  }

  def createType() : AtlasTypesDef = {

    // name, description, createTime and owner are inherited from process
    val name = AtlasTypeUtil.createRequiredAttrDef("name", "string")
    name.setIsUnique(true)
    name.setIsIndexable(true)
    val procType = AtlasTypeUtil.createClassTypeDef(SCHEMA_TYPE, "Data Schema Type", "1.0", ImmutableSet.of("Process"),
      name,
      AtlasTypeUtil.createRequiredAttrDef("encoding", "string"),
      AtlasTypeUtil.createRequiredAttrDef("description", "string"),
      AtlasTypeUtil.createRequiredAttrDef("owner", "string"),
      AtlasTypeUtil.createRequiredAttrDef("namespace", "string"),
      AtlasTypeUtil.createRequiredAttrDef("type", "string"),
      AtlasTypeUtil.createRequiredAttrDef("versionId", "int"),
      AtlasTypeUtil.createRequiredAttrDef("schema", "string"),
      AtlasTypeUtil.createRequiredAttrDef("createTime", "Date"),
      AtlasTypeUtil.createOptionalAttrDef("relatedSchemas", "array<DataSet>"))

    AtlasTypeUtil.getTypesDef(procType)
  }
}
