package io.github.quillraven.descent.tiled

import com.badlogic.gdx.maps.tiled.TiledMap
import io.github.quillraven.descent.assets.Dungeon
import io.github.quillraven.descent.assets.Dungeon.Companion.NO_DUNGEON
import io.github.quillraven.descent.assets.Room
import io.github.quillraven.descent.assets.Room.Companion.NO_ROOM

class DungeonService {
    var activeRoom: Room = NO_ROOM
        private set
    var activeDungeon: Dungeon = NO_DUNGEON
        private set

    var onRoomChange: (room: Room, tiledMap: TiledMap) -> Unit = { _, _ -> }

    fun setDungeon(dungeon: Dungeon) {
        this.activeDungeon = dungeon
        setRoom(activeDungeon.startRoom)
    }

    fun setRoom(room: Room) {
        this.activeRoom = room
        onRoomChange(room, room.tiledMap)
    }
}