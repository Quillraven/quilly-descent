package io.github.quillraven.descent.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.g2d.TextureAtlas

enum class AtlasAsset : Asset<TextureAtlas> {
    GAME_OBJECTS;

    override val descriptor = AssetDescriptor("graphics/${name.lowercase()}.atlas", TextureAtlas::class.java)
}