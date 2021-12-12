package com.game.scenes.level1

import indigo.*
import indigo.scenes.SceneEvent
import indigoextras.geometry.{BoundingBox, Vertex}
import com.game.model.{GameControlScheme, GameState, PlayerControlState}
import com.game.model.enemy.EnemyModel
import com.game.model.other.Projectile
import com.game.model.player.PlayerModel
import com.game.scenes.gameover.GameOverScene
import com.game.events.EnemyDeathEvent

final case class Level1Model(
    controlScheme: GameControlScheme,
    player: PlayerModel,
    cameraBoundingBox: BoundingBox,
    gameMap: Level1GameMap,
    enemies: List[EnemyModel] = List(),
    enemyProjectiles: List[Projectile] = List(),
    lastUpdated: Seconds = Seconds.zero
) {
  def update(
      gameTime: GameTime,
      inputState: InputState
  ): GlobalEvent => Outcome[Level1Model] = {
    // FrameTick is always the last event, which is why we update the model's lastUpdated
    case FrameTick =>
      Level1Model.update(
        gameTime,
        inputState,
        this.copy(lastUpdated = gameTime.running)
      )(FrameTick)

    // Don't update lastUpdated since this event is being processed in this Frame
    case e =>
      Level1Model.update(gameTime, inputState, this)(e)
  }
}

object Level1Model {
  def initial(viewport: GameViewport): Level1Model = {
    Level1Model(
      controlScheme = GameControlScheme.keysArrow,
      cameraBoundingBox = BoundingBox(0, 0, viewport.width, viewport.height),
      gameMap = Level1GameMap(viewport),
      player = PlayerModel(viewport),
      enemies =
        EnemyModel(Vertex(170, 120)) :: EnemyModel(Vertex(400, 300)) :: Nil,
      enemyProjectiles = List(),
      lastUpdated = Seconds.zero
    )
  }

  def update(
      gameTime: GameTime,
      inputState: InputState,
      state: Level1Model
  ): GlobalEvent => Outcome[Level1Model] = {
    case FrameTick => {
      // Enemies and projectiles
      val updatedProjectiles = updateProjectilePositions(
        state.enemyProjectiles,
        state.cameraBoundingBox
      )
      val (updatedEnemies, newProjectiles, enemyEvents) = updateEnemies(
        state.enemies,
        state.player,
        state.player.projectiles,
        gameTime
      )

      // Player
      val updatedControlState = inputState.mapInputs(
        state.controlScheme.inputMapping,
        PlayerControlState()
      )
      val (newPlayer, playerEvents) = state.player.update(
        state.cameraBoundingBox,
        state.gameMap,
        gameTime,
        updatedControlState
      )

      val updatedAndNewEnemyProjectiles = newProjectiles ::: updatedProjectiles
      val (isPlayerHit, damage) = checkPlayerHit(
        player = newPlayer,
        projectiles = updatedAndNewEnemyProjectiles
      )
      val updatedPlayer =
        if isPlayerHit then newPlayer.takeDamage(damage) else newPlayer

      Outcome(
        state.copy(
          player = updatedPlayer,
          enemies = updatedEnemies,
          enemyProjectiles = updatedAndNewEnemyProjectiles
        )
      ).createGlobalEvents(newState =>
        if newState.player.status.isAlive() then playerEvents ::: enemyEvents
        else SceneEvent.JumpTo(GameOverScene.name) :: Nil
      )
    }

    case _ => Outcome(state)
  }

  def updateProjectilePositions(
      projectiles: List[Projectile],
      cameraBoundingBox: BoundingBox
  ): List[Projectile] = {
    // Can perhaps do it in one go
    projectiles
      .filter(projectile => cameraBoundingBox.contains(projectile.location))
      .map[Projectile](projectile => {
        projectile.copy(location =
          projectile.location.moveBy(projectile.direction * projectile.speed)
        )
      })
  }

  def updateEnemies(
      enemies: List[EnemyModel],
      player: PlayerModel,
      playerProjectiles: List[Projectile],
      gameTime: GameTime
  ): (List[EnemyModel], List[Projectile], List[GlobalEvent]) = {
    val (livingEnemies, deadEnemies) = enemies
      .map(enemy => {
        val (isHit, damage) = checkEnemyHit(enemy, playerProjectiles)
        if isHit then enemy.takeDamage(damage) else enemy
      })
      .partition(_.isAlive())

    val (updatedEnemies, newProjectiles) =
      livingEnemies.map(_.update(gameTime, player.location)).unzip
    val deadEnemyEvents =
      deadEnemies.map(enemy => EnemyDeathEvent(enemy.location.toPoint))

    (updatedEnemies, newProjectiles.flatten, deadEnemyEvents)
  }

  def checkPlayerHit(
      player: PlayerModel,
      projectiles: List[Projectile]
  ): (Boolean, Int) = {
    val hits = projectiles.find(projectile => player.isHit(projectile.location))
    hits match {
      case Some(value) => (true, value.damage)
      case None        => (false, 0)
    }
  }

  def checkEnemyHit(
      enemy: EnemyModel,
      projectiles: List[Projectile]
  ): (Boolean, Int) = {
    val hits = projectiles.find(projectile => enemy.isHit(projectile.location))
    hits match {
      case Some(value) => (true, value.damage)
      case None        => (false, 0)
    }
  }
}
