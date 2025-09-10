package io.github.quillraven.descent.procgen

data class RoomData(
    val index: Int,
    val indexX: Int,
    val indexY: Int,
    val type: RoomType,
) {
    // in which directions are doors to leave/enter the room?
    private val doors = mutableSetOf<Direction>()

    val numDoors: Int
        get() = doors.size

    val doorDirections: Set<Direction>
        get() = doors

    fun addDoor(door: Direction) {
        doors.add(door)
    }

    fun removeDoor(door: Direction) {
        doors.remove(door)
    }

    fun hasDoor(direction: Direction): Boolean {
        return direction in doors
    }
}