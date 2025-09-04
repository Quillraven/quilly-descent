package io.github.quillraven.descent.system

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import com.github.quillraven.fleks.collection.compareEntityBy
import io.github.quillraven.descent.GdxGame
import io.github.quillraven.descent.component.Graphic
import io.github.quillraven.descent.component.Transform
import ktx.graphics.use

class RenderSystem(
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject(),
    private val camera: OrthographicCamera = inject(),
) : IteratingSystem(
    family = family { all(Graphic, Transform) },
    comparator = compareEntityBy(Transform),
) {
    private val mapRenderer = OrthogonalTiledMapRenderer(null, GdxGame.UNIT_SCALE, batch)

    override fun onTick() {
        batch.color = Color.WHITE
        gameViewport.apply()

        mapRenderer.setView(camera)
        mapRenderer.render()

        batch.use(camera) {
            super.onTick()
        }
        batch.color = Color.WHITE
    }

    override fun onTickEntity(entity: Entity) {
        val (region, color) = entity[Graphic]
        val (position, size, rotation) = entity[Transform]
        val regionW = region.regionWidth.toFloat()
        val regionH = region.regionHeight.toFloat()
        val effectiveSize = Scaling.fill.apply(regionW, regionH, size.x, size.y)

        batch.color = color
        batch.draw(
            region, // region
            position.x, position.y, // position
            effectiveSize.x * 0.5f, effectiveSize.y * 0.5f, // origin
            effectiveSize.x, effectiveSize.y, // size
            1f, 1f, // scaling
            rotation // rotation in degrees
        )
    }

    fun setMap(map: TiledMap) {
        mapRenderer.map = map
    }

    override fun onDispose() {
        mapRenderer.dispose()
    }
}