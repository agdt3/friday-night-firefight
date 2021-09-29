package com.game.init

import indigo._
import indigo.package$package.GameViewport

object StartupData {
  def initialize(viewConfig: ViewConfig): Outcome[Startup[StartupData]] = {
    Outcome(Startup.Success(createStartupData(viewConfig)))
  }

  def createStartupData(viewConfig: ViewConfig): StartupData = {
    StartupData(
      viewConfig = viewConfig,
      staticAssets = StaticAssets(
        character = GameAssets.character,
        bullet = GameAssets.bullet,
        enemy1 = GameAssets.enemy1,
      ),
    )
  }
}

final case class StartupData(viewConfig: ViewConfig, staticAssets: StaticAssets)

final case class StaticAssets(
                               character: Graphic[Material.ImageEffects],
                               bullet: Graphic[Material.Bitmap],
                               enemy1: Graphic[Material.Bitmap],
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