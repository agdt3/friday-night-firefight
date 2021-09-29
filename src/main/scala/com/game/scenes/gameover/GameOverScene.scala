package com.game.scenes.gameover

import com.game.init.StartupData
import com.game.model.player.PlayerModel
import com.game.model.{Model, ViewModel}
import com.game.scenes.start.StartScene
import indigo.*
import indigo.scenes.*

object GameOverScene extends Scene[StartupData, Model, ViewModel] {
  type SceneModel     = Model
  type SceneViewModel = Unit

  val name: SceneName =
    SceneName("gameover")

  val modelLens: Lens[Model, Model] =
    Lens.keepLatest

  val viewModelLens: Lens[ViewModel, Unit] =
    Lens.unit

  val eventFilters: EventFilters =
    EventFilters.Restricted
      .withViewModelFilter(_ => None)

  val subSystems: Set[SubSystem] =
    Set()

  def updateModel(context: FrameContext[StartupData], model: Model): GlobalEvent => Outcome[Model] = {
    case KeyboardEvent.KeyDown(_) =>
      Outcome(Model.initial(context.startUpData.viewConfig.viewport))
        .addGlobalEvents(SceneEvent.JumpTo(StartScene.name))

    case _ => Outcome(model)
  }

  def updateViewModel(
                       context: FrameContext[StartupData],
                       gameModel: Model,
                       viewModel: Unit
                     ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  def present(
               context: FrameContext[StartupData],
               gameModel: Model,
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
    val horizontal = viewport.horizontalMiddle - 150
    val vertical = viewport.verticalMiddle - viewport.verticalMiddle / 2
    List(
      TextBox("Game Over")
        .withFontFamily(FontFamily.monospace)
        .withColor(RGBA.White)
        .withFontSize(Pixels(32))
        .withStroke(TextStroke(RGBA.Red, Pixels(1)))
        .withPosition(Point(horizontal, vertical))
        .alignCenter,
      TextBox("Press Any Key To Try Again")
        .withFontFamily(FontFamily.monospace)
        .withColor(RGBA.White)
        .withFontSize(Pixels(16))
        .withStroke(TextStroke(RGBA.Red, Pixels(1)))
        .withPosition(Point(horizontal, vertical + 50))
        .alignCenter
    )
  }
}
