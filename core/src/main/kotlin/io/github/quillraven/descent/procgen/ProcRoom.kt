package io.github.quillraven.descent.procgen

data class ProcRoom(
    val index: Int,
    val location: IntVector2,
    val type: RoomType,
)