package io.github.quillraven.descent.procgen

class ProcDungeon(
    val dimension: IntVector2,
    startLocation: IntVector2,
) {
    val width: Int
        get() = dimension.x
    val height: Int
        get() = dimension.y
    val rooms: Array<Array<ProcRoom?>> = Array(dimension.x) { x ->
        Array(dimension.y) { y ->
            if (x == startLocation.x && y == startLocation.y) {
                ProcRoom(index = 0, location = IntVector2(x, y), type = RoomType.START)
            } else {
                null
            }
        }
    }

    operator fun plusAssign(room: ProcRoom) {
        rooms[room.location.x][room.location.y] = room
    }

    fun clearRoom(location: IntVector2) {
        rooms[location.x][location.y] = null
    }

    fun hasRoomAt(location: IntVector2): Boolean {
        return rooms.getOrNull(location.x)?.getOrNull(location.y) != null
    }

    override fun toString(): String {
        return buildString {
            val maxIdxSize = "${width * height}".length

            for (y in height - 1 downTo 0) {
                for (x in 0 until width) {
                    val room: ProcRoom? = rooms.getOrNull(x)?.getOrNull(y)
                    if (room == null) {
                        append("[${" ".repeat(maxIdxSize + 1)}]")
                        continue
                    }

                    append("[${room.type.name.first()}${room.index.toString().padEnd(maxIdxSize)}]")
                }
                appendLine()
            }
        }
    }
}