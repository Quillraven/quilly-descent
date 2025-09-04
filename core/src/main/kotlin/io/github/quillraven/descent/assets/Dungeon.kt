package io.github.quillraven.descent.assets

import com.badlogic.gdx.utils.Disposable
import io.github.quillraven.descent.procgen.RoomType

class Dungeon(
    val rooms: Array<Array<Room?>>,
) : Disposable {
    val startRoom = rooms.flatten().single { it?.type == RoomType.START } ?: error("There is no start room")
    var activeRoom = startRoom

    override fun dispose() {
        rooms.flatten().filterNotNull().forEach { it.dispose() }
    }
}