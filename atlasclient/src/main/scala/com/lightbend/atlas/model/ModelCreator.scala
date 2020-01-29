package com.lightbend.atlas.model

import com.lightbend.atlas.metadata.MetadataSupport._
import com.lightbend.atlas.metadata._
import com.lightbend.atlas.utils.AtlasSupport
import org.apache.atlas.`type`.AtlasTypeUtil

object ModelCreator {

  // Classification names
  private val MODEL_SERVING_CLASSIFICATION = "Model Serving"
  private val RECOMMENDER_CLASSIFICATION = "Recommender Model Serving"

  // Input and output

  val modelInput = Schema(
    "Model Input",                                          // name
    "json",                                                 // encoding
    "Input message for the recommender model.",             // description
    "Boris",                                                // owner
    "com.lightbend.recommender.model",                      // namespace
    "RecommenderRequestInputs",                             // type
    1,                                                      // version
    """{
      | "definitions": {},
      | "$schema": "http://json-schema.org/draft-07/schema#",
      | "$id": "http://example.com/root.json",
      | "type": "object",
      | "title": "The Root Schema",
      | "required": [
      |   "signature_name",
      |   "inputs"
      | ],
      | "properties": {
      |   "signature_name": {
      |     "$id": "#/properties/signature_name",
      |     "type": "string",
      |     "title": "The Signature_name Schema",
      |     "default": "",
      |     "examples": [
      |       ""
      |     ],
      |     "pattern": "^(.*)$"
      |   },
      |   "inputs": {
      |     "$id": "#/properties/inputs",
      |     "type": "object",
      |     "title": "The Inputs Schema",
      |     "required": [
      |       "products",
      |       "users"
      |     ],
      |     "properties": {
      |       "products": {
      |       "$id": "#/properties/inputs/properties/products",
      |       "type": "array",
      |       "title": "The Products Schema",
      |       "items": {
      |         "$id": "#/properties/inputs/properties/products/items",
      |         "type": "array",
      |         "title": "The Items Schema",
      |         "items": {
      |           "$id": "#/properties/inputs/properties/products/items/items",
      |           "type": "integer",
      |           "title": "The Items Schema",
      |           "default": 0,
      |           "examples": [
      |             1
      |           ]
      |         }
      |       }
      |     },
      |     "users": {
      |       "$id": "#/properties/inputs/properties/users",
      |       "type": "array",
      |       "title": "The Users Schema",
      |       "items": {
      |         "$id": "#/properties/inputs/properties/users/items",
      |         "type": "array",
      |         "title": "The Items Schema",
      |         "items": {
      |           "$id": "#/properties/inputs/properties/users/items/items",
      |           "type": "integer",
      |           "title": "The Items Schema",
      |           "default": 0,
      |           "examples": [
      |             10
      |           ]
      |         }
      |       }
      |     }
      |   }
      | }
      |}
      |}
      |""".stripMargin,
    Seq.empty,
    Seq(MODEL_SERVING_CLASSIFICATION, RECOMMENDER_CLASSIFICATION)
  )

  val modelOutput = Schema(
    "Model Output",                                         // name
    "json",                                                 // encoding
    "Output message for the recommender model.",            // description
    "Boris",                                                // owner
    "com.lightbend.recommender.model",                      // namespace
    "RecommenderRequestOutputs",                            // type
    1,                                                      // version
    """{
      | "definitions": {},
      | "$schema": "http://json-schema.org/draft-07/schema#",
      | "$id": "http://example.com/root.json",
      | "type": "object",
      | "title": "The Root Schema",
      | "required": [
      |   "outputs"
      | ],
      | "properties": {
      |   "outputs": {
      |     "$id": "#/properties/outputs",
      |     "type": "array",
      |     "title": "The Outputs Schema",
      |     "items": {
      |       "$id": "#/properties/outputs/items",
      |       "type": "array",
      |       "title": "The Items Schema",
      |       "items": {
      |         "$id": "#/properties/outputs/items/items",
      |         "type": "number",
      |         "title": "The Items Schema",
      |         "default": 0.0,
      |         "examples": [
      |           0.1
      |         ]
      |       }
      |     }
      |   }
      | }
      | }
      |""".stripMargin,
    Seq.empty,
    Seq()
  )

  val model = MLModel(
    "Product recommender",                                // Model name, has to be unique
    "Product recommendation model",                       // Description
    "Boris",                                              // owner
    "http://metadataref",                                 // URL of Model creation metadata
    Some("Collaborative filtering"),                      // Optional model type, for example, linear regression
    1,                                                    // Version
    null,                                                 // Input Schema
    null,                                                 // Output Schema
    Some("http://recommendermodelserver.kubeflow.svc.cluster.local:8501"),// Optional deployment URL
    Seq.empty
  )

  def main(args: Array[String]): Unit = {

    val atlas = AtlasSupport()
/*
    atlas.deleteEntity(SCHEMA_TYPE, "Model Input")
    atlas.deleteEntity(SCHEMA_TYPE, "Model Output")
    atlas.deleteEntity(MODEL_TYPE, "Product recommender")
    atlas.resetConnection()
*/
    // Create types
    atlas.createType(Schema.createType())
    // Wait for a min to make sure that creation completes in Atlas
    atlas.resetConnection()
    atlas.createType(MLModel.createType())
    // Wait for a min to make sure that creation completes in Atlas
    atlas.resetConnection()

    // Create classifications
    atlas.createType(Classification(
      MODEL_SERVING_CLASSIFICATION,                           // Name
      "All artifacts for Model Serving",                      // Description
      "!.0"                                                   // Version
    ).createAtlasClassificationEntity())
    atlas.createType(Classification(
      RECOMMENDER_CLASSIFICATION,                             // Name
      "All artifacts for Recommender model",                  // Description
      "!.0"                                                   // Version
    ).createAtlasClassificationEntity())
    // Wait for a min to make sure that creation completes in Atlas
    atlas.resetConnection()

    // Create entities
    model.input = atlas.createInstance(modelInput.createAtlasSchemaEntity())
    // Wait for a min to make sure that creation completes in Atlas
    atlas.resetConnection()
    model.output = atlas.createInstance(modelOutput.createAtlasSchemaEntity())
    // Wait for a min to make sure that creation completes in Atlas
    atlas.resetConnection()
    atlas.createInstance(model.createAtlasSchemaEntity())
    // Wait for a min to make sure that creation completes in Atlas
    atlas.resetConnection()
  }
}
