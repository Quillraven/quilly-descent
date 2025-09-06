package io.github.quillraven.descent.input

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import com.github.quillraven.fleks.Family
import io.github.quillraven.descent.component.Input

class KeyboardMouseInputProcessor(
    private val inputFamily: Family,
    private val keyMapping: MutableMap<Int, Command> = defaultMapping()
) : InputAdapter() {

    override fun keyDown(keycode: Int): Boolean {
        keyMapping[keycode]?.let { command ->
            inputFamily.forEach { entity ->
                entity[Input].downCommands += command
            }
            return true
        }

        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        keyMapping[keycode]?.let { command ->
            inputFamily.forEach { entity ->
                entity[Input].upCommands += command
            }
            return true
        }

        return false
    }

    companion object {
        fun defaultMapping(): MutableMap<Int, Command> = mutableMapOf(
            Keys.W to MoveCommand(90f),
            Keys.S to MoveCommand(270f),
            Keys.A to MoveCommand(180f),
            Keys.D to MoveCommand(360f),
        )
    }
}