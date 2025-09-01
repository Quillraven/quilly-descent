package io.github.quillraven.descent.procgen

import kotlin.math.max
import kotlin.random.Random

data class IntVector2(val x: Int, val y: Int) {
    operator fun plus(other: IntVector2): IntVector2 {
        return IntVector2(x + other.x, y + other.y)
    }

    companion object {
        val UP = IntVector2(0, 1)
        val RIGHT = IntVector2(1, 0)
        val DOWN = IntVector2(0, -1)
        val LEFT = IntVector2(-1, 0)

        fun randomDirection(): IntVector2 = when (Random.nextInt(0, 4)) {
            0 -> UP
            1 -> RIGHT
            2 -> DOWN
            3 -> LEFT
            else -> error("Impossible random direction")
        }

        fun oppositeDirection(direction: IntVector2): IntVector2 = when (direction) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
            else -> error("Impossible opposite direction")
        }
    }
}

fun generateDungeon(
    dimension: IntVector2,
    criticalPathLength: Int,
    startLocations: List<IntVector2> = emptyList(),
    numBranches: Int = 0,
    branchLength: IntRange = 0..0,
): ProcDungeon {
    require(dimension.x > 0 && dimension.y > 0) { "'dimension' must be greater than 0." }
    require(criticalPathLength > 0 && criticalPathLength < dimension.x * dimension.y) { "'criticalPathLength' must be greater than 0 and smaller than the dungeon size." }
    require(startLocations.none { it.x !in 0..<dimension.x || it.y !in 0..<dimension.y }) { "'startLocations' must be within dungeon dimensions." }
    require(numBranches == 0 || (numBranches > 0 && branchLength.first > 0 && branchLength.last > 0)) { "'branchLength' must be greater than 0." }

    // if no start location is provided, then a random location is used
    val startLocation: IntVector2 = when {
        startLocations.isEmpty() -> IntVector2(
            Random.nextInt(0, dimension.x),
            Random.nextInt(0, dimension.y),
        )

        else -> startLocations.random()
    }

    // generate the dungeon
    return ProcDungeon(dimension, startLocation).apply {
        val branchCandidates = mutableListOf<IntVector2>()
        if (!generateCriticalPath(this, startLocation, criticalPathLength, RoomType.MAIN, branchCandidates)) {
            error("Could not generate critical path")
        }
        generateBranches(this, numBranches, branchLength, branchCandidates)
        generateDoors(this)
    }
}

fun generateDoors(dungeon: ProcDungeon) {
    val allRooms = dungeon.rooms.flatten().filterNotNull()
    generateStartRoomDoors(dungeon, allRooms)
    generateEndRoomDoor(dungeon, allRooms)

    val remainingRooms = allRooms.filter { it.type.isNotStartOrEnd }
    remainingRooms.forEach { room ->
        // if a room already has more than one door, then it was already processed
        if (room.numDoors > 1) return@forEach

        // ignore START and END room because they are handled separately before
        val surroundingRooms = dungeon.surroundingRooms(room)
            .filterValues { it.type.isNotStartOrEnd }
            .filterKeys { doorDirection -> doorDirection !in room }
        if (surroundingRooms.isEmpty()) return@forEach
        var numDoors = (1..surroundingRooms.size).random()
        val minNumDoors = when {
            room.type == RoomType.OPTIONAL && room.isLastPathElement -> 1 - room.numDoors
            else -> 2 - room.numDoors
        }
        numDoors = max(minNumDoors, numDoors)

        surroundingRooms.entries.shuffled().take(numDoors).forEach { (direction, surroundingRoom) ->
            room.addDoor(direction)
            surroundingRoom.addDoor(IntVector2.oppositeDirection(direction))
        }
    }
}

