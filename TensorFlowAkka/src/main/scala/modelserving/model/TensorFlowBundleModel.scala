package modelserving.model

import java.io.File
import java.nio.file.Files

import com.google.protobuf.Descriptors
import org.tensorflow.framework._
import org.tensorflow.{ SavedModelBundle, Tensor }
import pipelines.examples.modelserving.recommender.avro._

import scala.collection.JavaConverters._
import scala.collection.mutable.{ Map ⇒ MMap }

/**
 * Class encapsulating TensorFlow (SavedModelBundle) model processing.
 *
 * @param  modelName model Name
 * @param  modelPath model location
 */
class TensorFlowBundleModel(modelName: String, modelPath: String) extends TensorFlowBase(modelName, modelPath) {

  // get tags. We assume here that the first tag is the one we use
  private val tags: Seq[String] = getTags(modelPath)
  private val bundle = SavedModelBundle.load(modelPath, tags(0))
  private val graph = bundle.graph
  // get metatagraph and signature
  private val metaGraphDef = MetaGraphDef.parseFrom(bundle.metaGraphDef)
  private val signatureMap = metaGraphDef.getSignatureDefMap.asScala
  //  parse signature, so that we can use definitions (if necessary) programmatically in score method
  private val signatures = parseSignatures(signatureMap)
  // Create TensorFlow session
  private val session = bundle.session

  /**
   * Convert incoming wine record to Tensor.
   */
  private def toTensor(record: RecommenderRecord): Seq[Tensor[_]] = {
    val products = Tensor.create(record.products.map(p ⇒ Array(p.toFloat)).toArray)
    val users = Tensor.create(record.products.map(_ ⇒ Array(record.user.toFloat)).toArray)
    Seq(users, products)
  }

  /**
   * Usage of TensorFlow bundled model for Wine scoring.
   */
  override def invokeModel(record: RecommenderRecord): Either[String, RecommenderServingOutput] = {
    try {
      // Create record tensor
      val modelInput = toTensor(record)
      // Serve model using TensorFlow APIs
      val signature = signatures.head._2
      val tinputs = signature.inputs.map(inp ⇒ inp._2.name).toSeq
      val toutput = signature.outputs.head._2.name
      val result = session.runner
        .feed(tinputs(0), modelInput(0))
        .feed(tinputs(1), modelInput(1))
        .fetch(toutput).run().get(0)
      // process result
      val rshape = result.shape
      val rMatrix = Array.ofDim[Float](rshape(0).asInstanceOf[Int], rshape(1).asInstanceOf[Int])
      result.copyTo(rMatrix)
      val prediction = rMatrix.map(arrayV ⇒ arrayV(0).toDouble)
      val predictions = prediction.zip(record.products).map(r ⇒ (r._2.toString, r._1)).unzip
      Right(RecommenderServingOutput(predictions._1, predictions._2.toSeq))
    } catch {
      case t: Throwable ⇒ Left(t.getMessage)
    }
  }

  /**
   * Cleanup when a model is not used anymore
   */
  override def cleanup(): Unit = {
    try {
      session.close
    } catch {
      case t: Throwable ⇒
        println(s"WARNING: in TensorFlowBundleModel.cleanup(), call to session.close threw $t. Ignoring")
    }
    try {
      graph.close
    } catch {
      case t: Throwable ⇒
        println(s"WARNING: in TensorFlowBundleModel.cleanup(), call to graph.close threw $t. Ignoring")
    }
  }

  /**
   * Parse signatures
   *
   * @param signatures - signatures from metagraph
   * @returns map of names/signatures
   */
  private def parseSignatures(signatures: MMap[String, SignatureDef]): Map[String, Signature] = {
    signatures.map(signature ⇒
      signature._1 -> Signature(parseInputOutput(signature._2.getInputsMap.asScala), parseInputOutput(signature._2.getOutputsMap.asScala))).toMap
  }

  /**
   * Parse Input/Output
   *
   * @param inputOutputs - Input/Output definition from metagraph
   * @returns map of names/fields
   */
  private def parseInputOutput(inputOutputs: MMap[String, TensorInfo]): Map[String, Field] =
    inputOutputs.map {
      case (key, info) ⇒
        var name = ""
        var dtype: Descriptors.EnumValueDescriptor = null
        var shape = Seq.empty[Int]
        info.getAllFields.asScala.foreach { descriptor ⇒
          if (descriptor._1.getName.contains("shape")) {
            descriptor._2.asInstanceOf[TensorShapeProto].getDimList.toArray.map(d ⇒
              d.asInstanceOf[TensorShapeProto.Dim].getSize).toSeq.foreach(v ⇒ shape = shape :+ v.toInt)

          }
          if (descriptor._1.getName.contains("name")) {
            name = descriptor._2.toString.split(":")(0)
          }
          if (descriptor._1.getName.contains("dtype")) {
            dtype = descriptor._2.asInstanceOf[Descriptors.EnumValueDescriptor]
          }
        }
        key -> Field(name, dtype, shape)
    }.toMap

  /**
   * Gets all tags in the saved bundle and uses the first one. If you need a specific tag, overwrite this method
   * With a seq (of one) tags returning desired tag.
   *
   * @param directory - directory for saved model
   * @returns sequence of tags
   */
  private def getTags(directory: String): Seq[String] = {

    val d = new File(directory)
    val pbfiles = if (d.exists && d.isDirectory)
      d.listFiles.filter(_.isFile).filter(name ⇒ (name.getName.endsWith("pb") || name.getName.endsWith("pbtxt"))).toList
    else
      List[File]()
    if (pbfiles.length > 0) {
      val byteArray = Files.readAllBytes(pbfiles(0).toPath)
      SavedModel.parseFrom(byteArray).getMetaGraphsList.asScala.
        flatMap(graph ⇒ graph.getMetaInfoDef.getTagsList.asByteStringList.asScala.map(_.toStringUtf8))
    } else
      Seq.empty
  }
}

object TensorFlowBundleModel {
  def apply(modelName: String, modelPath: String): Option[TensorFlowBundleModel] = {
    try {
      Some(new TensorFlowBundleModel(modelName: String, modelPath: String))
    } catch {
      case t: Throwable ⇒
        println(s"Failed to create TensorFlowBundleModel with the name $modelName from directory $modelPath")
        t.printStackTrace()
        None
    }
  }
}

/** Definition of the field (input/output) */
case class Field(name: String, `type`: Descriptors.EnumValueDescriptor, shape: Seq[Int])

/** Definition of the signature */
case class Signature(inputs: Map[String, Field], outputs: Map[String, Field])
