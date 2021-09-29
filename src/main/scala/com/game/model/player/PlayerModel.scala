package com.game.model.player

import indigo.*
import indigoextras.geometry.{BoundingBox, Vertex}
import indigo.IndigoLogger.*
import com.game.model.player.PlayerStatus
import com.game.model.GameState
import com.game.model.other.Projectile
import com.game.scenes.level1.Level1GameMap

enum Direction:
  case LEFT, RIGHT, UP, DOWN

final case class PlayerModel(
                              location: Vertex,
                              direction: Direction,
                              hitBox: Vertex,
                              status: PlayerStatus,
                              projectiles: List[Projectile],
                              rof: Seconds = Seconds(0.25),
                              lastFired: Seconds = Seconds(0)
                            ) {
  def isHit(incoming: Vertex): Boolean = getHitBoundingBox().contains(incoming)

  def getHitBoundingBox(): BoundingBox = BoundingBox(location, hitBox).moveBy(-hitBox.x / 2, -hitBox.y / 2)

  def takeDamage(damage: Int): PlayerModel = {
    if (status.isInvincibleFor > Seconds(0))
      this
    else
      PlayerModel.takeDamage(this, damage)
  }

  def turn(direction: Direction): PlayerModel = {
    if (this.direction == direction)
      this
    else
      this.copy(direction = direction)
  }

  def fire(isFiring: Boolean): PlayerModel = {
    this.copy(status = this.status.copy(isFiring = isFiring))
  }

  def move(isMoving: Boolean): PlayerModel = {
    if (this.status.isMoving == isMoving)
      this
    else
      this.copy(status = this.status.copy(isMoving = isMoving))
  }

  def update(cameraBoundingBox: BoundingBox, gameMap: Level1GameMap, gameTime: GameTime): (PlayerModel, List[GlobalEvent]) = {
    val (player, hasNewProjectiles) = PlayerModel.update(this, cameraBoundingBox, gameMap, gameTime)

    if (hasNewProjectiles)
      (player, PlaySound(AssetName("fireSound1"), Volume.Max) :: Nil)
    else
      (player, Nil)
  }
}

object PlayerModel {
  def apply(viewport: GameViewport): PlayerModel = {
    PlayerModel(
      Vertex(viewport.horizontalMiddle, viewport.height - 32),
      Direction.UP,
      Vertex(32, 32),
      PlayerStatus(),
      List()
    )
  }

  def update(player: PlayerModel, cameraBoundingBox: BoundingBox, gameMap: Level1GameMap, gameTime: GameTime): (PlayerModel, Boolean) = {
    val newPosition = updatePlayerPosition(player, cameraBoundingBox, gameMap)
    val (lastFired, updatedProjectiles, hasNewProjectiles) = updatePlayerProjectiles(player, cameraBoundingBox, gameTime)
    val newStatus = updtateStatus(player.status, gameTime)
    val newPlayer = player.copy(
      location = newPosition,
      projectiles = updatedProjectiles,
      lastFired = lastFired,
      status = newStatus,
    )
    (newPlayer, hasNewProjectiles)
  }

  def updatePlayerPosition(player: PlayerModel, cameraBoundingBox: BoundingBox, gameMap: Level1GameMap): Vertex = {
    player.status.isMoving match
      case true => newPlayerPosition(player.location, player.direction, player.status.speed, cameraBoundingBox)
      case false => player.location
  }

  def updatePlayerProjectiles(
                               player: PlayerModel,
                               cameraBoundingBox: BoundingBox,
                               gameTime: GameTime
                             ): (Seconds, List[Projectile], Boolean) = {
    val shiftedProjectiles = newProjectilePostions(player.projectiles, cameraBoundingBox)
    player.status.isFiring match
      case true => {
        val (lastFired, newProjectiles) = addProjectiles(player, shiftedProjectiles, gameTime)
        (lastFired, newProjectiles, (newProjectiles.length > shiftedProjectiles.length))
      }
      case false => (player.lastFired, shiftedProjectiles, false)
  }

  def updtateStatus(status: PlayerStatus, gameTime: GameTime): PlayerStatus = {
    if (status.isInvincibleFor == Seconds(0)) {
      return status
    }
    val remainaingTime = status.isInvincibleFor - gameTime.delta
    if (remainaingTime < Seconds(0)) {
      status.copy(
        isInvincibleFor = Seconds(0)
      )
    } else {
      status.copy(
        isInvincibleFor = remainaingTime
      )
    }
  }

  def takeDamage(player: PlayerModel, damage: Int): PlayerModel = {
    player.copy(
      status = player.status.copy(
        isInvincibleFor = PlayerStatus.defaultInvincibilityTime,
        health = player.status.health - damage
      )
    )
  }

  def addProjectiles(
                      player: PlayerModel,
                      projectiles: List[Projectile],
                      gameTime: GameTime
                     ): (Seconds, List[Projectile]) = {
    val newLastFired = player.lastFired + gameTime.delta
    newLastFired < player.rof match
      case true => (newLastFired, projectiles)
      case false => (Seconds.zero, addProjectile(player.location, player.direction) :: projectiles)
  }

  def addProjectile(location: Vertex, direction: Direction): Projectile = {
    /*
    val direction = player.direction match
      case Direction.UP => Vertex(0, -1.0f)
      case Direction.DOWN => Vertex(0, 1.0f)
      case Direction.LEFT => Vertex(-1.0, 0)
      case Direction.RIGHT => Vertex(1.0, 0)
    */
    val direction = Vertex(0, -1.0f)
    Projectile(location, direction)
  }

  def newProjectilePostions(projectiles: List[Projectile], cameraBoundingBox: BoundingBox): List[Projectile] = {
    // Can perhaps do it in one go
    projectiles
      .filter(projectile => cameraBoundingBox.contains(projectile.location))
      .map[Projectile](projectile => {
        projectile.copy(location = projectile.location.moveBy(projectile.direction * projectile.speed))
      })
  }

  def newPlayerPosition(currentPosition: Vertex, currentDirection: Direction, speed: Double, camerBoundingBox: BoundingBox): Vertex = {
    currentDirection match
      case Direction.UP => {
        val nextPosition = currentPosition.moveBy(0, -speed)
        camerBoundingBox.contains(nextPosition.moveBy(0, -32)) match
          case true => nextPosition
          case false => currentPosition
      }
      case Direction.DOWN => {
        val nextPosition = currentPosition.moveBy(0, speed)
        camerBoundingBox.contains(nextPosition.moveBy(0, 32)) match
          case true => nextPosition
          case false => currentPosition
      }
      case Direction.LEFT => {
        val nextPosition = currentPosition.moveBy(-speed, 0)
        camerBoundingBox.contains(nextPosition.moveBy(-32, 0)) match
          case true => nextPosition
          case false => currentPosition
      }
      case Direction.RIGHT => {
        val nextPosition = currentPosition.moveBy(speed, 0)
        camerBoundingBox.contains(nextPosition.moveBy(32, 0)) match
          case true => nextPosition
          case false => currentPosition
      }
  }
}