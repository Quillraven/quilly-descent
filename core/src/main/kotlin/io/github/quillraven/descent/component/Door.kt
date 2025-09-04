package io.github.quillraven.descent.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Door(
    var locked: Boolean = true,
) : Component<Door> {
    override fun type() = Door

    companion object : ComponentType<Door>()
}