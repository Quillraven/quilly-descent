package io.github.quillraven.descent.assets

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import io.github.quillraven.descent.procgen.Direction
import io.github.quillraven.descent.procgen.RoomType
import ktx.math.vec2
import ktx.tiled.height
import ktx.tiled.width

class Room(
    val indexX: Int,
    val indexY: Int,
    val type: RoomType,
    val tiledMap: TiledMap,
    val doorLocations: Map<Direction, Vector2>,
    val playerSpawn: Vector2,
    val size: Vector2 = vec2(tiledMap.width.toFloat(), tiledMap.height.toFloat())
) : Disposable {
    override fun dispose() {
        tiledMap.dispose()
    }

    companion object {
        val NO_ROOM = Room(-1, -1, RoomType.OPTIONAL, TiledMap(), emptyMap(), vec2(-1f, -1f), vec2(-1f - 1f))
    }
}