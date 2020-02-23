package modelserving.streamlets

import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets.avro._
import cloudflow.streamlets._
import modelserving.model.TensorFlowBundleModel
import pipelines.examples.modelserving.recommender.avro._

final class RecommenderModelServer extends AkkaStreamlet {

  val modelName = "Recommendor model"
  val modelPath = getClass.getResource("/model/").getPath


  val in  = AvroInlet[RecommenderRecord]("recommender-records")
  val out = AvroOutlet[RecommenderResult]("recommender-results", _.inputRecord.datatype)

  final override val shape = StreamletShape.withInlets(in).withOutlets(out)

  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {
    val model = TensorFlowBundleModel(modelName, modelPath).get
    println(s"Created model $model")

    def runnableGraph() =
      sourceWithOffsetContext(in)
        .via(dataFlow)
        .to(sinkWithOffsetContext(out))

    // Data processing
    protected def dataFlow =
      FlowWithOffsetContext[RecommenderRecord]
      .map(model.score(_))
  }
}
