package io.github.quillraven.descent.assets

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import io.github.quillraven.descent.procgen.RoomType
import ktx.math.vec2
import ktx.tiled.height
import ktx.tiled.width

enum class DoorType {
    LEFT, TOP, RIGHT, BOTTOM, ;

    companion object {
        fun ofDirection(x: Int, y: Int): DoorType {
            return when {
                x == 0 && y == 1 -> TOP
                x == 0 && y == -1 -> BOTTOM
                x == 1 && y == 0 -> RIGHT
                x == -1 && y == 0 -> LEFT
                else -> error("Invalid direction for door")
            }
        }
    }
}

class Room(
    val location: Vector2,
    val type: RoomType,
    val tiledMap: TiledMap,
    val doorLocations: Map<DoorType, Vector2>,
    val playerSpawn: Vector2,
    val size: Vector2 = vec2(tiledMap.width.toFloat(), tiledMap.height.toFloat())
) : Disposable {
    override fun dispose() {
        tiledMap.dispose()
    }

    companion object {
        val NO_ROOM = Room(vec2(-1f, -1f), RoomType.OPTIONAL, TiledMap(), emptyMap(), vec2(-1f, -1f), vec2(-1f - 1f))
    }
}