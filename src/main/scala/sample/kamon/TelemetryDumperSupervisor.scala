package sample.kamon

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import sample.kamon.TelemetryDumperSupervisor.Saved

import scala.util.Random

class TelemetryDumperSupervisor extends Actor with ActorLogging {

  log.info("TelemetryDumperSupervisor started!")

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case e: Exception =>
      log.error(e, "There was an error when trying to save telemetry data, restarting.")
      Restart
  }

  def receive: Receive = {
    case telemetry: Int =>
      Thread.sleep(40 * Random.nextInt(10))
      context.actorOf(TelemetryDumper.props(new SimpleBackoffStrategy, telemetry))
    case Saved(telemetry) =>
    //Make some post process
  }
}

object TelemetryDumperSupervisor {
  def props(): Props = Props(classOf[TelemetryDumperSupervisor])

  case class Saved(telemetry: Int)
}