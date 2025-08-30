package io.github.quillraven.descent.assets

import com.badlogic.gdx.utils.Disposable

class Dungeon(
    val rooms: Array<Array<Room?>>,
) : Disposable {
    override fun dispose() {
        rooms.flatten().filterNotNull().forEach { it.dispose() }
    }
}