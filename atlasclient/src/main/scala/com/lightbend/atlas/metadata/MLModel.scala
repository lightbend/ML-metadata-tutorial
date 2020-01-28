package com.lightbend.atlas.metadata

import com.google.common.collect.ImmutableSet
import com.lightbend.atlas.metadata.MetadataSupport._
import org.apache.atlas.`type`.AtlasTypeUtil
import org.apache.atlas.model.instance.AtlasEntity
import org.apache.atlas.model.typedef.AtlasTypesDef

import scala.collection.JavaConverters._

case class MLModel(
    name : String,                                        // Model name, has to be unique
    description : String,                                 // Description
    owner : String,                                       // Maintainer
    creationURL : String,                                 // URL of Model creation metadata
    modelType : Option[String],                           // Optional model type, for example, linear regression
    version: Int,                                         // Version
    input : AtlasEntity,                                  // Input Schema
    output : AtlasEntity,                                 // Output Schema
    deploymentURL : Option[String],                       // OPtional deployment URL
    classifications : Seq[String]                         // Classifications used for this schema{
  ){
  def createAtlasSchemaEntity() : AtlasEntity = {
    val entity = new AtlasEntity(SCHEMA_TYPE)
    entity.setAttribute("name", name)
    entity.setAttribute(REFERENCEABLE_ATTRIBUTE_NAME, name)
    entity.setAttribute("description", description)
    entity.setAttribute("owner", owner)
    entity.setAttribute("creationURL", creationURL)
    entity.setAttribute("modeltype", modelType)
    entity.setAttribute("versionId", version)
    entity.setAttribute("inputschema", input)
    entity.setAttribute("outputschema", output)
    entity.setAttribute("deploymenturl", deploymentURL)
    entity.setAttribute("deployTime", System.currentTimeMillis)
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
      AtlasTypeUtil.createRequiredAttrDef("description", "string"),
      AtlasTypeUtil.createRequiredAttrDef("owner", "string"),
      AtlasTypeUtil.createRequiredAttrDef("creationURL", "string"),
      AtlasTypeUtil.createOptionalAttrDef("modeltype", "string"),
      AtlasTypeUtil.createRequiredAttrDef("versionId", "int"),
      AtlasTypeUtil.createRequiredAttrDef("inputschema", "DataSet"),
      AtlasTypeUtil.createRequiredAttrDef("outputschema", "DataSet"),
      AtlasTypeUtil.createRequiredAttrDef("createTime", "Date"),
      AtlasTypeUtil.createOptionalAttrDef("deploymenturl", "string"))

    AtlasTypeUtil.getTypesDef(procType)
  }
}
