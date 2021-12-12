import indigo.*
import indigo.scenes.*
import indigoextras.subsystems.FPSCounter

import com.game.init.{GameAssets, StartupData, ViewConfig}
import com.game.model.{Model, ViewModel}
import com.game.scenes.start.StartScene
import com.game.scenes.level1.Level1Scene
import com.game.scenes.gameover.GameOverScene

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object Main extends IndigoGame[ViewConfig, StartupData, Model, ViewModel] {
  def initialScene(bootData: ViewConfig): Option[SceneName] =
    Option(StartScene.name)

  def scenes(bootData: ViewConfig): NonEmptyList[Scene[StartupData, Model, ViewModel]] =
    NonEmptyList(StartScene, Level1Scene, GameOverScene)

  val eventFilters: EventFilters =
    EventFilters.Restricted

  def boot(flags: Map[String, String]): Outcome[BootResult[ViewConfig]] =
    Outcome {
      val viewConfig: ViewConfig =
        ViewConfig.default

      val assetPath: String =
        flags.getOrElse("baseUrl", "")

      val config =
        GameConfig(
          viewport = viewConfig.viewport,
          frameRate = 60,
          clearColor = RGBA.Black,
          magnification = viewConfig.magnificationLevel
        )

      BootResult(config, viewConfig)
        .withAssets(GameAssets.assets(assetPath))
        //.withFonts(GameAssets.fontInfo)
        .withSubSystems(
          Set(FPSCounter(Point(5, 5), 60, Option(BindingKey("fps"))))
        )
    }

  def initialModel(startupData: StartupData): Outcome[Model] =
      Outcome(Model.initial(startupData.viewConfig.viewport))

  def initialViewModel(startupData: StartupData, model: Model): Outcome[ViewModel] =
    Outcome(ViewModel.initial(startupData, model))

  def setup(viewConfig: ViewConfig, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[StartupData]] =
    StartupData.initialize(viewConfig, assetCollection)

  def updateModel(context: FrameContext[StartupData], model: Model): GlobalEvent => Outcome[Model] = {
    case GameReset =>
      Outcome(Model.initial(context.startUpData.viewConfig.viewport))

    case _ =>
      Outcome(model)
  }

  def updateViewModel(
                       context: FrameContext[StartupData],
                       model: Model,
                       viewModel: ViewModel
                     ): GlobalEvent => Outcome[ViewModel] =
    _ => Outcome(viewModel)

  def present(
               context: FrameContext[StartupData],
               model: Model,
               viewModel: ViewModel
             ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment.empty
        .addLayer(Layer(BindingKey("game")))
        .addLayer(Layer(BindingKey("ui")))
        .addLayer(Layer(BindingKey("fps")))
    )
}

case object GameReset extends GlobalEvent
