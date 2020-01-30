package com.lightbend.atlas.utils

import com.sun.jersey.core.util.MultivaluedMapImpl
import org.apache.atlas.AtlasClientV2
import org.apache.atlas.model.SearchFilter

import scala.collection.JavaConverters._

// Implementation is based on this example:
//      https://github.com/apache/incubator-atlas/blob/master/webapp/src/main/java/org/apache/atlas/examples/QuickStartV2.java
object ConnectivityTester {

  val AVRO_COLLECTION = "avro_collection"
  val AVRO_ENUM = "avro_enum"
  val AVRO_FIELD = "avro_field"
  val AVRO_FIXED = "avro_fixed"
  val AVRO_PRIMITIVE = "avro_primitive"
  val AVRO_RECORD ="avro_record"
  val AVRO_SCHEMA = "avro_schema"
  val AVRO_TYPE = "avro_type"
  val KAFKA_TOPIC = "kafka_topic"

//  private val TYPES = Array(AVRO_COLLECTION, AVRO_ENUM, AVRO_FIELD, AVRO_FIXED, AVRO_PRIMITIVE, AVRO_RECORD, AVRO_SCHEMA, AVRO_TYPE, KAFKA_TOPIC)
  private val TYPES = Array("Referenceable", "Asset", "Infrastructure", "DataSet", "Process")

  def main(args: Array[String]): Unit = {

    val atlasSupport = AtlasSupport()

    for (typeName <- TYPES) {
      val searchDefs = atlasSupport.getTypeByName(typeName)
      searchDefs.getEnumDefs.asScala.foreach(enum =>
        println(s"Enum defs for type $typeName  is $enum")
      )
      searchDefs.getStructDefs.asScala.foreach(struct =>
        println(s"Struct defs for type $typeName  is $struct")
      )
      searchDefs.getClassificationDefs.asScala.foreach(clas =>
        println(s"Classification defs for type $typeName  is $clas")
      )
      searchDefs.getEntityDefs.asScala.foreach(ent =>
        println(s"Entity defs for type $typeName  is $ent")
      )
      searchDefs.getRelationshipDefs.asScala.foreach(rel =>
        println(s"Relationship defs for type $typeName  is $rel")
      )
    }
  }
}
