package sample.kamon

import akka.actor._
import akka.pattern.{Backoff, BackoffSupervisor}
import kamon.Kamon
import scala.concurrent.duration._

object Main extends App {

  Kamon.start()

  val system = ActorSystem("application")

  val actorProps = Props[LongConsumer]

  val supervisor = BackoffSupervisor.props(
    Backoff.onStop(
      actorProps,
      childName = "telemetry-consumer",
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2 // adds 20% "noise" to vary the intervals slightly
    ).withSupervisorStrategy(
      OneForOneStrategy() {
        case ex =>
          system.log.error(ex, "There was an error in KafkaActor")
          SupervisorStrategy.Restart //Here we can add some log or send a notification
      })
  )

  system.actorOf(supervisor)
}
