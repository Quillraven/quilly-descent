package io.github.quillraven.descent.component

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Transform(
    val position: Vector2,
    val size: Vector2,
    val rotationDegrees: Float = 0f,
    val z: Int = 0,
) : Component<Transform>, Comparable<Transform> {
    override fun type() = Transform

    override fun compareTo(other: Transform): Int = when {
        z < other.z -> -1
        z > other.z -> 1
        position.y < other.position.y -> 1
        position.y > other.position.y -> -1
        position.x < other.position.x -> 1
        position.x > other.position.x -> -1
        else -> 0
    }

    companion object : ComponentType<Transform>()
}