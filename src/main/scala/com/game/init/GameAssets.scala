package com.game.init

import indigo.{
  NonEmptyList,
  AssetName,
  AnimationKey,
  Animation,
  Frame,
  Cycle,
  Millis,
  AssetPath,
  AssetType,
  Rectangle,
  Graphic,
  Material,
  ShaderId,
  EntityShader,
  BlendShader
}
import indigo.shared.events.AssetEvent.AssetBatchLoaded

object GameAssets {
  val bulletTexture: AssetName = AssetName("bulletTexture")
  val characterTexture: AssetName = AssetName("characterTexture")
  val enemy1Texture: AssetName = AssetName("enemy1Texture")
  val fireSound1: AssetName = AssetName("fireSound1")

  val explosionTexture: AssetName = AssetName("explosion")

  val testVert: AssetName = AssetName("testVert")
  val testFrag: AssetName = AssetName("testFrag")
  val blendFrag: AssetName = AssetName("blendFrag")

  val explosionAnimationKey1: AnimationKey = AnimationKey("explosion1")
  val explosionAnimationKey2: AnimationKey = AnimationKey("explosion2")
  val explosionAnimationKey3: AnimationKey = AnimationKey("explosion3")
  val frameInterval: Int = 83
  val explosionCycle: Cycle = Cycle.create(
    "basic_explosion",
    NonEmptyList(
      Frame(Rectangle(0, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(96, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(192, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(288, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(384, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(480, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(576, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(672, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(768, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(864, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(960, 0, 96, 96), Millis(frameInterval)),
      Frame(Rectangle(1056, 0, 96, 96), Millis(frameInterval)),
    )
  )

  val explosionAnimationSet: Set[Animation] = Set(
    Animation.create(
      explosionAnimationKey1,
      explosionCycle
    ),
    Animation.create(
      explosionAnimationKey2,
      explosionCycle
    ),
    Animation.create(
      explosionAnimationKey3,
      explosionCycle
    )
  )

  val testShader: EntityShader.External =
    EntityShader
      .External(ShaderId("testShader"))
      //.withVertexProgram(testVert)
      .withFragmentProgram(testFrag)


  val blendShader: BlendShader.External =
    BlendShader
      .External(ShaderId("blendShader"))
      .withFragmentProgram(blendFrag)

  def characterMaterial: Material.ImageEffects = Material.ImageEffects(characterTexture)
  def bulletMaterial: Material.Bitmap = Material.Bitmap(bulletTexture)
  def enemy1Material: Material.Bitmap = Material.Bitmap(enemy1Texture)//.withShaderId(ShaderId("testShader"))
  def explosion1Material: Material.Bitmap = Material.Bitmap(explosionTexture)

  // Set reference / rotation point at the center
  val character: Graphic[Material.ImageEffects] = Graphic(0, 0, 64, 64, 2, characterMaterial).withRef(32, 32)
  val enemy1: Graphic[Material.Bitmap] = Graphic(0, 0, 64, 64, 2, enemy1Material).withRef(32, 32)
  val bullet: Graphic[Material.Bitmap] = Graphic(0, 0, 64, 64, 2, bulletMaterial).scaleBy(0.5, 0.5).withRef(32, 32)

  def assets(baseUrl: String): Set[AssetType] =
    Set(
      AssetType.Image(characterTexture, AssetPath(baseUrl + "assets/char1.png")),
      AssetType.Image(bulletTexture, AssetPath(baseUrl + "assets/bullet1.png")),
      AssetType.Image(enemy1Texture, AssetPath(baseUrl + "assets/enemy2.png")),
      AssetType.Audio(fireSound1, AssetPath(baseUrl + "assets/MiniShot2.wav")),
      AssetType.Image(explosionTexture, AssetPath(baseUrl + "assets/Explosion.png")),
      //AssetType.Text(testVert, AssetPath("assets/test.vert")),
      AssetType.Text(testFrag, AssetPath("assets/test.frag")),
      AssetType.Text(blendFrag, AssetPath("assets/scanline_blend.frag")),
    )
}