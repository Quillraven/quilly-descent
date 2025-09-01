package io.github.quillraven.descent.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.configureWorld
import io.github.quillraven.descent.GdxGame
import io.github.quillraven.descent.assets.AssetService
import io.github.quillraven.descent.assets.DungeonDifficulty
import io.github.quillraven.descent.assets.DungeonType
import io.github.quillraven.descent.system.DungeonDebugRenderSystem
import io.github.quillraven.descent.system.RenderSystem
import ktx.app.KtxScreen
import ktx.scene2d.actors
import ktx.scene2d.defaultStyle
import ktx.scene2d.label
import ktx.scene2d.table

class GameScreen(
    game: GdxGame,
    private val gameViewport: Viewport = game.gameViewport,
    private val batch: Batch = game.batch,
    private val stage: Stage = game.stage,
    private val assetService: AssetService = game.assetService,
) : KtxScreen {

    val world = initEcs()

    fun initEcs(): World = configureWorld {
        injectables {
            add(batch)
            add(gameViewport)
            add(assetService)
        }

        systems {
            add(RenderSystem())
            add(DungeonDebugRenderSystem())
        }
    }

    override fun show() {
        val skin = Skin().apply {
            add("default", Label.LabelStyle(BitmapFont(), Color.WHITE))
        }
        stage.actors {
            table(skin) {
                setFillParent(true)
                bottom()
                label("Have fun!", defaultStyle, skin) {
                    this.setAlignment(Align.center)
                    it.growX()
                }
            }
        }

        val dungeon = assetService.get(DungeonDifficulty.HARD, DungeonType.DEFAULT)
        world.system<DungeonDebugRenderSystem>().setDungeon(dungeon)
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