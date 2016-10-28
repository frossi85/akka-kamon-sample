package sample.kamon

import akka.actor.{Actor, ActorLogging, Props}
import sample.kamon.TelemetryDumperSupervisor.Saved

import scala.util.Random

class TelemetryDumper(backoffStrategy: BackoffStrategy, telemetry: Int)
  extends Actor with ActorLogging {

  import context.dispatcher

  override def preStart(): Unit = {
    context.system.scheduler.scheduleOnce(backoffStrategy.backoffTime, self, telemetry)
    backoffStrategy.increment()
  }

  // The only stable data the actor has during restarts is those embedded in
  // the Props when it was created. In this case telemetryRepository, backoffStrategy and telemetry.
  def receive: Receive = {
    case telemetry: Int =>
      log.info("####### Telemetry: " + telemetry)
      Thread.sleep(30 * Random.nextInt(10))

      backoffStrategy.reset()

      //indicate to supervisor that the operation was a success
      context.parent ! Saved(telemetry)
      // Don't forget to stop the actor after it has nothing more to do
      context.stop(self)
  }
}

object TelemetryDumper {
  def props(backoffStrategy: BackoffStrategy, telemetry: Int): Props =
    Props(classOf[TelemetryDumper], backoffStrategy, telemetry)
}


