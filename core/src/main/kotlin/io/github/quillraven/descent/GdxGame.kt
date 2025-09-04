package io.github.quillraven.descent

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.quillraven.descent.assets.AssetService
import io.github.quillraven.descent.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import kotlin.math.min

class GdxGame : KtxGame<KtxScreen>() {
    val batch: Batch by lazy { SpriteBatch() }
    val gameViewport: Viewport = ExtendViewport(16f, 9f, 21f, 10f)
    val uiViewport: Viewport = ExtendViewport(854f, 480f)
    val stage: Stage by lazy { Stage(uiViewport, batch) }
    val assetService: AssetService by lazy { AssetService() }

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
    }

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiViewport.update(width, height, true)
        super.resize(width, height)
    }

    override fun render() {
        clearScreen(0f, 0f, 0f, 1f)
        currentScreen.render(min(Gdx.graphics.deltaTime, 1 / 30f))
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        stage.dispose()
        assetService.dispose()
    }

    companion object {
        const val UNIT_SCALE = 1 / 16f
    }
}