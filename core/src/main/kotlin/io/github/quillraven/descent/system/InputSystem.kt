package io.github.quillraven.descent.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import io.github.quillraven.descent.component.Input
import io.github.quillraven.descent.component.Move
import io.github.quillraven.descent.input.MoveCommand

class InputSystem() : IteratingSystem(family { all(Input) }) {

    override fun onTickEntity(entity: Entity) {
        val (downCommands, upCommands) = entity[Input]
        if (downCommands.isEmpty() && upCommands.isEmpty()) return

        downCommands.forEach { cmd ->
            when {
                cmd is MoveCommand -> moveEntity(entity, cmd.directionDegrees)
            }
        }
        downCommands.clear()

        upCommands.forEach { cmd ->
            when {
                cmd is MoveCommand -> moveEntity(entity, -cmd.directionDegrees)
            }
        }
        upCommands.clear()

    }

    private fun moveEntity(entity: Entity, directionDegrees: Float) {
        entity.getOrNull(Move)?.let { move ->
            when (directionDegrees) {
                90f -> move.direction.y += 1f
                -90f -> move.direction.y -= 1f
                180f -> move.direction.x -= 1f
                -180f -> move.direction.x += 1f
                270f -> move.direction.y -= 1f
                -270f -> move.direction.y += 1f
                360f -> move.direction.x += 1f
                -360f -> move.direction.x -= 1f
            }
        }
    }
}