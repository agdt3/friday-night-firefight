package com.game.model.other

import indigoextras.geometry.Vertex

case class Projectile(location: Vertex, direction: Vertex, speed: Float = 10.0f, damage: Int = 2)