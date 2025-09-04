package io.github.quillraven.descent.screen

import io.github.quillraven.descent.GdxGame
import io.github.quillraven.descent.assets.AssetService
import io.github.quillraven.descent.assets.AtlasAsset
import ktx.app.KtxScreen

class LoadingScreen(
    private val game: GdxGame,
    private val assetService: AssetService = game.assetService,
) : KtxScreen {

    override fun show() {
        AtlasAsset.entries.forEach { assetService.queue(it) }
    }

    override fun render(delta: Float) {
        if (assetService.update()) {
            loadingDone()
        }
    }

    private fun loadingDone() {
        game.removeScreen<LoadingScreen>()
        dispose()
        game.addScreen(GameScreen(game))
        game.setScreen<GameScreen>()
    }
}