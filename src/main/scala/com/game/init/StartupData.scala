package com.game.init

import indigo._
import indigo.json.Json
import indigo.package$package.GameViewport
import indigo.shared.IndigoLogger.consoleLog

object StartupData {
  def initialize(
      viewConfig: ViewConfig,
      assetCollection: AssetCollection
  ): Outcome[Startup[StartupData]] = {
    val maybeTiledMap = for {
      j <- assetCollection.findTextDataByName(GameAssets.level1MapData)
      t <- Json.tiledMapFromJson(j)
      g <- t.toGroup(GameAssets.level1MapImage)
    } yield g

    maybeTiledMap match
      case Some(tiledMap: Group) => {
        val startupData =
          createStartupData(viewConfig, tiledMap.moveBy(0, -7000))
        Outcome(
          Startup
            .Success(startupData)
            .addAnimations(
              startupData.staticAssets.explosionAnimationSet.toList
            )
            .addShaders(
              List(
                startupData.staticAssets.testShader,
                startupData.staticAssets.blendShader
              )
            )
        )
      }
      case None =>
        Outcome(
          Startup.Failure("Could not generate a TiledMap!")
        )
  }

  def createStartupData(
      viewConfig: ViewConfig,
      tiledMap: Group
  ): StartupData = {
    StartupData(
      viewConfig = viewConfig,
      staticAssets = StaticAssets(
        character = GameAssets.character,
        bullet = GameAssets.bullet,
        enemy1 = GameAssets.enemy1,
        explosionMaterial = GameAssets.explosion1Material,
        explosionAnimationSet = GameAssets.explosionAnimationSet,
        testShader = GameAssets.testShader,
        blendShader = GameAssets.blendShader,
        tiledMap = tiledMap
      )
    )
  }
}

final case class StartupData(viewConfig: ViewConfig, staticAssets: StaticAssets)

final case class StaticAssets(
    character: Graphic[Material.ImageEffects],
    bullet: Graphic[Material.Bitmap],
    enemy1: Graphic[Material.Bitmap],
    explosionMaterial: Material.Bitmap,
    explosionAnimationSet: Set[Animation],
    testShader: EntityShader,
    blendShader: EntityShader,
    tiledMap: Group
)

final case class ViewConfig(magnificationLevel: Int, viewport: GameViewport)

object ViewConfig {
  val default: ViewConfig = {
    ViewConfig(
      magnificationLevel = 1,
      viewport = GameViewport(1024, 768)
    )
  }
}
