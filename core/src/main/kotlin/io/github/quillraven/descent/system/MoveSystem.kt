package io.github.quillraven.descent.system

import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import io.github.quillraven.descent.component.Move
import io.github.quillraven.descent.component.Transform

class MoveSystem : IteratingSystem(family { all(Move, Transform) }) {
    override fun onTickEntity(entity: Entity) {
        val (speed, angle) = entity[Move]
        if (angle == -1f) return

        val moveX = speed * MathUtils.cosDeg(angle) * deltaTime
        val moveY = speed * MathUtils.sinDeg(angle) * deltaTime
        entity[Transform].position.add(moveX, moveY)
    }
}