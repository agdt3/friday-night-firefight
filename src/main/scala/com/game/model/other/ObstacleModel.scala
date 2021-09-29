package com.game.model.other

import indigoextras.geometry.Vertex

trait ObstacleModel(val damage: Int)

class Barrier(location: Vertex) extends ObstacleModel(4)

class Debris(location: Vertex) extends  ObstacleModel(2)