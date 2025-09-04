package io.github.quillraven.descent.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import io.github.quillraven.descent.assets.AtlasAsset
import io.github.quillraven.descent.assets.DoorType
import io.github.quillraven.descent.assets.Room
import io.github.quillraven.descent.component.Door
import io.github.quillraven.descent.component.Graphic
import io.github.quillraven.descent.component.Transform
import ktx.app.gdxError
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2
import ktx.tiled.forEachLayer

class DoorSystem(
    atlas: TextureAtlas = inject(AtlasAsset.GAME_OBJECTS.name)
) : IteratingSystem(family = family { all(Door) }) {
    private val doorUpRegion = atlas.findRegion(("doors/normal_top")) ?: gdxError("No 'normal_top' door region")
    private val doorLeftRegion = atlas.findRegion(("doors/normal_left")) ?: gdxError("No 'normal_left' door region")

    override fun onTickEntity(entity: Entity) {
    }

    fun setRoom(room: Room, tiledMap: TiledMap) {
        room.doorLocations.forEach { (doorType, location) ->
            val (cellX, cellY) = location
            tiledMap.forEachLayer<TiledMapTileLayer> { layer ->
                layer.setCell(cellX.toInt(), cellY.toInt(), null)
            }

            val rotation = when (doorType) {
                DoorType.LEFT -> 180f
                else -> 0f
            }
            val region = when (doorType) {
                DoorType.TOP, DoorType.BOTTOM -> doorUpRegion
                else -> doorLeftRegion
            }
            world.entity { e ->
                e += Transform(
                    position = vec2(cellX, cellY),
                    size = vec2(1f, 1f),
                    rotationDegrees = rotation,
                )
                e += Graphic(region)
                e += Door()
            }
        }
    }
}