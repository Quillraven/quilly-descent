package io.github.quillraven.descent

import com.badlogic.gdx.tools.texturepacker.TexturePacker

fun main() {
    val toPack = listOf(
        Triple("assets_raw/tilesets/default", "assets/maps", "default")
    )

    toPack.forEach { (inputDir, outputDir, atlasName) ->
        println("Packing $inputDir")
        TexturePacker.process(inputDir, outputDir, atlasName)
    }
    println("Packing done")
}
