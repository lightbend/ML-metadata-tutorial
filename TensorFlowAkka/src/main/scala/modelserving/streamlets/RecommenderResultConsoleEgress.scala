package modelserving.streamlets

import cloudflow.akkastream.AkkaStreamlet
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet
import pipelines.examples.modelserving.recommender.avro._

final case object RecommenderResultConsoleEgress extends AkkaStreamlet {

  // Input
  val in = AvroInlet[RecommenderResult]("inference-result")
  // Shape
  final override val shape = StreamletShape.withInlets(in)

  // Create logic
  override def createLogic = new RunnableGraphStreamletLogic() {

    // Write method
    def write(record: RecommenderResult): Unit = println(record.toString)

    // Runnable graph
    def runnableGraph = sourceWithOffsetContext(in).map(write(_)).to(sinkWithOffsetContext)
  }
}
