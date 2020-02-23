package modelserving.streamlets

import akka.NotUsed
import akka.stream.scaladsl._
import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets._
import pipelines.examples.modelserving.recommender.avro._
import cloudflow.streamlets.avro._

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.util.Random

/**
 * Ingress of data for recommendations. In this case, every second we
 * load and send downstream one record that is randomly generated.
 */
final case object RecommenderRecordIngress extends AkkaStreamlet {

  // Output
  val out = AvroOutlet[RecommenderRecord]("recommender-records", _.datatype)

  // Shape
  final override val shape = StreamletShape.withOutlets(out)

  // Create Logic
  override final def createLogic = new RunnableGraphStreamletLogic {
    // Runnable graph
    def runnableGraph =
      RecordIngressUtils.makeSource().to(plainSink(out))
  }
}

object RecordIngressUtils {
  // Data frequency
  lazy val dataFrequencyMilliseconds: FiniteDuration = 5.second

  // Make source
  def makeSource(frequency: FiniteDuration = dataFrequencyMilliseconds): Source[RecommenderRecord, NotUsed] = {
    Source.repeat(RecordGenerator)
      .map(gen ⇒ gen.generateRecord())
      .throttle(1, frequency)
  }
}

// Request record generator
private object RecordGenerator {

  // Random generator
  protected lazy val generator = Random

  // Generate new request record
  def generateRecord(): RecommenderRecord = {
    val user = generator.nextInt(1000).toLong
    val nprods = generator.nextInt(30)
    val products = new ListBuffer[Long]()
    0 to nprods foreach { _ ⇒ products += generator.nextInt(300).toLong }
    new RecommenderRecord(user, products)
  }
}
