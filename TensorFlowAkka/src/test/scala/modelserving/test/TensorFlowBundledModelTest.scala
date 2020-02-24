package modelserving.test

import org.scalatest.FlatSpec
import modelserving.model._
import pipelines.examples.modelserving.recommender.avro._

class TensorFlowBundledModelTest extends FlatSpec {

  val modelName = "Recommendor model"
  val modelPath = getClass.getResource("/model/").getPath

  val products = Seq(1L, 2L, 3L, 4L)

  val input = new RecommenderRecord(10L, products)

  "Processing of model" should "complete successfully" in {

    val model = TensorFlowBundleModel(modelName, modelPath)
    println("Model created")
    val result = model.get.score(input)
    println(result)
  }
}