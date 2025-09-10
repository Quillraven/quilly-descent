package io.github.quillraven.descent.procgen

class DungeonData(
    val width: Int,
    val height: Int,
    startX: Int,
    startY: Int,
) {
    val rooms: Array<Array<RoomData?>> = Array(width) { x ->
        Array(height) { y ->
            if (x == startX && y == startY) {
                RoomData(index = 0, indexX = x, indexY = y, type = RoomType.START)
            } else {
                null
            }
        }
    }

    fun addRoom(room: RoomData) {
        rooms[room.indexX][room.indexY] = room
    }

    fun removeRoom(x: Int, y: Int) {
        rooms[x][y] = null
    }

    fun hasRoomAt(x: Int, y: Int): Boolean {
        return rooms.getOrNull(x)?.getOrNull(y) != null
    }

    fun roomOrNull(x: Int, y: Int): RoomData? {
        return rooms.getOrNull(x)?.getOrNull(y)
    }

    fun room(x: Int, y: Int): RoomData {
        return roomOrNull(x, y) ?: error("There is no room at $x,$y")
    }

    fun surroundingRooms(room: RoomData): Map<Direction, RoomData> {
        val result = mutableMapOf<Direction, RoomData>()
        roomOrNull(room.indexX, room.indexY + 1)?.let { result[Direction.UP] = it }
        roomOrNull(room.indexX, room.indexY - 1)?.let { result[Direction.DOWN] = it }
        roomOrNull(room.indexX + 1, room.indexY)?.let { result[Direction.RIGHT] = it }
        roomOrNull(room.indexX - 1, room.indexY)?.let { result[Direction.LEFT] = it }
        return result
    }

    fun toDebugString(): String {
        return buildString {
            // Each room is represented as a 3x3 grid, so we need to iterate through 3 times the height
            for (gridY in (height * 3 - 1) downTo 0) {
                for (gridX in 0 until (width * 3)) {
                    // Determine which room we're in and which position within that room
                    val roomX = gridX / 3
                    val roomY = gridY / 3
                    val posX = gridX % 3
                    val posY = gridY % 3

                    val room: RoomData? = roomOrNull(roomX, roomY)
                    if (room == null) {
                        append(" ")
                        continue
                    }

                    when {
                        // Center position (1,1) - show room type
                        posX == 1 && posY == 1 -> append(room.type.name.first())

                        // Corners (0,0), (0,2), (2,0), (2,2)
                        (posX == 0 || posX == 2) && (posY == 0 || posY == 2) -> append(" ")

                        // Top (1,2) - check for a door in the UP direction
                        posX == 1 && posY == 2 -> {
                            if (room.hasDoor(Direction.UP)) append(".") else append(" ")
                        }

                        // Bottom (1,0) - check for a door in the DOWN direction
                        posX == 1 -> {
                            if (room.hasDoor(Direction.DOWN)) append(".") else append(" ")
                        }

                        // Left (0,1) - check for a door in the LEFT direction
                        posX == 0 -> {
                            if (room.hasDoor(Direction.LEFT)) append(".") else append(" ")
                        }

                        // Right (2,1) - check for a door in the RIGHT direction
                        else -> {
                            if (room.hasDoor(Direction.RIGHT)) append(".") else append(" ")
                        }
                    }
                }
                appendLine()
            }
        }
    }

    override fun toString(): String {
        return "DungeonData(width=$width, height=$height, numRooms=${rooms.sumOf { it.count { room -> room != null } }})"
    }
}