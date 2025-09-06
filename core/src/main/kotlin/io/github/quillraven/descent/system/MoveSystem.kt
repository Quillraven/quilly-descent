package io.github.quillraven.descent.system

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import io.github.quillraven.descent.component.Move
import io.github.quillraven.descent.component.Transform

class MoveSystem : IteratingSystem(family { all(Move, Transform) }) {
    override fun onTickEntity(entity: Entity) {
        val (speed, direction) = entity[Move]
        if (direction.isZero) return

        val moveX = speed * direction.x * deltaTime
        val moveY = speed * direction.y * deltaTime
        entity[Transform].position.add(moveX, moveY)
    }
}