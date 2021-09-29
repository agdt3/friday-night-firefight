package com.game.scenes.start

import indigo.*
import indigo.scenes.*

import com.game.init.StartupData
import com.game.model.{Model, ViewModel}
import com.game.scenes.level1.Level1Scene


object StartScene extends Scene[StartupData, Model, ViewModel] {
  type SceneModel     = Unit
  type SceneViewModel = Unit

  val name: SceneName =
    SceneName("start")

  val modelLens: Lens[Model, Unit] =
    Lens.unit

  val viewModelLens: Lens[ViewModel, Unit] =
    Lens.unit

  val eventFilters: EventFilters =
    EventFilters.Restricted
      .withViewModelFilter(_ => None)

  val subSystems: Set[SubSystem] =
    Set()

  def updateModel(
                   context: FrameContext[StartupData],
                   gameModel: Unit
                 ): GlobalEvent => Outcome[Unit] = {
    case KeyboardEvent.KeyDown(_) =>
      Outcome(gameModel).addGlobalEvents(SceneEvent.JumpTo(Level1Scene.name))

    case _ => Outcome(gameModel)
  }

  def updateViewModel(
                       context: FrameContext[StartupData],
                       gameModel: Unit,
                       viewModel: Unit
                     ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  def present(
               context: FrameContext[StartupData],
               gameModel: Unit,
               viewModel: Unit
             ): Outcome[SceneUpdateFragment] = {
    val fragment = SceneUpdateFragment
      .empty
      .addLayer(
        Layer(
          BindingKey("ui"),
          drawTitle(context.startUpData.viewConfig.viewport)
        )
      )
    Outcome(fragment)
  }

  def drawTitle(viewport: GameViewport): List[SceneNode] = {
    val horizontal = viewport.horizontalMiddle
    val vertical = viewport.verticalMiddle - viewport.verticalMiddle / 2
    List(
      TextBox("Friday Night Firefight", viewport.width / 2, 200)
        .withFontFamily(FontFamily.monospace)
        .withColor(RGBA.White)
        .withFontSize(Pixels(32))
        .withStroke(TextStroke(RGBA.Red, Pixels(1)))
        .withPosition(Point(horizontal - 250, vertical))
        .alignCenter,
      TextBox("Press Any Key To Start")
        .withFontFamily(FontFamily.monospace)
        .withColor(RGBA.White)
        .withFontSize(Pixels(16))
        .withStroke(TextStroke(RGBA.Red, Pixels(1)))
        .withPosition(Point(horizontal - 150, vertical + 50))
        .alignCenter
    )
  }
}