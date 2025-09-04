package io.github.quillraven.descent

import com.badlogic.gdx.tools.texturepacker.TexturePacker

fun main() {
    val toPack = listOf(
        Triple("../assets_raw/objects", "../assets/graphics", "game_objects")
    )

    toPack.forEach { (inputDir, outputDir, atlasName) ->
        println("Packing $inputDir")
        TexturePacker.process(inputDir, outputDir, atlasName)
    }
    println("Packing done")
}
