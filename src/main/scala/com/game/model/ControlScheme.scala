package com.game.model

import indigo.*
import indigo.IndigoLogger._
import com.game.model.player.{Direction, PlayerModel}

trait GameControlScheme {
  def controlPlayer(keyboardEvent: KeyboardEvent, gameTime: GameTime, player: PlayerModel): PlayerModel

  def swap: GameControlScheme = {
    this match {
      case GameControlScheme.SchemeA(_, _, _, _, _) => GameControlScheme.keysWASD
      case GameControlScheme.SchemeB(_, _, _, _, _) => GameControlScheme.keysArrow
    }
  }
}

object GameControlScheme {
  val keysWASD: SchemeA = SchemeA(Key.KEY_W, Key.KEY_S, Key.KEY_A, Key.KEY_D, Key.SPACE)
  val keysArrow: SchemeB = SchemeB(Key.UP_ARROW, Key.DOWN_ARROW, Key.LEFT_ARROW, Key.RIGHT_ARROW, Key.SPACE)

  final case class SchemeA(up: Key, down: Key, left: Key, right: Key, action: Key) extends GameControlScheme {
    def mapInput(inputState: InputState): InputMapping[Vector2] = {
      InputMapping()
    }

    override def controlPlayer(keyboardEvent: KeyboardEvent, gameTime: GameTime, player: PlayerModel): PlayerModel = {
      keyboardEvent match {
        case KeyboardEvent.KeyDown(Key.KEY_W) => {
          player.turn(Direction.UP).move(true)
        }
        case KeyboardEvent.KeyDown(Key.KEY_S) => {
          player.turn(Direction.DOWN).move(true)
        }
        case KeyboardEvent.KeyDown(Key.KEY_A) => {
          player.turn(Direction.LEFT).move(true)
        }
        case KeyboardEvent.KeyDown(Key.KEY_D) => {
          player.turn(Direction.RIGHT).move(true)
        }
        case KeyboardEvent.KeyDown(Key.SPACE) => player.fire(true);
        case KeyboardEvent.KeyUp(Key.SPACE) => player.fire(false);
        case KeyboardEvent.KeyUp(_) => player.move(false)
        case _ => player
      }
    }
  }

  final case class SchemeB(up: Key, down: Key, left: Key, right: Key, action: Key) extends GameControlScheme {
    def mapInput(inputState: InputState, player: PlayerModel, gameTime: GameTime): InputMapping[PlayerModel] = {
      InputMapping(
        Combo.withKeyInputs(Key.UP_ARROW) -> (player.turn(Direction.UP).move(true)),
        Combo.withKeyInputs(Key.DOWN_ARROW) -> (player.turn(Direction.DOWN).move(true)),
        Combo.withKeyInputs(Key.LEFT_ARROW) -> (player.turn(Direction.LEFT).move(true)),
        Combo.withKeyInputs(Key.RIGHT_ARROW) -> (player.turn(Direction.RIGHT).move(true)),
        Combo.withKeyInputs(Key.SPACE) -> (player.fire(true)),
        Combo.withKeyInputs(Key.UP_ARROW, Key.SPACE) -> (player.turn(Direction.UP).move(true).fire(true)),
        Combo.withKeyInputs(Key.DOWN_ARROW, Key.SPACE) -> (player.turn(Direction.DOWN).move(true).fire(true)),
        Combo.withKeyInputs(Key.LEFT_ARROW, Key.SPACE) -> (player.turn(Direction.LEFT).move(true).fire(true)),
        Combo.withKeyInputs(Key.RIGHT_ARROW, Key.SPACE) -> (player.turn(Direction.RIGHT).move(true).fire(true)),
      )
    }

    override def controlPlayer(keyboardEvent: KeyboardEvent, gameTime: GameTime, player: PlayerModel): PlayerModel = {
      keyboardEvent match {
        case KeyboardEvent.KeyDown(Key.UP_ARROW) => player.turn(Direction.UP).move(true)
        case KeyboardEvent.KeyDown(Key.DOWN_ARROW) => player.turn(Direction.DOWN).move(true)
        case KeyboardEvent.KeyDown(Key.LEFT_ARROW) => player.turn(Direction.LEFT).move(true)
        case KeyboardEvent.KeyDown(Key.RIGHT_ARROW) => player.turn(Direction.RIGHT).move(true)
        case KeyboardEvent.KeyDown(Key.SPACE) => player.fire(true);
        case KeyboardEvent.KeyUp(Key.SPACE) => player.fire(false);
        case KeyboardEvent.KeyUp(Key.UP_ARROW | Key.DOWN_ARROW | Key.LEFT_ARROW | Key.RIGHT_ARROW) => player.move(false)
        case _ => player
      }
    }
  }
}
