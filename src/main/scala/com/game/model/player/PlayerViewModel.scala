package com.game.model.player

case class PlayerViewModel(
                          previousDirection: Direction,
                          invincibilityFramesLeft: Int
                          ) {
  def update(playerModel: PlayerModel): PlayerViewModel = {
    this.copy(previousDirection = playerModel.direction)
  }
}

object PlayerViewModel {
  def initial(): PlayerViewModel = {
    PlayerViewModel(
      Direction.UP,
      0
    )
  }
}