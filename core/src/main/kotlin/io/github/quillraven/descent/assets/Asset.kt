package io.github.quillraven.descent.assets

import com.badlogic.gdx.assets.AssetDescriptor

interface Asset<T> {
    val descriptor: AssetDescriptor<T>
}