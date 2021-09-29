package com.game.scenes.level1

import indigo.*
import indigo.shared.IndigoLogger.consoleLog
import com.game.model.player.PlayerViewModel

case class Level1ViewModel(
                          playerViewModel: PlayerViewModel,
                          cameraPosition: Point
                          ) {

  def update(gameTime: GameTime, level1Model: Level1Model): GlobalEvent => Outcome[Level1ViewModel] = {
    Level1ViewModel.update(gameTime, level1Model, this)
  }
}

object Level1ViewModel {
  def initial(playerViewModel: PlayerViewModel): Level1ViewModel = Level1ViewModel(playerViewModel, Point(0,0))

  def update(gameTime: GameTime, levelModel: Level1Model, viewModel: Level1ViewModel): GlobalEvent => Outcome[Level1ViewModel] = {
    case FrameTick => {
      Outcome(viewModel)
      /*
      Outcome(
        viewModel.copy(cameraPosition = viewModel.cameraPosition.moveBy(0, -0.5))
      )
      */
    }
    case _ => Outcome(viewModel)
  }
}