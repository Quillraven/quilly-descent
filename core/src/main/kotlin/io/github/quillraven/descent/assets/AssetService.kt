package io.github.quillraven.descent.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.Disposable
import io.github.quillraven.descent.GdxGame

class AssetService(resolver: FileHandleResolver = InternalFileHandleResolver()) : Disposable {
    private val manager = AssetManager(resolver).apply {
        setLoader(TiledMap::class.java, TmxMapLoader(resolver))
        setLoader(Dungeon::class.java, DungeonLoader(resolver))
    }

    fun get(difficulty: DungeonDifficulty, tileset: DungeonTileset): Dungeon {
        val dungeonName = "Dungeon-${difficulty.name}"
        val parameter = DungeonLoaderParameters(difficulty, tileset, GdxGame.UNIT_SCALE)
        manager.load(dungeonName, Dungeon::class.java, parameter)
        manager.finishLoading()
        return manager.get(dungeonName, Dungeon::class.java)
    }

    fun <T> get(asset: Asset<T>): T = manager.get<T>(asset.descriptor)

    fun <T> queue(asset: Asset<T>) = manager.load(asset.descriptor)

    fun update(): Boolean = manager.update()

    override fun dispose() {
        manager.dispose()
    }
}