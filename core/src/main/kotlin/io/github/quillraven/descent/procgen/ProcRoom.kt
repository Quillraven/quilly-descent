package io.github.quillraven.descent.procgen

data class ProcRoom(
    val index: Int,
    val location: IntVector2,
    val type: RoomType,
) {
    private val doors = mutableSetOf<IntVector2>()

    val numDoors: Int
        get() = doors.size

    val isLastPathElement: Boolean
        get() = index == 1

    val doorDirections: Set<IntVector2>
        get() = doors

    operator fun contains(door: IntVector2) = door in doors

    fun addDoor(door: IntVector2) {
        doors.add(door)
    }

    fun hasDoor(direction: IntVector2): Boolean {
        return direction in doors
    }

    fun hasDoors(): Boolean = doors.isNotEmpty()
}