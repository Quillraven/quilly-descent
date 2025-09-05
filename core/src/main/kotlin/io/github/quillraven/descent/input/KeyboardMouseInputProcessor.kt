package io.github.quillraven.descent.input

import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.InputAdapter
import com.github.quillraven.fleks.Family
import io.github.quillraven.descent.component.Input

class KeyboardMouseInputProcessor(
    private val inputFamily: Family,
) : InputAdapter() {

    override fun touchDown(
        screenX: Int,
        screenY: Int,
        pointer: Int,
        button: Int
    ): Boolean {
        if (Buttons.LEFT != button) return false

        inputFamily.forEach { entity ->
            entity[Input].commands += MoveCommand(screenX, screenY)
        }
        return true
    }

}