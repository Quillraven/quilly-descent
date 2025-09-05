package io.github.quillraven.descent.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.World.Companion.inject
import io.github.quillraven.descent.component.Input
import io.github.quillraven.descent.component.Move
import io.github.quillraven.descent.component.Transform
import io.github.quillraven.descent.input.MoveCommand
import ktx.math.vec2

class InputSystem(
    private val viewport: Viewport = inject(),
) : IteratingSystem(family { all(Input) }) {

    private val tmpVec = vec2()

    override fun onTickEntity(entity: Entity) {
        val commands = entity[Input].commands
        if (commands.isEmpty()) return

        commands.forEach { cmd ->
            when {
                cmd is MoveCommand -> moveEntity(entity, cmd.targetX, cmd.targetY)
            }
        }
        commands.clear()
    }

    private fun moveEntity(entity: Entity, targetX: Int, targetY: Int) {
        tmpVec.set(targetX.toFloat(), targetY.toFloat())
        viewport.unproject(tmpVec)

        entity.getOrNull(Transform)?.let { transform ->
            val angle = MathUtils.atan2Deg360(tmpVec.y - transform.position.y, tmpVec.x - transform.position.x)

            entity.getOrNull(Move)?.let { move ->
                move.angle = angle
            }
        }
    }
}