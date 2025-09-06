package io.github.quillraven.descent.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import io.github.quillraven.descent.assets.AtlasAsset
import io.github.quillraven.descent.assets.Room
import io.github.quillraven.descent.component.Graphic
import io.github.quillraven.descent.component.Input
import io.github.quillraven.descent.component.Move
import io.github.quillraven.descent.component.Tag
import io.github.quillraven.descent.component.Transform
import ktx.app.gdxError
import ktx.math.vec2

class PlayerSpawnSystem(
    atlas: TextureAtlas = inject(AtlasAsset.GAME_OBJECTS.name)
) : IteratingSystem(family { all(Tag.PLAYER, Transform) }) {

    private val playerRegion = atlas.findRegion("player/idle") ?: gdxError("Could not find region for player")

    override fun onTickEntity(entity: Entity) = Unit

    fun setRoom(room: Room) {
        if (family.isEmpty) {
            spawnPlayer()
        }

        family.forEach { player ->
            player[Transform].position.set(room.playerSpawn)
        }
    }

    private fun spawnPlayer() {
        world.entity {
            it += Transform(vec2(), vec2(1f, 1f))
            it += Graphic(playerRegion)
            it += Input()
            it += Move(4f)
            it += Tag.CAMERA_FOCUS
            it += Tag.PLAYER
        }
    }
}