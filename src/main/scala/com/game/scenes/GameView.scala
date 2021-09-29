package com.game.scenes

import indigo.*
import indigo.shared.*
import indigo.IndigoLogger._
import com.game.init.ViewConfig
import com.game.model.GameModel
import com.game.init.StaticAssets
import com.game.model.player.Direction

object GameView {
  def update(viewConfig: ViewConfig, model: GameModel, staticAssets: StaticAssets): Outcome[SceneUpdateFragment] = {
    Outcome(
      SceneUpdateFragment
        .empty
        .addLayer(
          Layer(
            BindingKey("ui"),
            uiLayer(viewConfig, model)
          )
        )
        .addLayer(
          Layer(
            BindingKey("game"),
            gameLayer(viewConfig, model, staticAssets)
          )
        )
    )
    //).addGlobalEvents(playSounds(model, staticAssets))
  }

  def gameLayer(viewConfig: ViewConfig, currentState: GameModel, staticAssets: StaticAssets): List[SceneNode] = {
    drawCharacter(viewConfig, currentState, staticAssets.character) ::
      drawBullets(viewConfig, currentState, staticAssets.bullet)
  }

  def drawCharacter(config: ViewConfig, currentState: GameModel, characterAsset: Graphic[_]): Graphic[_] = {
    // val radians = getAngleDifference(currentState.player.direction)
    // characterAsset.rotateBy(radians).moveTo(currentState.player.location.toPoint)
    characterAsset.moveTo(currentState.player.location.toPoint)
  }

  def drawBullets(config: ViewConfig, currentState: GameModel, bulletAsset: Graphic[_]): List[Graphic[_]] = {
    currentState.player.projectiles map { projectile => bulletAsset.moveTo(projectile.location.toPoint) }
  }

  def playSounds(currentState: GameModel, staticAssets: StaticAssets): List[GlobalEvent] = {
    currentState.player.lastFired.equals(Seconds(0)) && currentState.player.status.isFiring match
      case true => PlaySound(AssetName("fireSound1"), Volume.Max) :: List()
      case false => Nil
  }

  def getAngleDifference(newDirection: Direction): Radians = {
    newDirection match
      case Direction.UP => Radians(0)
      case Direction.DOWN => Radians.PI
      case Direction.LEFT => Radians.fromDegrees(-90)
      case Direction.RIGHT => Radians.fromDegrees(90)
  }

  def uiLayer(config: ViewConfig, currentState: GameModel): List[SceneNode] = {
    drawHealthBar(config, currentState)
  }

  def drawHealthBar(config: ViewConfig, currentState: GameModel): List[SceneNode] = {
    val currentHealth = currentState.player.status.health
    val totalHealth = currentState.player.status.totalHealth
    val healthBar = (0 until totalHealth).map { index => drawHealthBox(index, currentHealth) }
    healthBar.toList
  }

  def drawHealthBox(index: Int, currentHealth: Int): SceneNode = {
    val fillColor = if (currentHealth > index + 1) then RGBA.White else RGBA.Zero
    val offset = 15
    Shape.Box(
      Rectangle(Point(17 * index + offset, offset), Size(10, 20)),
      Fill.Color(fillColor),
      Stroke(2, RGBA.White)
    )
  }
}
