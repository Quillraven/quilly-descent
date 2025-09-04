package io.github.quillraven.descent.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.github.quillraven.descent.procgen.IntVector2
import io.github.quillraven.descent.procgen.generateDungeon
import ktx.app.gdxError
import ktx.assets.getLoader
import ktx.collections.GdxArray
import ktx.log.logger
import ktx.math.vec2
import ktx.tiled.layer
import ktx.tiled.shape
import ktx.tiled.tileHeight
import ktx.tiled.tileWidth

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
                    val tiledMapPath = mapPool.random().path()
                    val tiledMap = tiledMapLoader.load(tiledMapPath)
                    val doorLocations = updateDoorTiles(tiledMapPath, tiledMap, room.doorDirections)

                    Room(
                        location = vec2(roomX.toFloat(), roomY.toFloat()),
                        type = room.type,
                        tiledMap = tiledMap,
                        doorLocations = doorLocations
                    )
                }
            }
        )
    }

    private fun updateDoorTiles(
        tiledMapPath: String,
        tiledMap: TiledMap,
        doorDirections: Set<IntVector2>
    ): Map<DoorType, Vector2> {
        val doorLayer = tiledMap.layer("doors")
        return doorDirections.associate { direction ->
            val doorObjects = when (direction) {
                IntVector2.UP -> doorLayer.objects.filter { it.name == "up" }
                IntVector2.DOWN -> doorLayer.objects.filter { it.name == "down" }
                IntVector2.RIGHT -> doorLayer.objects.filter { it.name == "right" }
                IntVector2.LEFT -> doorLayer.objects.filter { it.name == "left" }
                else -> gdxError("Invalid direction for doors: $direction")
            }

            if (doorObjects.isEmpty()) {
                gdxError("Missing door area for direction $direction in map $tiledMapPath")
            }

            // set random spawn point within the door area
            val doorObject = doorObjects.random()
            val doorSpawnArea = doorObject.shape as Rectangle
            val spawnPointX = MathUtils.random(doorSpawnArea.x + 1f, doorSpawnArea.x + doorSpawnArea.width - 1f)
            val spawnPointY = MathUtils.random(doorSpawnArea.y + 1f, doorSpawnArea.y + doorSpawnArea.height - 1f)
            val cellIndexX = (spawnPointX / tiledMap.tileWidth).toInt()
            val cellIndexY = (spawnPointY / tiledMap.tileHeight).toInt()

            // return door location
            DoorType.ofDirection(direction.x, direction.y) to vec2(cellIndexX.toFloat(), cellIndexY.toFloat())
        }
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