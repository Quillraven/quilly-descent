package io.github.quillraven.descent.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Move(
    var speed: Float,
    var angle: Float = -1f,
) : Component<Move> {
    override fun type() = Move

    companion object : ComponentType<Move>()
}