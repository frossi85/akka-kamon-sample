package sample.kamon

import akka.actor.{Actor, ActorLogging, Props}
import sample.kamon.KafkaActorConsumer.{Consume, Start}
import sample.kamon.MessageGenerator.{ConstantLoad, Peak, Schedule}
import sample.kamon.RandomNumberActor.{GenerateNumber, GenerateSecureNumber}

import scala.util.Random

class LongConsumer extends Actor with ActorLogging {

  val telemetryDumperSupervisor = context.actorOf(
    TelemetryDumperSupervisor.props(),
    "telemetry-dumper-supervisor"
  )

  lazy val topicStream: Iterable[Int] = Stream.from(1)

  override def preStart(): Unit = {
    log.info("######## LongConsumer preStart!!!")
    self ! Consume
  }

  def receive: Receive = {
    case Consume =>
      log.info("######## LongConsumer Consume!!!")
      topicStream foreach { kafkaMessage =>
        consume(kafkaMessage)
      }
  }

  def consume(kafkaMessage: Int): Unit = {
    telemetryDumperSupervisor ! kafkaMessage
  }
}

object KafkaActorConsumer {
  object Consume
  object Start
}









