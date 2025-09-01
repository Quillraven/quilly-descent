package io.github.quillraven.descent.procgen

enum class RoomType {
    START,
    END,
    MAIN,
    OPTIONAL;

    val isNotStartOrEnd: Boolean
        get() = this != START && this != END
}