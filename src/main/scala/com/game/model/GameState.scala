package com.game.model
import indigo.Seconds

sealed trait GameState {
  val hasCrashed: Boolean
  def updateNow(time: Seconds): GameState
}

object GameState {
  final case class Crashed(crashedAt: Seconds, lastUpdated: Seconds)
    extends GameState {
    val hasCrashed: Boolean = true

    def updateNow(time: Seconds): GameState.Crashed =
      this.copy(lastUpdated = time)
  }

  final case class Running(lastUpdated: Seconds) extends GameState {
    val hasCrashed: Boolean = false

    def updateNow(time: Seconds): GameState.Running =
      this.copy(lastUpdated = time)

    def crash(crashedAt: Seconds): GameState.Crashed =
      GameState.Crashed(crashedAt, lastUpdated)
  }

  object Running {
    val start: Running = GameState.Running(Seconds.zero)
  }
}
