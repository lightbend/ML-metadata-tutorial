package com.lightbend.atlas.metadata

import com.google.common.collect.ImmutableSet
import org.apache.atlas.`type`.AtlasTypeUtil
import org.apache.atlas.model.typedef.AtlasTypesDef

case class Classification(
    name : String,                                      // Nama
    description: String,                                // Description
    version: String                                     // Version
  ) {

  def createAtlasClassificationEntity(): AtlasTypesDef = {
    val classification = AtlasTypeUtil.createTraitTypeDef(name, description, version, ImmutableSet.of[String]())
    AtlasTypeUtil.getTypesDef(classification)
  }
}