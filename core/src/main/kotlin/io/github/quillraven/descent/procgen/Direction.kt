package io.github.quillraven.descent.procgen

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    /**
     * Returns the next direction counterclockwise.
     */
    fun next(): Direction = when (this) {
        RIGHT -> UP
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
    }

    /**
     * Returns the opposite direction.
     */
    fun opposite(): Direction = when (this) {
        RIGHT -> LEFT
        UP -> DOWN
        LEFT -> RIGHT
        DOWN -> UP
    }

    companion object {
        /**
         * Returns a random direction.
         */
        fun random() = entries.random()
    }
}