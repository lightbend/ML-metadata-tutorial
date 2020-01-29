package com.lightbend.atlas.utils

import com.google.common.collect.ImmutableSet
import com.sun.jersey.core.util.MultivaluedMapImpl
import org.apache.atlas.AtlasClientV2
import org.apache.atlas.`type`.AtlasTypeUtil
import org.apache.atlas.model.SearchFilter
import org.apache.atlas.model.instance.AtlasEntity
import org.apache.atlas.model.instance.EntityMutations.EntityOperation
import org.apache.atlas.model.typedef.AtlasTypesDef
import org.apache.commons.collections.CollectionUtils

import com.lightbend.atlas.metadata.MetadataSupport._

import scala.collection.JavaConverters._

class AtlasSupport(user : String, passw : String, url : String) {

  var client = new AtlasClientV2(Array(url), Array(user, passw))

  // Reset client
  def resetConnection() : Unit = {
    client.close()
    client = new AtlasClientV2(Array(url), Array(user, passw))
  }

  // Create Atlas entity
  def createInstance(entity: AtlasEntity) : AtlasEntity = {

    try {
      val response = client.createEntity(new AtlasEntity.AtlasEntityWithExtInfo(entity))
      val entities = response.getEntitiesByOperation(EntityOperation.CREATE)
      if (CollectionUtils.isNotEmpty(entities)) {
        val getByGuidResponse = client.getEntityByGuid(entities.get(0).getGuid)
        val ret = getByGuidResponse.getEntity
        System.out.println(s"Created entity of type ${ret.getTypeName}, with name ${ret.getAttribute(REFERENCEABLE_ATTRIBUTE_NAME)} and guid: ${ret.getGuid}")
        ret
      }
      else {
        val guids = response.getGuidAssignments()
        if (guids.size() > 0) {
          val guid = guids.values().asScala.toList.head
          val ret = client.getEntityByGuid(guid).getEntity
          System.out.println(s"Reading existing entity of type ${ret.getTypeName}, with name ${ret.getAttribute(REFERENCEABLE_ATTRIBUTE_NAME)} and guid: ${ret.getGuid}")
          ret
        }else
          null
      }
    }catch {
      case t: Throwable =>
        t.printStackTrace()
        null
    }
  }

  // Create Atlas type
  def createType(`type` : AtlasTypesDef): AtlasTypesDef = {
    // Check if type exists
    val params = new MultivaluedMapImpl()
    if(`type`.getEntityDefs.size() > 0) params.add(SearchFilter.PARAM_NAME, `type`.getEntityDefs.get(0).getName)
    if(`type`.getRelationshipDefs.size() > 0) params.add(SearchFilter.PARAM_NAME, `type`.getRelationshipDefs.get(0).getName)
    if(`type`.getClassificationDefs.size() > 0) params.add(SearchFilter.PARAM_NAME, `type`.getClassificationDefs.get(0).getName)
    try{
      val search = client.getAllTypeDefs(new SearchFilter(params))
      if((search.getEntityDefs.size() > 0) || (search.getRelationshipDefs.size() > 0) || (search.getClassificationDefs.size() > 0)){
        println(s"Type already exists")
        search
      } else {
        val result = client.createAtlasTypeDefs(`type`)
        println(s"Type is created with result - $result")
        result
      }
    }catch {
      case t: Throwable =>
        t.printStackTrace()
        null
    }
  }

  def getTypeByName(name : String) : AtlasTypesDef = {
    val searchParams = new MultivaluedMapImpl
    searchParams.add(SearchFilter.PARAM_NAME, name)
    val searchFilter = new SearchFilter(searchParams)
    client.getAllTypeDefs(searchFilter)
  }

  def deleteRelationshipByName(name : String) : Unit = {
    val types = getTypeByName(name)
    types.getRelationshipDefs.size() match {
      case l if (l > 0) =>
        val definition = AtlasTypeUtil.getTypesDef(types.getRelationshipDefs.get(0))
        try {
          val result = client.deleteAtlasTypeDefs(definition)
          println(s"Relationship $name is deleted with result - $result")
        } catch {
          case t: Throwable =>
            t.printStackTrace()
        }
      case _ =>
        println(s"Relationship $name does not exist")
    }
  }

  def deleteTypeByName(name : String) : Unit = {
    val types = getTypeByName(name)
    types.getEntityDefs.size() match {
      case l if (l > 0) =>
        val definition = AtlasTypeUtil.getTypesDef(types.getEntityDefs.get(0))
        try {
          val result = client.deleteAtlasTypeDefs(definition)
          println(s"Type $name is deleted with result - $result")
        } catch {
          case t: Throwable =>
            t.printStackTrace()
        }
      case _ =>
        println(s"Relationship $name does not exist")
    }
  }

  // Delete classification by name
  def deleteClassification(name : String) : Unit = {
    val classification = AtlasTypeUtil.createTraitTypeDef(name, "", ImmutableSet.of[String]())
    val definition = AtlasTypeUtil.getTypesDef(classification)
    try {
      val result = client.deleteAtlasTypeDefs(definition)
      println(s"Classification $name is deleted with result - $result")
    }catch {
      case t: Throwable =>
        t.printStackTrace()
    }
  }


  def deleteEntity(entitytype : String, name : String) : Unit = {
    try{
      val result = client.getEntitiesByAttribute(entitytype, Seq(Map(REFERENCEABLE_ATTRIBUTE_NAME -> name).asJava).asJava)
      result.getEntities.size() match {
        case l if (l > 0) =>
          val guid = result.getEntities.get(0).getGuid
          deleteEntity(guid)
        case _ =>
      }
    }catch{
      case t: Throwable =>
        t.printStackTrace()
    }
  }

  // Delete entity by GUID
  def deleteEntity(guid : String) : Unit = {
    try {
      val result = client.deleteEntityByGuid(guid)
      println(s"Entity $guid is deleted with result - $result")
    }catch {
      case t: Throwable =>
        t.printStackTrace()
    }
  }
}

object AtlasSupport{

  def apply(user : String = "admin", passw : String = "admin", url : String = "http://localhost:21000"): AtlasSupport = new AtlasSupport(user, passw, url)
}