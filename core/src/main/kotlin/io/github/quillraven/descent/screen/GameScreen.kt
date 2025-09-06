package io.github.quillraven.descent.screen

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.configureWorld
import io.github.quillraven.descent.GdxGame
import io.github.quillraven.descent.assets.AssetService
import io.github.quillraven.descent.assets.AtlasAsset
import io.github.quillraven.descent.assets.DungeonDifficulty
import io.github.quillraven.descent.assets.DungeonType
import io.github.quillraven.descent.component.Input
import io.github.quillraven.descent.input.KeyboardMouseInputProcessor
import io.github.quillraven.descent.system.CameraSystem
import io.github.quillraven.descent.system.DebugSystem
import io.github.quillraven.descent.system.DoorSystem
import io.github.quillraven.descent.system.InputSystem
import io.github.quillraven.descent.system.MoveSystem
import io.github.quillraven.descent.system.PlayerSpawnSystem
import io.github.quillraven.descent.system.RenderSystem
import io.github.quillraven.descent.tiled.TiledService
import ktx.app.KtxScreen

class GameScreen(
    game: GdxGame,
    private val gameViewport: Viewport = game.gameViewport,
    private val batch: Batch = game.batch,
    private val stage: Stage = game.stage,
    private val assetService: AssetService = game.assetService,
    private val inputProcessor: InputMultiplexer = game.inputProcessor,
) : KtxScreen {

    private val tiledService = TiledService()
    private val world = initEcs()

    fun initEcs(): World = configureWorld {
        injectables {
            add(batch)
            add(gameViewport)
            add(gameViewport.camera as OrthographicCamera)
            add(assetService)
            add(AtlasAsset.GAME_OBJECTS.name, assetService.get(AtlasAsset.GAME_OBJECTS))
            add(tiledService)
        }

        systems {
            add(InputSystem())
            add(PlayerSpawnSystem())
            add(MoveSystem())
            add(DoorSystem())
            add(CameraSystem())
            add(RenderSystem())
            add(DebugSystem())
//            add(DungeonDebugRenderSystem())
        }
    }

    override fun show() {
        inputProcessor.clear()
        inputProcessor.setProcessors(KeyboardMouseInputProcessor(world.family { all(Input) }))

        val dungeon = assetService.get(DungeonDifficulty.HARD, DungeonType.DEFAULT)
//        world.system<DungeonDebugRenderSystem>().setDungeon(dungeon)
        tiledService.onRoomChange = { room, tiledMap ->
            world.system<DoorSystem>().setRoom(room, tiledMap)
            world.system<CameraSystem>().resetCamera()
            world.system<RenderSystem>().setMap(tiledMap)
            world.system<PlayerSpawnSystem>().setRoom(room)
        }
        tiledService.setActiveDungeonRoom(dungeon, dungeon.startRoom)
    }

    override fun render(delta: Float) {
        world.update(delta)

        stage.viewport.apply()
        stage.batch.color = Color.WHITE
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        world.dispose()
    }
}