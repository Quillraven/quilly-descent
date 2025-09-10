package io.github.quillraven.descent.procgen

enum class RoomType {
    START,
    END,
    MAIN, // critical path
    OPTIONAL; // branch path

    val isNotStartOrEnd: Boolean
        get() = this != START && this != END
}