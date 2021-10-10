package com.game.events

import indigo.*
import indigoextras.geometry.Vertex

trait ViewModelEvent extends GlobalEvent

case class EnemyDeathEvent(
                            location: Point,
                          ) extends ViewModelEvent