private fun generateStartRoomDoors(
    dungeon: ProcDungeon,
    allRooms: List<ProcRoom>
) {
    val startRoom = allRooms.single { it.type == RoomType.START }
    val surroundingRooms = dungeon.surroundingRooms(startRoom)
    val mainRooms = surroundingRooms.filterValues { it.type == RoomType.MAIN }
    if (mainRooms.isEmpty()) {
        // should only be possible if a critical path is not long enough and END is directly next to START
        val endRoom = surroundingRooms.filterValues { it.type == RoomType.END }
        require(endRoom.size == 1) { "There is no END room next to the START room" }
        val doorDirection = endRoom.keys.single()
        startRoom.addDoor(doorDirection)
        endRoom.values.single().addDoor(IntVector2.oppositeDirection(doorDirection))
        return
    }

    val numDoors = (1..mainRooms.size).random()
    mainRooms.entries.shuffled().take(numDoors).forEach { (direction, room) ->
        startRoom.addDoor(direction)
        room.addDoor(IntVector2.oppositeDirection(direction))
    }
}

private fun generateEndRoomDoor(
    dungeon: ProcDungeon,
    allRooms: List<ProcRoom>
) {
    val endRoom = allRooms.single { it.type == RoomType.END }
    // if END room already has a door, then it was the special case handled in generateStartRoomDoors,
    // which means that there is only a short critical path directly from START to END
    // -> nothing to do
    if (endRoom.hasDoors()) return

    val surroundingRooms = dungeon.surroundingRooms(endRoom)
    val mainRooms = surroundingRooms.filterValues { it.type == RoomType.MAIN }
    require(mainRooms.isNotEmpty()) { "There is no MAIN room next to the END room" }

    // only one door to the final room of a level
    val (direction, room) = mainRooms.entries.shuffled().random()
    endRoom.addDoor(direction)
    room.addDoor(IntVector2.oppositeDirection(direction))
}

fun generateCriticalPath(
    dungeon: ProcDungeon,
    from: IntVector2,
    length: Int,
    roomType: RoomType,
    branchCandidates: MutableList<IntVector2>
): Boolean {
    if (length == 0) return true

    val (dungeonWidth, dungeonHeight) = Pair(0..<dungeon.width, 0..<dungeon.height)
    var direction = IntVector2.randomDirection()
    val realRoomType = if (length == 1 && roomType == RoomType.MAIN) RoomType.END else roomType

    // try at most four times to add a room (one try per direction going clockwise if one direction fails)
    repeat(4) {
        val nextLocation = from + direction
        if (nextLocation.x !in dungeonWidth || nextLocation.y !in dungeonHeight) {
            // out of dungeon boundaries
            direction = IntVector2(-direction.y, direction.x)
            return@repeat
        }
        if (dungeon.hasRoomAt(nextLocation)) {
            // there is already a room at that location
            direction = IntVector2(-direction.y, direction.x)
            return@repeat
        }

        // found a potential location for a room -> add it to the dungeon ...
        dungeon += ProcRoom(index = length, location = nextLocation, type = realRoomType)
        if (realRoomType == RoomType.MAIN) {
            // the last room (=end of dungeon) and other branch rooms are not branch candidates
            branchCandidates += nextLocation
        }

        // ... and continue the generation of the next room
        if (generateCriticalPath(dungeon, from = nextLocation, length - 1, roomType, branchCandidates)) {
            return true
        }

        // critical path creation failed at some point -> remove room and try a different direction (go clockwise)
        dungeon.clearRoom(nextLocation)
        branchCandidates -= nextLocation
        direction = IntVector2(-direction.y, direction.x)
    }

    // failed to generate a room in any of the four directions
    return false
}

fun generateBranches(
    dungeon: ProcDungeon,
    numBranches: Int,
    branchLength: IntRange,
    branchCandidates: MutableList<IntVector2>
) {
    var branchesCreated = 0
    while (branchesCreated < numBranches && branchCandidates.isNotEmpty()) {
        val candidate = branchCandidates.random()
        val length = branchLength.random()
        if (generateCriticalPath(dungeon, from = candidate, length, RoomType.OPTIONAL, branchCandidates)) {
            // successfully created a path of the branch candidate
            ++branchesCreated
        } else {
            // couldn't create a path of the branch candidate because
            // e.g., the path is too long, or the candidate is surrounded by rooms
            // -> remove the candidate
            branchCandidates -= candidate
        }
    }
}

fun main() {
    repeat(1000) {
        val dungeon = generateDungeon(
            dimension = IntVector2(8, 8),
            criticalPathLength = 12,
            numBranches = 6,
            branchLength = 3..5
        )
        println("$dungeon")
    }
}
