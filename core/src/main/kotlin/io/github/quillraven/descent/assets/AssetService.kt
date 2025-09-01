package io.github.quillraven.descent.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.Disposable

class AssetService(resolver: FileHandleResolver = InternalFileHandleResolver()) : Disposable {
    private val manager = AssetManager(resolver).apply {
        setLoader(TiledMap::class.java, TmxMapLoader(resolver))
        setLoader(Dungeon::class.java, DungeonLoader(resolver))
    }

    fun get(difficulty: DungeonDifficulty, type: DungeonType): Dungeon {
        val dungeonName = "Dungeon-${difficulty.name}"
        manager.load(dungeonName, Dungeon::class.java, DungeonLoaderParameters(difficulty, type))
        manager.finishLoading()
        return manager.get(dungeonName, Dungeon::class.java)
    }

    override fun dispose() {
        manager.dispose()
    }
}