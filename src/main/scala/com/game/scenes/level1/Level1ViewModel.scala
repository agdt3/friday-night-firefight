package com.game.scenes.level1

import indigo.{FrameTick, GlobalEvent, Outcome, GameTime, Point, AnimationKey, Seconds}
import indigoextras.geometry.Vertex

import com.game.model.player.PlayerViewModel
import com.game.events.{EnemyDeathEvent, ViewModelEvent}

case class Level1ViewModel(
                          playerViewModel: PlayerViewModel,
                          cameraPosition: Point,
                          explosions: List[Explosion] = Nil,
                          explosionIncrement: Int = 0
                          ) {
  def update(gameTime: GameTime, level1Model: Level1Model): GlobalEvent => Outcome[Level1ViewModel] = {
    Level1ViewModel.update(gameTime, level1Model, this)
  }
}

object Level1ViewModel {
  def initial(playerViewModel: PlayerViewModel): Level1ViewModel = Level1ViewModel(playerViewModel, Point(0,0))

  def update(gameTime: GameTime, levelModel: Level1Model, viewModel: Level1ViewModel): GlobalEvent => Outcome[Level1ViewModel] = {
    case FrameTick => {
      val updatedExplosions = viewModel.explosions collect {
        case explosion if explosion.timeElapsed + gameTime.delta < explosion.duration => {
          explosion.copy(timeElapsed = explosion.timeElapsed + gameTime.delta)
        }
      }

      Outcome(viewModel.copy(explosions = updatedExplosions))
      /*
      Outcome(
        viewModel.copy(cameraPosition = viewModel.cameraPosition.moveBy(0, -0.5))
      )
      */
    }
    case event: EnemyDeathEvent => {
      Outcome(
        viewModel.copy(
          explosionIncrement = viewModel.explosionIncrement + 1,
          explosions = Explosion(
            event.location,
            animationKey = AnimationKey(s"explosion${viewModel.explosionIncrement + 1}")
          ) :: viewModel.explosions
        )
      )
    }
    case _ => Outcome(viewModel)
  }
}

case class Explosion(
                      location: Point,
                      duration: Seconds = Seconds(1),
                      timeElapsed: Seconds = Seconds(0),
                      animationKey: AnimationKey,
                    )