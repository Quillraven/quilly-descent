package io.github.quillraven.descent.system

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject

class RenderSystem(
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject(),
    private val camera: OrthographicCamera = gameViewport.camera as OrthographicCamera,
) : IntervalSystem() {
    private val mapRenderer = OrthogonalTiledMapRenderer(null, 1f / 16f, batch)

    override fun onTick() {
        batch.color = Color.WHITE
        gameViewport.apply()

        if (mapRenderer.map != null) {
            mapRenderer.setView(camera)
            mapRenderer.render()
        }
    }

    fun setMap(map: TiledMap) {
        mapRenderer.map = map
    }

    override fun onDispose() {
        mapRenderer.dispose()
    }
}