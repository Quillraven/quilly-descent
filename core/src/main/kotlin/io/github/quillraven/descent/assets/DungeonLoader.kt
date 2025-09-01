package io.github.quillraven.descent.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import io.github.quillraven.descent.procgen.generateDungeon
import ktx.assets.getLoader
import ktx.collections.GdxArray
import ktx.log.logger
import ktx.math.vec2

class DungeonLoader(private val resolver: FileHandleResolver) :
    AsynchronousAssetLoader<Dungeon, DungeonLoaderParameters>(resolver) {

    override fun loadAsync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: DungeonLoaderParameters?
    ) {
    }

    override fun loadSync(
        manager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: DungeonLoaderParameters?
    ): Dungeon {
        require(parameter != null) { "DungeonLoader requires DungeonParameters with dungeonData." }

        val tiledMapLoader: TmxMapLoader? = manager.getLoader<TiledMap>() as TmxMapLoader?
        require(tiledMapLoader != null) { "DungeonLoader requires a loader for TiledMap assets." }

        // 1) generate some random dungeon data using procedural generation
        log.debug { "Loading dungeon of difficulty ${parameter.difficulty.name}" }
        val dungeonData = generateDungeon(
            dimension = parameter.difficulty.dimension,
            criticalPathLength = parameter.difficulty.criticalPathLength.random(),
            numBranches = parameter.difficulty.numBranches.random(),
            branchLength = parameter.difficulty.branchLength,
        )
        log.debug { "\n$dungeonData" }

        // 2) convert raw data to real game dungeon data using random Tiled maps for the dungeon
        val mapsFolder = resolver.resolve("assets/maps")
        val mapPool = mapsFolder.list { it.name.startsWith("${parameter.type.fileNameId}-") }
        require(mapPool.isNotEmpty()) { "There are no maps for dungeon type '${parameter.type}'" }

        return Dungeon(
            rooms = Array(dungeonData.width) { x ->
                Array(dungeonData.height) { y ->
                    val room = dungeonData.rooms[x][y] ?: return@Array null
                    val (roomX, roomY) = room.location

                    Room(
                        location = vec2(roomX.toFloat(), roomY.toFloat()),
                        type = room.type,
                        tiledMap = tiledMapLoader.load(mapPool.random().path()),
                        doorLocations = room.doorDirections.map { vec2(it.x.toFloat(), it.y.toFloat()) }
                    )
                }
            }
        )
    }

    override fun getDependencies(
        fileName: String,
        file: FileHandle,
        parameter: DungeonLoaderParameters?
    ): GdxArray<AssetDescriptor<*>>? {
        return null
    }

    companion object {
        private val log = logger<DungeonLoader>()
    }
}

data class DungeonLoaderParameters(
    val difficulty: DungeonDifficulty,
    val type: DungeonType,
) : AssetLoaderParameters<Dungeon>()