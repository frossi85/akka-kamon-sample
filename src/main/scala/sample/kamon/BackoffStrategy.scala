package sample.kamon

import scala.concurrent.duration.FiniteDuration

trait BackoffStrategy {
  def increment(): Unit

  def backoffTime: FiniteDuration

  def reset(): Unit
}
