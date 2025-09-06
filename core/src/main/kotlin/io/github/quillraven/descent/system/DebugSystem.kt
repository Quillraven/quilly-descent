package io.github.quillraven.descent.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.github.quillraven.fleks.IntervalSystem

class DebugSystem() : IntervalSystem() {
    private val profiler = GLProfiler(Gdx.graphics).apply { enable() }
    private var accumulator = 0f

    override fun onTick() {
        accumulator += deltaTime
        if (accumulator >= 1f) {
            accumulator = 0f
            Gdx.graphics.setTitle("#entities: ${world.numEntities}, #draw: ${profiler.drawCalls}")
        }
        profiler.reset()
    }
}