package io.github.quillraven.descent.screen

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
import io.github.quillraven.descent.system.CameraSystem
import io.github.quillraven.descent.system.DoorSystem
import io.github.quillraven.descent.system.RenderSystem
import io.github.quillraven.descent.tiled.TiledService
import ktx.app.KtxScreen

class GameScreen(
    game: GdxGame,
    private val gameViewport: Viewport = game.gameViewport,
    private val batch: Batch = game.batch,
    private val stage: Stage = game.stage,
    private val assetService: AssetService = game.assetService,
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
        }

        systems {
            add(DoorSystem())
            add(CameraSystem())
            add(RenderSystem())
//            add(DungeonDebugRenderSystem())
        }
    }

    override fun show() {
        val dungeon = assetService.get(DungeonDifficulty.HARD, DungeonType.DEFAULT)
//        world.system<DungeonDebugRenderSystem>().setDungeon(dungeon)
        tiledService.onRoomChange = { room, tiledMap ->
            world.system<DoorSystem>().setRoom(room, tiledMap)
            world.system<CameraSystem>().resetCamera()
            world.system<RenderSystem>().setMap(tiledMap)
        }
        tiledService.setActiveDungeonRoom(dungeon.activeRoom)
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