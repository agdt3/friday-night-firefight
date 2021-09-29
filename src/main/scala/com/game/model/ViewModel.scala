package com.game.model

import com.game.init.StartupData
import com.game.model.GameModel
import com.game.model.player.PlayerViewModel
import com.game.scenes.level1.Level1ViewModel

final case class ViewModel(
                          level1ViewModel: Level1ViewModel,
                          playerViewModel: PlayerViewModel
                          )

object ViewModel {
  def initial(startupData: StartupData, model: Model): ViewModel = {
    val playerViewModel = PlayerViewModel.initial()
    ViewModel(
      level1ViewModel = Level1ViewModel.initial(playerViewModel),
      playerViewModel
    )
  }
}