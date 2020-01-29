package com.lightbend.atlas.metadata

import com.google.common.collect.ImmutableSet
import com.lightbend.atlas.metadata.MetadataSupport._
import org.apache.atlas.`type`.AtlasTypeUtil
import org.apache.atlas.model.instance.AtlasEntity
import org.apache.atlas.model.typedef.AtlasRelationshipDef._
import org.apache.atlas.model.typedef.AtlasStructDef.AtlasAttributeDef.Cardinality
import org.apache.atlas.model.typedef.AtlasTypesDef

import scala.collection.JavaConverters._

case class MLModel(
    name : String,                                        // Model name, has to be unique
    description : String,                                 // Description
    owner : String,                                       // Maintainer
    creationURL : String,                                 // URL of Model creation metadata
    modelType : Option[String],                           // Optional model type, for example, linear regression
    version: Int,                                         // Version
    var input : AtlasEntity,                              // Input Schema
    var output : AtlasEntity,                             // Output Schema
    deploymentURL : Option[String],                       // Optional deployment URL
    classifications : Seq[String]                         // Classifications used for this schema{
  ) {
  def createAtlasSchemaEntity(): AtlasEntity = {
    val entity = new AtlasEntity(MODEL_TYPE)
    entity.setAttribute("name", name)
    entity.setAttribute(REFERENCEABLE_ATTRIBUTE_NAME, name)
    entity.setAttribute("description", description)
    entity.setAttribute("owner", owner)
    entity.setAttribute("creationURL", creationURL)
    modelType match {
      case Some(mtype) => entity.setAttribute("modeltype", mtype)
      case _ =>
    }
    entity.setAttribute("version", version)
    entity.setAttribute("inputschema", input)
    entity.setAttribute("outputschema", output)
    deploymentURL match {
      case Some(url) => entity.setAttribute("deploymenturl", url)
      case _ =>
    }
    entity.setAttribute("createTime", System.currentTimeMillis())
    classifications.size match {
      case len if len > 0 => entity.setClassifications(toAtlasClassifications(classifications).asJava)
      case _ =>
    }
    entity
  }
}

object MLModel{
  def createType() : AtlasTypesDef = {

    // name, description, createTime and owner are inherited from process
    val procType = AtlasTypeUtil.createClassTypeDef(MODEL_TYPE, "Model Type", "1.0", ImmutableSet.of("Process"),
      AtlasTypeUtil.createRequiredAttrDef("creationURL", "string"),
      AtlasTypeUtil.createOptionalAttrDef("modeltype", "string"),
      AtlasTypeUtil.createRequiredAttrDef("version", "int"),
      AtlasTypeUtil.createRequiredAttrDef("inputschema", "DataSet"),
      AtlasTypeUtil.createRequiredAttrDef("outputschema", "DataSet"),
      AtlasTypeUtil.createOptionalAttrDef("deploymenturl", "string"))

    AtlasTypeUtil.getTypesDef(procType)
  }

  val modelInput = AtlasTypeUtil.createRelationshipTypeDef(
    "model_input_schema",
    "Association between model and input schema",
    "1.0",
    RelationshipCategory.AGGREGATION,
    PropagateTags.ONE_TO_TWO,
    AtlasTypeUtil.createRelationshipEndDef(
      MODEL_TYPE,
      "inputschema",
      Cardinality.SINGLE,
      true),
    AtlasTypeUtil.createRelationshipEndDef(
      "DataSet",
      "inputschemaForModel",
      Cardinality.SET,
      false)
  )

  val modelOutput = AtlasTypeUtil.createRelationshipTypeDef(
    "model_output_schema",
    "Association between model and output schema",
    "1.0",
    RelationshipCategory.AGGREGATION,
    PropagateTags.ONE_TO_TWO,
    AtlasTypeUtil.createRelationshipEndDef(
      MODEL_TYPE,
      "outputschema",
      Cardinality.SINGLE,
      true),
    AtlasTypeUtil.createRelationshipEndDef(
      "DataSet",
      "outputschemaForModel",
      Cardinality.SET,
      false)
  )
}
