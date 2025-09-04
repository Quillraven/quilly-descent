package io.github.quillraven.descent.component

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

data class Graphic(
    val region: TextureRegion,
    val color: Color = Color.WHITE.cpy(),
) : Component<Graphic> {
    override fun type() = Graphic

    companion object : ComponentType<Graphic>()
}