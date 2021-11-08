package com.game.model.player

import indigo.*
import indigoextras.geometry.{BoundingBox, Vertex}
import indigo.IndigoLogger.*
import com.game.model.player.PlayerStatus
import com.game.model.{GameState, PlayerControlState}
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

  def getNewLocation(velocity: Vector2): Vertex = Vertex(this.location.x + velocity.x, this.location.y + velocity.y)

  def update(
              cameraBoundingBox: BoundingBox,
              gameMap: Level1GameMap,
              gameTime: GameTime,
              playerControlState: PlayerControlState
            ): (PlayerModel, List[GlobalEvent]) = {
    val (player, hasNewProjectiles) = PlayerModel.update(this, cameraBoundingBox, gameMap, gameTime, playerControlState)

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

  def update(
              player: PlayerModel,
              cameraBoundingBox: BoundingBox,
              gameMap: Level1GameMap,
              gameTime: GameTime,
              playerControlState: PlayerControlState
            ): (PlayerModel, Boolean) = {
    val newPosition = updatePlayerPosition(player, cameraBoundingBox, gameMap, gameTime, playerControlState)
    val (lastFired, updatedProjectiles, hasNewProjectiles) = updatePlayerProjectiles(player, cameraBoundingBox, gameTime, playerControlState)
    val newStatus = updtateStatus(player.status, gameTime)
    val newPlayer = player.copy(
      location = newPosition,
      projectiles = updatedProjectiles,
      lastFired = lastFired,
      status = newStatus,
    )
    (newPlayer, hasNewProjectiles)
  }

  def updatePlayerPosition(
                            player: PlayerModel,
                            cameraBoundingBox: BoundingBox,
                            gameMap: Level1GameMap,
                            gameTime: GameTime,
                            playerControlState: PlayerControlState
                          ): Vertex = {
    val newLocation = Vertex(
      player.location.x + playerControlState.direction.x * player.status.speed * gameTime.delta.toDouble,
      player.location.y + playerControlState.direction.y * player.status.speed * gameTime.delta.toDouble
    )

    val contains = playerControlState.direction match
      case Vector2(0, 0) => true
      case Vector2(1.0, 0) => cameraBoundingBox.contains(newLocation.x + player.hitBox.x / 2, newLocation.y)
      case Vector2(-1.0, 0) => cameraBoundingBox.contains(newLocation.x - player.hitBox.x / 2, newLocation.y)
      case Vector2(0, 1.0) => cameraBoundingBox.contains(newLocation.x, newLocation.y + player.hitBox.y / 2)
      case Vector2(0, -1.0) => cameraBoundingBox.contains(newLocation.x, newLocation.y - player.hitBox.y / 2)
      case Vector2(1.0, 1.0) => cameraBoundingBox.contains(newLocation.x + player.hitBox.x / 2, newLocation.y + player.hitBox.y / 2)
      case Vector2(-1.0, -1.0) => cameraBoundingBox.contains(newLocation.x - player.hitBox.x / 2, newLocation.y - player.hitBox.y / 2)
      case Vector2(1.0, -1.0) => cameraBoundingBox.contains(newLocation.x + player.hitBox.x / 2, newLocation.y - player.hitBox.y / 2)
      case Vector2(-1.0, 1.0) => cameraBoundingBox.contains(newLocation.x - player.hitBox.x / 2, newLocation.y + player.hitBox.y / 2)

    if contains then newLocation else player.location
  }

  def updatePlayerProjectiles(
                               player: PlayerModel,
                               cameraBoundingBox: BoundingBox,
                               gameTime: GameTime,
                               playerControlState: PlayerControlState
                             ): (Seconds, List[Projectile], Boolean) = {
    val shiftedProjectiles = newProjectilePostions(player.projectiles, cameraBoundingBox)
    playerControlState.firing match
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
}