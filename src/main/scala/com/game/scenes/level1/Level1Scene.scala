package com.game.scenes.level1

import indigo.*
import indigo.scenes.*

import com.game.init.StartupData
import com.game.model.{Model, ViewModel}
import com.game.scenes.level1.Level1View
import com.game.events.ViewModelEvent

object Level1Scene extends Scene[StartupData, Model, ViewModel] {
  type SceneModel     = Level1Model
  type SceneViewModel = Level1ViewModel

  val name: SceneName =
    SceneName("level1")

  val modelLens: Lens[Model, Level1Model] =
    Lens(
      (gameModel: Model) => gameModel.level1Model.copy(
        controlScheme = gameModel.controlScheme,
        player = gameModel.player
      ),
      (gameModel, newLeve1Model) => gameModel.copy(
        level1Model = newLeve1Model,
        player = newLeve1Model.player
      )
    )

  val viewModelLens: Lens[ViewModel, Level1ViewModel] =
    Lens(
      (viewModel: ViewModel) => viewModel.level1ViewModel.copy(
        playerViewModel = viewModel.playerViewModel
      ),
      (viewModel: ViewModel, newLevel1ViewModel) => viewModel.copy(
        level1ViewModel = newLevel1ViewModel,
        playerViewModel = newLevel1ViewModel.playerViewModel
      )
    )

  val eventFilters: EventFilters =
    EventFilters.Permissive.withViewModelFilter(event => {
        event match
          case event : ViewModelEvent => Some(event)
          case event : ViewEvent => Some(event)
          case event : FrameTick => Some(event)
          case _ => None
      }
    )

  val subSystems: Set[SubSystem] =
    Set()

  def updateModel(
                   context: FrameContext[StartupData],
                   levelModel: Level1Model
                 ): GlobalEvent => Outcome[Level1Model] = {
    levelModel.update(context.gameTime, context.inputState)
  }

  def updateViewModel(
                       context: FrameContext[StartupData],
                       gameModel: Level1Model,
                       viewModel: Level1ViewModel
                     ): GlobalEvent => Outcome[Level1ViewModel] = {
    viewModel.update(context.gameTime, gameModel)
  }

  def present(
               context: FrameContext[StartupData],
               gameModel: Level1Model,
               viewModel: Level1ViewModel
             ): Outcome[SceneUpdateFragment] = {
    Level1View.update(context.startUpData.viewConfig, gameModel, viewModel, context.startUpData.staticAssets)
  }
}
