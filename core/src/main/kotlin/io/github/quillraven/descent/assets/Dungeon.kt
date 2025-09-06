package io.github.quillraven.descent.assets

import com.badlogic.gdx.utils.Disposable
import io.github.quillraven.descent.assets.Room.Companion.NO_ROOM
import io.github.quillraven.descent.procgen.RoomType
import ktx.app.gdxError

class Dungeon(
    val rooms: Array<Array<Room?>>,
    val startRoom: Room = rooms.flatten().singleOrNull { it?.type == RoomType.START } ?: error("There is no start room")
) : Disposable {

    fun roomAt(x: Int, y: Int): Room {
        return rooms[x][y] ?: gdxError("There is no room at $x,$y")
    }

    override fun dispose() {
        rooms.flatten().filterNotNull().forEach { it.dispose() }
    }

    companion object {
        val NO_DUNGEON = Dungeon(emptyArray(), NO_ROOM)
    }
}