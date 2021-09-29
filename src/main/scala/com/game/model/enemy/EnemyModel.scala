package com.game.model.enemy

import com.game.model.enemy.EnemyModel.rotateVertex
import com.game.model.other.Projectile
import indigo.*
import indigoextras.geometry.{BoundingBox, Vertex}
import indigo.shared.IndigoLogger.consoleLog

final case class EnemyModel(
                              location: Vertex,
                              direction: Vertex = Vertex(0,1f),
                              hitBox: Vertex = Vertex(64, 64),
                              health: Int = 2,
                              isFiring: Boolean = true,
                              rof: Seconds = Seconds(1.0),
                              lastFired: Seconds = Seconds(0),
                              followAngle: Radians = Radians(0.2617), // radians/sec, rate of rotation towards target
                              movementPattern: List[Vertex] = List(),
                              lastUpdated: Seconds = Seconds(0),
                            ) {
  def isHit(incoming: Vertex): Boolean = getHitBoundingBox().contains(incoming)

  def getHitBoundingBox(): BoundingBox = BoundingBox(location, hitBox).moveBy(-hitBox.x / 2, -hitBox.y / 2)

  def takeDamage(damage: Int) = this.copy(health = this.health - damage)

  def isAlive(): Boolean = health > 0

  def emitProjectiles(gameTime: GameTime): (Seconds, List[Projectile]) = {
    val newLastFired = lastFired + gameTime.delta
    newLastFired < rof match
      case true => (newLastFired, List())
      case false => (Seconds(0), Projectile(location, direction) :: List())
  }

  def updateDirection(gameTime: GameTime, playerPostion: Vertex): (Seconds, Vertex) = {
    if (followAngle == Radians(0)) (lastUpdated, direction)

    val newLastUpdated = lastUpdated + gameTime.delta
    if (newLastUpdated >= Seconds(0.1)) {
      //val angle = Math.atan2(location.y - playerPostion.y, location.x - playerPostion.x)
      //val rotate = if (angle > 0) followAngle else followAngle * -1
      //val newVertex = rotateVertex(direction, rotate)
      val newVertex = rotateVertex(direction, followAngle)
      (Seconds(0), direction.moveTo(newVertex))
    } else {
      (newLastUpdated, direction)
    }
  }

  def update(gameTime: GameTime, playerPostion: Vertex): (EnemyModel, List[Projectile]) = {
    EnemyModel.update(this, gameTime, playerPostion)
  }

}

object EnemyModel {
  def apply(): EnemyModel = {
    EnemyModel(Vertex(170, 120))
  }

  def update(enemy: EnemyModel, gameTime: GameTime, playerPosition: Vertex): (EnemyModel, List[Projectile]) = {
    val (newLastFired, newProjectiles) = enemy.emitProjectiles(gameTime)
    val (newLastUpdated, newDirection) = enemy.updateDirection(gameTime, playerPosition)
    (enemy.copy(
      lastFired = newLastFired,
      lastUpdated = newLastUpdated,
      direction = newDirection
    ), newProjectiles)
  }

  def rotateVertex(vertex: Vertex, angle: Radians): Vertex = {
    consoleLog(s"vert ${vertex}")
    val s = Math.sin(angle.toDouble)
    val c = Math.cos(angle.toDouble)

    val x = vertex.x * c - vertex.y * s;
    val y = vertex.x * s + vertex.y * c;

    Vertex(x, y)
  }
}