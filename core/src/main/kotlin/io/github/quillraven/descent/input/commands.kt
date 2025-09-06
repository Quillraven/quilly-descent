package io.github.quillraven.descent.input

sealed interface Command

class MoveCommand(val directionDegrees: Float) : Command
