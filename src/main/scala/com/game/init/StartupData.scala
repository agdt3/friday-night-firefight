package com.game.init

import indigo._
import indigo.package$package.GameViewport

object StartupData {
  def initialize(viewConfig: ViewConfig): Outcome[Startup[StartupData]] = {
    val startupData = createStartupData(viewConfig)
    Outcome(
      Startup
        .Success(startupData)
        .addAnimations(startupData.staticAssets.explosionAnimationSet.toList)
        .addShaders(List(startupData.staticAssets.testShader, startupData.staticAssets.blendShader))
    )
    // Outcome(Startup.Success(createStartupData(viewConfig)))
  }

  def createStartupData(viewConfig: ViewConfig): StartupData = {
    StartupData(
      viewConfig = viewConfig,
      staticAssets = StaticAssets(
        character = GameAssets.character,
        bullet = GameAssets.bullet,
        enemy1 = GameAssets.enemy1,
        explosionMaterial = GameAssets.explosion1Material,
        explosionAnimationSet = GameAssets.explosionAnimationSet,
        testShader = GameAssets.testShader,
        blendShader = GameAssets.blendShader
      ),
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
                               blendShader: EntityShader
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