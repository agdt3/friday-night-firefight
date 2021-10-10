package com.game.scenes.level1

import com.game.init.{StaticAssets, ViewConfig}
import com.game.model.player.Direction
import com.game.scenes.level1.Level1View.gameLayer

import indigo.*
import indigo.shared.*
import indigo.shared.IndigoLogger.consoleLog
import indigoextras.geometry.{BoundingBox, Vertex}

object Level1View {
  def update(
              viewConfig: ViewConfig,
              model: Level1Model,
              viewModel: Level1ViewModel,
              staticAssets: StaticAssets
            ): Outcome[SceneUpdateFragment] = {
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
            gameLayer(viewConfig, model, viewModel, staticAssets)
          )
          //).withCamera(Camera.LookAt(model.player.location.toPoint))
          //).withCamera(Camera.Fixed(model.cameraBoundingBox.topLeft.toPoint))
        )
    )
  }

  def gameLayer(config: ViewConfig, model: Level1Model, viewModel: Level1ViewModel, staticAssets: StaticAssets): List[SceneNode] = {
    drawCharacter(config, model, staticAssets.character) ::
      drawEnemies(config, model, staticAssets.enemy1) :::
      drawBullets(config, model, staticAssets.bullet) :::
      drawEnemyBullets(config, model, staticAssets.bullet)
      ::: drawExplosions(config, viewModel, staticAssets.explosionMaterial)
      // ::: _drawBoundingBoxes(config, model)
  }

  def drawCharacter(config: ViewConfig, model: Level1Model, characterAsset: Graphic[Material.ImageEffects]): Graphic[_] = {
    // val radians = getAngleDifference(model.player.direction)
    // characterAsset.rotateBy(radians).moveTo(model.player.location.toPoint)

    characterAsset
      .moveTo(model.player.location.toPoint)
      .modifyMaterial(material => {
        if (model.player.status.isInvincibleFor > Seconds(0)) then material.withAlpha(0.5) else material
      })
  }

  def drawBullets(config: ViewConfig, model: Level1Model, bulletAsset: Graphic[_]): List[Graphic[_]] = {
    model.player.projectiles map { projectile => bulletAsset.moveTo(projectile.location.toPoint) }
  }

  def drawEnemies(config: ViewConfig, model: Level1Model, enemyAsset: Graphic[_]): List[Graphic[_]] = {
    //model.enemies.map {enemy => enemyAsset.moveTo(enemy.location.toPoint)}
    model.enemies.map(enemy => {
      // val angle = getAngleDifference(enemy.direction, Vertex(0, 1f))
      // enemyAsset.moveTo(enemy.location.toPoint).rotateBy(angle)}
      enemyAsset.moveTo(enemy.location.toPoint)
    })
  }

  def drawEnemyBullets(config: ViewConfig, model: Level1Model, bulletAsset: Graphic[_]): List[Graphic[_]] = {
    model.enemyProjectiles map { projectile => bulletAsset.moveTo(projectile.location.toPoint) }
  }

  def drawExplosions(config: ViewConfig, viewModel: Level1ViewModel, explosionMaterial: Material): List[Sprite[_]] = {
    viewModel.explosions map { explosion =>
      val offset = explosion.location - Point(32, 32)
      Sprite(
        BindingKey("explosion animation"),
        offset.x,
        offset.y,
        1,
        explosion.animationKey,
        explosionMaterial
      ).play()
    }
  }

  def getAngleDifference(newDirection: Direction): Radians = {
    newDirection match
      case Direction.UP => Radians(0)
      case Direction.DOWN => Radians.PI
      case Direction.LEFT => Radians.fromDegrees(-90)
      case Direction.RIGHT => Radians.fromDegrees(90)
  }

  def getAngleDifference(v1: Vertex, v2: Vertex): Radians = {
    // α = arccos[(a · b) / (|a| * |b|)]
    val angle = Math.acos(v1.dot(v2) / (v1.length * v2.length))
    Radians(angle)
  }

  def uiLayer(config: ViewConfig, model: Level1Model): List[SceneNode] = {
    drawHealthBar(config, model)
  }

  def drawHealthBar(config: ViewConfig, model: Level1Model): List[SceneNode] = {
    val currentHealth = model.player.status.health
    val totalHealth = model.player.status.totalHealth
    val healthBar = (0 until totalHealth).map { index => drawHealthBox(index, currentHealth) }
    healthBar.toList
  }

  def drawHealthBox(index: Int, currentHealth: Int): SceneNode = {
    val fillColor = if (currentHealth >= index + 1) then RGBA.White else RGBA.Zero
    val offset = 15
    Shape.Box(
      Rectangle(Point(17 * index + offset, offset), Size(10, 20)),
      Fill.Color(fillColor),
      Stroke(2, RGBA.White)
    )
  }

  private def _drawBoundingBoxes(config: ViewConfig, model: Level1Model): List[SceneNode] = {
    val enemyBoxes = model.enemies.flatMap(enemy => {
      _drawBoundingBox(enemy.getHitBoundingBox(), RGBA.Green)
    })

    _drawBoundingBox(model.player.getHitBoundingBox(), RGBA.Red) :::
      _drawBoundingBox(model.cameraBoundingBox, RGBA.Blue)
      ::: enemyBoxes
  }

  private def _drawBoundingBox(box: BoundingBox, color: RGBA): List[Shape.Line] = {
    List(
      Shape.Line(
        box.topLeft.toPoint,
        box.topRight.toPoint,
        Stroke(2, color)
      ),
      Shape.Line(
        box.topRight.toPoint,
        box.bottomRight.toPoint,
        Stroke(2, color)
      ),
      Shape.Line(
        box.bottomRight.toPoint,
        box.bottomLeft.toPoint,
        Stroke(2, color)
      ),
      Shape.Line(
        box.bottomLeft.toPoint,
        box.topLeft.toPoint,
        Stroke(2, color)
      ),
    )
  }
}
