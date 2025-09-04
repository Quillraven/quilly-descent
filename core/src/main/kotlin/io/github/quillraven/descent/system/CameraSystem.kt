package io.github.quillraven.descent.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import io.github.quillraven.descent.component.Tag

class CameraSystem(
    private val viewport: Viewport = inject(),
    private val camera: OrthographicCamera = inject(),
) : IteratingSystem(family { all(Tag.CAMERA_FOCUS) }) {

    override fun onTickEntity(entity: Entity) {
    }

    fun resetCamera() {
        camera.position.set(viewport.worldWidth / 2, viewport.worldHeight / 2, 0f)
        camera.update()
    }
}