package com.game.scenes.level1

import com.game.model.enemy.EnemyModel
import com.game.model.other.{Debris, Nitro, ObstacleModel, PickupModel}
import indigo.*
import indigoextras.geometry.{BoundingBox, Vertex}

final case class Level1GameMap(
                          width: Int,
                          Height: Int,
                          tileSize: Int,
                          enemies: List[EnemyModel],
                          obstacles: List[ObstacleModel],
                          pickups: List[PickupModel]
                        )

object Level1GameMap {
  def apply(viewport: GameViewport): Level1GameMap = {
    val pickups = List(
      Nitro(location = Vertex(4, 10))
    )
    val obstacles = List(
      Debris(Vertex(30, 150))
    )
    // Witdth and Height measured in number of 64 x 64 tiles
    Level1GameMap(12, 144, 64, List(), obstacles, pickups)
  }
}