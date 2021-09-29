package com.game.model

import indigo.*
import com.game.model.player.PlayerModel
import com.game.scenes.level1.Level1Model

case class Model(
                  player: PlayerModel,
                  gameState: GameState,
                  controlScheme: GameControlScheme,
                  level1Model: Level1Model,
                )

object Model {
  def initial(viewport: GameViewport): Model = {
    Model(
        player = PlayerModel(viewport),
        gameState = GameState.Running.start,
        controlScheme = GameControlScheme.keysArrow,
        level1Model = Level1Model.initial(viewport),
    )
  }
}