package io.github.quillraven.descent.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import io.github.quillraven.descent.procgen.Direction

data class Door(
    val direction: Direction,
    var locked: Boolean = true,
) : Component<Door> {
    override fun type() = Door

    companion object : ComponentType<Door>()
}