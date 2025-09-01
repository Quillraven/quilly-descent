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

    fun roomAt(x: Int, y: Int): ProcRoom? {
        return rooms.getOrNull(x)?.getOrNull(y)
    }

    fun surroundingRooms(room: ProcRoom): Map<IntVector2, ProcRoom> {
        val result = mutableMapOf<IntVector2, ProcRoom>()
        roomAt(room.location.x, room.location.y + 1)?.let { result[IntVector2.UP] = it }
        roomAt(room.location.x, room.location.y - 1)?.let { result[IntVector2.DOWN] = it }
        roomAt(room.location.x + 1, room.location.y)?.let { result[IntVector2.RIGHT] = it }
        roomAt(room.location.x - 1, room.location.y)?.let { result[IntVector2.LEFT] = it }
        return result
    }

    override fun toString(): String {
        return buildString {
            // Each room is 3x3, so we need to iterate through 3 times the height
            for (gridY in (height * 3 - 1) downTo 0) {
                for (gridX in 0 until (width * 3)) {
                    // Determine which room we're in and which position within that room
                    val roomX = gridX / 3
                    val roomY = gridY / 3
                    val posX = gridX % 3
                    val posY = gridY % 3

                    val room: ProcRoom? = roomAt(roomX, roomY)
                    if (room == null) {
                        append(" ")
                        continue
                    }

                    when {
                        // Center position (1,1) - show room type
                        posX == 1 && posY == 1 -> append(room.type.name.first())

                        // Corners (0,0), (0,2), (2,0), (2,2) - always dots
                        (posX == 0 || posX == 2) && (posY == 0 || posY == 2) -> append(" ")

                        // Top (1,2) - check for UP door
                        posX == 1 && posY == 2 -> {
                            if (room.hasDoor(IntVector2.UP)) append("+") else append(" ")
                        }

                        // Bottom (1,0) - check for DOWN door
                        posX == 1 && posY == 0 -> {
                            if (room.hasDoor(IntVector2.DOWN)) append("+") else append(" ")
                        }

                        // Left (0,1) - check for LEFT door
                        posX == 0 && posY == 1 -> {
                            if (room.hasDoor(IntVector2.LEFT)) append("+") else append(" ")
                        }

                        // Right (2,1) - check for RIGHT door
                        posX == 2 && posY == 1 -> {
                            if (room.hasDoor(IntVector2.RIGHT)) append("+") else append(" ")
                        }

                        else -> append(" ")
                    }
                }
                appendLine()
            }
        }

    }
}