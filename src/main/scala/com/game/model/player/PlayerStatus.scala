package com.game.model.player

import com.game.model.player.PlayerStatus.{defaultInvincibilityTime, defaultSpeed, defaultTotalHealth}
import indigo.Seconds

case class PlayerStatus(
                         health: Int = defaultTotalHealth,
                         totalHealth: Int = defaultTotalHealth,
                         isInvincibleFor: Seconds = Seconds(0),
                         speed: Double = defaultSpeed,
                         isFiring: Boolean = false,
                         isMoving: Boolean = false,
                       ) {
  def isAlive(): Boolean = health > 0
}

object PlayerStatus {
  val defaultSpeed = 8.0
  val defaultInvincibilityTime = Seconds(2)
  val defaultTotalHealth = 10
}