package io.github.quillraven.descent.assets

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import io.github.quillraven.descent.procgen.RoomType
import ktx.math.vec2
import ktx.tiled.height
import ktx.tiled.width

class Room(
    val location: Vector2,
    val type: RoomType,
    val tiledMap: TiledMap,
    val size: Vector2 = vec2(tiledMap.width.toFloat(), tiledMap.height.toFloat())
) : Disposable {
    override fun dispose() {
        tiledMap.dispose()
    }
}