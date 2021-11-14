package com.game.model

import indigo.*
import indigo.IndigoLogger._
import com.game.model.player.{Direction, PlayerModel}

trait GameControlScheme {
  val inputMapping: InputMapping[PlayerControlState]

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

  def generateInputMapping(up: Key, down: Key, left: Key, right: Key, action: Key): InputMapping[PlayerControlState] = {
    InputMapping(
      // Triples
      Combo.withKeyInputs(up, left, action) -> PlayerControlState(Vector2(-1.0, -1.0), true),
      Combo.withKeyInputs(up, right, action) -> PlayerControlState(Vector2(1.0, 1.0), true),

      Combo.withKeyInputs(down, left, action) -> PlayerControlState(Vector2(-1.0, 1.0), true),
      Combo.withKeyInputs(down, right, action) -> PlayerControlState(Vector2(1.0, 1.0), true),

      // Doubles
      Combo.withKeyInputs(up, left) -> PlayerControlState(Vector2(-1.0, -1.0)),
      Combo.withKeyInputs(up, right) -> PlayerControlState(Vector2(1.0, -1.0)),

      Combo.withKeyInputs(down, left) -> PlayerControlState(Vector2(-1.0, 1.0)),
      Combo.withKeyInputs(down, right) -> PlayerControlState(Vector2(1.0, 1.0)),

      Combo.withKeyInputs(up, down) -> PlayerControlState(Vector2(0, 0)),
      Combo.withKeyInputs(left, right) -> PlayerControlState(Vector2(0, 0)),

      Combo.withKeyInputs(up, action) -> PlayerControlState(Vector2(0, -1.0), true),
      Combo.withKeyInputs(down, action) -> PlayerControlState(Vector2(0, 1.0), true),
      Combo.withKeyInputs(left, action) -> PlayerControlState(Vector2(-1.0, 0), true),
      Combo.withKeyInputs(right, action) -> PlayerControlState(Vector2(1.0, 0), true),

      // Singles
      Combo.withKeyInputs(up) -> PlayerControlState(Vector2(0, -1.0)),
      Combo.withKeyInputs(down) -> PlayerControlState(Vector2(0, 1.0)),
      Combo.withKeyInputs(left) -> PlayerControlState(Vector2(-1.0, 0)),
      Combo.withKeyInputs(right) -> PlayerControlState(Vector2(1.0, 0)),
      Combo.withKeyInputs(action) -> PlayerControlState(firing = true),
    )
  }

  final case class SchemeA(up: Key, down: Key, left: Key, right: Key, action: Key) extends GameControlScheme {
    override val inputMapping: InputMapping[PlayerControlState] = generateInputMapping(up, down, left, right, action)
  }

  final case class SchemeB(up: Key, down: Key, left: Key, right: Key, action: Key) extends GameControlScheme {
    override val inputMapping: InputMapping[PlayerControlState] = generateInputMapping(up, down, left, right, action)
  }
}

final case class PlayerControlState(direction: Vector2 = Vector2(0, 0), firing: Boolean = false)