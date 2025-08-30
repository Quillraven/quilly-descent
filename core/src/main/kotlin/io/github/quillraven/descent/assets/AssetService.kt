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
    }

    fun get(filePath: String): TiledMap {
        manager.load(filePath, TiledMap::class.java)
        manager.finishLoading()
        return manager.get(filePath, TiledMap::class.java)
    }

    override fun dispose() {
        manager.dispose()
    }
}