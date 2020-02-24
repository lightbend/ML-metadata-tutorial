package com.lightbend.atlas.metadata

import org.apache.atlas.model.instance.AtlasClassification

object MetadataSupport {

  val SCHEMA_TYPE = "data_schema"
  val MODEL_TYPE = "Model"
  val KAFKA_TYPE = "kafka_topic"

  val QUALIFIED_NAME = "qualifiedName"
  val REFERENCEABLE_ATTRIBUTE_NAME: String = QUALIFIED_NAME

  val MODEL_SERVING_CLASSIFICATION = "Model Serving"


  def toAtlasClassifications(classifications: Seq[String]) : Seq[AtlasClassification] = {
    classifications.map(new AtlasClassification(_))
  }
}
