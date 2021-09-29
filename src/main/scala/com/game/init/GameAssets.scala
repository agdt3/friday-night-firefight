package com.game.init

import indigo.*
import indigo.shared.events.AssetEvent.AssetBatchLoaded

object GameAssets {
  val bulletTexture: AssetName = AssetName("bulletTexture")
  val characterTexture: AssetName = AssetName("characterTexture")
  val enemy1Texture: AssetName = AssetName("enemy1Texture")
  val fireSound1: AssetName = AssetName("fireSound1")

  def characterMaterial: Material.ImageEffects = Material.ImageEffects(characterTexture)
  def bulletMaterial: Material.Bitmap = Material.Bitmap(bulletTexture)
  def enemy1Material: Material.Bitmap = Material.Bitmap(enemy1Texture)

  // Set reference / rotation point at the center
  val character: Graphic[Material.ImageEffects] = Graphic(0, 0, 64, 64, 2, characterMaterial).withRef(32, 32)
  val enemy1: Graphic[Material.Bitmap] = Graphic(0, 0, 64, 64, 2, enemy1Material).withRef(32, 32)
  val bullet: Graphic[Material.Bitmap] = Graphic(0, 0, 64, 64, 2, bulletMaterial).scaleBy(0.5, 0.5).withRef(32, 32)

  def assets(baseUrl: String): Set[AssetType] =
    Set(
      AssetType.Image(characterTexture, AssetPath(baseUrl + "assets/char1.png")),
      AssetType.Image(bulletTexture, AssetPath(baseUrl + "assets/bullet1.png")),
      AssetType.Image(enemy1Texture, AssetPath(baseUrl + "assets/enemy2.png")),
      AssetType.Audio(fireSound1, AssetPath(baseUrl + "assets/MiniShot2.wav"))
    )
}