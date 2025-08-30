package io.github.quillraven.descent.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import io.github.quillraven.descent.assets.Dungeon
import io.github.quillraven.descent.assets.Room
import io.github.quillraven.descent.procgen.RoomType
import ktx.math.component1
import ktx.math.component2
import ktx.math.times

class DungeonDebugRenderSystem(
    private val batch: Batch = inject(),
    private val gameViewport: Viewport = inject(),
    private val camera: OrthographicCamera = gameViewport.camera as OrthographicCamera,
) : IntervalSystem() {

    private val mapRenderer = OrthogonalTiledMapRenderer(null, 1f / 16f, batch)
    private var rooms: List<Room> = emptyList()

    override fun onTick() {
        if (rooms.isEmpty()) return

        batch.color = Color.WHITE
        gameViewport.apply()

        val (origCamX, origCamY) = camera.position
        rooms.forEach { room ->
            val (roomX, roomY) = room.location * room.size

            if (!camera.frustum.boundsInFrustum(roomX, roomY, 0f, room.size.x, room.size.y, 0f)) {
                return@forEach
            }

            camera.position.x -= roomX
            camera.position.y -= roomY
            camera.update()

            mapRenderer.map = room.tiledMap
            mapRenderer.setView(camera)
            when (room.type) {
                RoomType.START -> batch.color = Color.GREEN
                RoomType.END -> batch.color = Color.RED
                RoomType.OPTIONAL -> batch.color = Color.YELLOW
                else -> batch.color = Color.WHITE
            }
            mapRenderer.render()
            camera.position.set(origCamX, origCamY, 0f)
        }

        when {
            Gdx.input.isKeyJustPressed(Keys.W) -> camera.position.y += 4.5f
            Gdx.input.isKeyJustPressed(Keys.S) -> camera.position.y -= 4.5f
            Gdx.input.isKeyJustPressed(Keys.A) -> camera.position.x -= 8f
            Gdx.input.isKeyJustPressed(Keys.D) -> camera.position.x += 8f
            Gdx.input.isKeyJustPressed(Keys.R) -> camera.zoom += 1f
            Gdx.input.isKeyJustPressed(Keys.F) -> camera.zoom -= 1f
        }
    }

    fun setDungeon(dungeon: Dungeon) {
        rooms = dungeon.rooms.flatten().filterNotNull().toList()
    }

    override fun onDispose() {
        mapRenderer.dispose()
    }
}