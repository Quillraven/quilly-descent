package io.github.quillraven.descent.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import io.github.quillraven.descent.input.Command

data class Input(
    val downCommands: MutableList<Command> = mutableListOf(),
    val upCommands: MutableList<Command> = mutableListOf(),
) : Component<Input> {
    override fun type() = Input

    companion object : ComponentType<Input>()
}