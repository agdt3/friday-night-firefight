package com.game.model

import indigo.*
import indigo.scenes.*
import indigo.IndigoLogger.*
import com.game.init.StartupData
import com.game.model.GameState
import com.game.model.player.{Direction, PlayerModel}
import com.game.scenes.level1.Level1Model

final case class GameModel(
                            player: PlayerModel,
                            gameState: GameState,
                            controlScheme: GameControlScheme,
                            level1Model: Level1Model,
                            lastUpdated: Seconds
                          ) {
  def update(gameTime: GameTime, inputState: InputState): GlobalEvent => Outcome[GameModel] = {
    // FrameTick is always the last event, which is why we update the GameModel's lastUpdated
    case FrameTick => {
      GameModel.updateRunning(
        gameTime,
        inputState,
        this.copy(lastUpdated = gameTime.running),
        GameState.Running(gameTime.running)
      )(FrameTick)
    }

    // Don't update lastUpdated since this event is being processed in this Frame
    case e => {
      GameModel.updateRunning(gameTime, inputState, this, GameState.Running(lastUpdated))(e)
    }
  }
}

object GameModel {
  def initialModel(viewport: GameViewport): GameModel =
    GameModel(
      player = PlayerModel(viewport),
      gameState = GameState.Running.start,
      controlScheme = GameControlScheme.keysArrow,
      level1Model = Level1Model.initial(viewport),
      lastUpdated = Seconds.zero
    )

  def updateRunning(
                    gameTime: GameTime,
                    inputState: InputState,
                    state: GameModel,
                    runningDetails: GameState.Running
                   ): GlobalEvent => Outcome[GameModel] = {
        /*
    case FrameTick => {
      val (newPlayer, globalEvents) = state.player.update(state.gameMap, gameTime)
      Outcome(state.copy(player = newPlayer)).addGlobalEvents(globalEvents)
    }
        */

    case event: KeyboardEvent => {
      Outcome(
        state.copy(
          player = state.controlScheme.controlPlayer(event, gameTime, state.player)
        )
      )
    }

    case _ => Outcome(state)
  }
}

