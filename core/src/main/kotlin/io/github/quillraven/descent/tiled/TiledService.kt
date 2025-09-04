package io.github.quillraven.descent.tiled

import com.badlogic.gdx.maps.tiled.TiledMap
import io.github.quillraven.descent.assets.Room

class TiledService {
    var activeMap: TiledMap = TiledMap()
    var onRoomChange: (room: Room, tiledMap: TiledMap) -> Unit = { _, _ -> }

    fun setActiveDungeonRoom(room: Room) {
        this.activeMap = room.tiledMap
        onRoomChange(room, activeMap)
    }
}