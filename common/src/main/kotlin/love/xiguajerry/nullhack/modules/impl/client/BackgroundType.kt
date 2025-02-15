package love.xiguajerry.nullhack.modules.impl.client

import love.xiguajerry.nullhack.RenderSystem
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.graphics.shader.bg.IgniteParticles

enum class BackgroundType(override val displayName: CharSequence) : Displayable {
    NONE("None") {
        override fun draw(alpha: Float) {}
    },
    PARTICLES("Particles") {
        override fun draw(alpha: Float) {
            RenderSystem.particleSystem.render(alpha)
        }
    },
    IGNITE("Ignite") {
        override fun draw(alpha: Float) {
            IgniteParticles.draw(alpha)
        }
    };

    abstract fun draw(alpha: Float)
}