package com.game.model.player

import com.game.model.player.PlayerStatus.{defaultInvincibilityTime, defaultSpeed, defaultTotalHealth}
import indigo.{Seconds,Vector2}

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
  val defaultSpeed = 360.0 // Speed gets divided by time delta, so its about 6 pixels per second
  val defaultInvincibilityTime = Seconds(2)
  val defaultTotalHealth = 10
}