package io.github.quillraven.descent.procgen

import kotlin.random.Random

fun generateDungeon(
    width: Int,
    height: Int,
    criticalPathLength: Int,
    startLocations: List<Pair<Int, Int>> = emptyList(),
    numBranches: Int = 0,
    branchLength: IntRange = 0..0,
): DungeonData {
    require(width > 0 && height > 0) { "dungeon size (width and height) must be greater than 0." }
    require(criticalPathLength > 0 && criticalPathLength < width * height) { "'criticalPathLength' must be greater than 0 and smaller than the dungeon size." }
    require(startLocations.none { (x, y) -> x !in 0..<width || y !in 0..<height }) { "'startLocations' must be within dungeon dimensions." }
    require(numBranches == 0 || (numBranches > 0 && branchLength.first > 0 && branchLength.last > 0)) { "'branchLength' must be greater than 0." }

    // if no start location is provided, then a random location is used
    val startLocation = when {
        startLocations.isEmpty() -> Random.nextInt(0, width) to Random.nextInt(0, height)
        else -> startLocations.random()
    }

    // generate the dungeon
    return DungeonData(width, height, startLocation.first, startLocation.second).apply {
        val branchCandidates = mutableListOf<Pair<Int, Int>>()
        if (!generateCriticalPath(startLocation, criticalPathLength, RoomType.MAIN, branchCandidates)) {
            error("Could not generate critical path")
        }
        generateBranches(numBranches, branchLength, branchCandidates)
        generateExtraDoors()
    }
}

private fun DungeonData.generateCriticalPath(
    from: Pair<Int, Int>,
    length: Int,
    roomType: RoomType,
    branchCandidates: MutableList<Pair<Int, Int>>
): Boolean {
    // path is created -> nothing more to do
    if (length == 0) return true

    val (widthRange, heightRange) = Pair(0..<this.width, 0..<this.height)
    var direction = Direction.random()
    val (fromX, fromY) = from
    // the last room (length=1) of the critical path (=roomType MAIN) is the END room
    val realRoomType = if (length == 1 && roomType == RoomType.MAIN) RoomType.END else roomType

    // try at most four times to add a room (one try per direction going clockwise if one direction fails)
    repeat(4) {
        val nextLocation = when (direction) {
            Direction.UP -> fromX to fromY + 1
            Direction.DOWN -> fromX to fromY - 1
            Direction.RIGHT -> fromX + 1 to fromY
            Direction.LEFT -> fromX - 1 to fromY
        }
        val (nextX, nextY) = nextLocation

        if (nextX !in widthRange || nextY !in heightRange || this.hasRoomAt(nextX, nextY)) {
            // out of dungeon boundaries or there is already a room at that location
            direction = direction.next()
            return@repeat
        }

        // found a potential location for a room -> add it to the dungeon ...
        this.addRoom(RoomData(index = length, indexX = nextX, indexY = nextY, type = realRoomType))
        if (realRoomType == RoomType.MAIN) {
            // the last room (=room type END) and branch rooms (=room type OPTIONAL) are not branch candidates
            branchCandidates += nextLocation
        }

        // ... add doors ...
        this.room(fromX, fromY).addDoor(direction)
        this.room(nextX, nextY).addDoor(direction.opposite())

        // ... and continue the generation of the next room
        if (generateCriticalPath(from = nextLocation, length - 1, roomType, branchCandidates)) {
            return true
        }

        // critical path creation failed at some point -> remove room and try a different direction
        this.removeRoom(nextX, nextY)
        branchCandidates -= nextLocation
        this.room(fromX, fromY).removeDoor(direction)
        direction = direction.next()
    }

    // failed to generate a room in any of the four directions
    return false
}

private fun DungeonData.generateBranches(
    numBranches: Int,
    branchLength: IntRange,
    branchCandidates: MutableList<Pair<Int, Int>>
) {
    var branchesCreated = 0
    while (branchesCreated < numBranches && branchCandidates.isNotEmpty()) {
        val candidateLocation = branchCandidates.random()
        val length = branchLength.random()
        if (this.generateCriticalPath(from = candidateLocation, length, RoomType.OPTIONAL, branchCandidates)) {
            // successfully created a path of the branch candidate
            ++branchesCreated
        } else {
            // couldn't create a path of the branch candidate because
            // e.g., the path is too long, or the candidate is surrounded by rooms
            // -> remove the candidate
            branchCandidates -= candidateLocation
        }
    }
}

private fun DungeonData.generateExtraDoors() {
    val allMainAndOptionalRooms = rooms
        .flatten()
        .filterNotNull()
        .filter { it.type.isNotStartOrEnd }

    allMainAndOptionalRooms.forEach { room ->
        // if a room already has doors in all directions, then ignore it
        if (room.numDoors == 4) return@forEach

        val extraRoomCandidates = this.surroundingRooms(room)
            // START and END rooms are not allowed for additional door connections
            .filterValues { it.type.isNotStartOrEnd }
            // only consider rooms to which there is no door yet
            .filterKeys { doorDirection -> !room.hasDoor(doorDirection) }

        // if there are no candidates -> go to the next room
        if (extraRoomCandidates.isEmpty()) return@forEach
        // don't add doors all the time (X% chance to ignore it)
        if (Random.nextFloat() < 0.75f) return@forEach

        val numDoors = (0..extraRoomCandidates.size).random()
        extraRoomCandidates.entries
            // pick random candidates
            .shuffled()
            .take(numDoors)
            .forEach { (direction, surroundingRoom) ->
                room.addDoor(direction)
                surroundingRoom.addDoor(direction.opposite())
            }
    }
}

fun main() {
    repeat(1000) {
        val dungeon = generateDungeon(
            width = 8,
            height = 8,
            criticalPathLength = 12,
            numBranches = 6,
            branchLength = 3..5
        )
        println(dungeon.toDebugString())
    }
}
