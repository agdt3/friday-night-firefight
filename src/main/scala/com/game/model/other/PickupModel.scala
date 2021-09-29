package com.game.model.other

import indigo.*
import indigoextras.geometry.Vertex
import com.game.model.player.PlayerModel

trait PickupModel(val timeRemaining: Seconds)

case class Nitro(speedBoost: Float = 2.0, location: Vertex) extends PickupModel(timeRemaining = Seconds(5.0)) {
  def start(playerModel: PlayerModel): PlayerModel = {
    playerModel.copy(status = playerModel.status.copy(speed = playerModel.status.speed + speedBoost))
  }

  def end(playerModel: PlayerModel): PlayerModel = {
    playerModel.copy(status = playerModel.status.copy(speed = playerModel.status.speed - speedBoost))
  }

  def isRunning(gameTimeDelta: Seconds): Boolean = timeRemaining - gameTimeDelta <= Seconds(0)
}

case class Booze(healthBoost: Int = 2, location: Vertex) extends PickupModel(timeRemaining = Seconds(0)) {
  def start(playerModel: PlayerModel): PlayerModel = {
    playerModel.copy(status = playerModel.status.copy(health = playerModel.status.health + healthBoost))
  }

  def end(playerModel: PlayerModel): PlayerModel = playerModel

  def isRunning(gameTimeDelta: Seconds): Boolean = false
}