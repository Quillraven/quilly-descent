package io.github.quillraven.descent.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import io.github.quillraven.descent.assets.DoorType

data class Door(
    val type: DoorType,
    var locked: Boolean = true,
) : Component<Door> {
    override fun type() = Door

    companion object : ComponentType<Door>()
}