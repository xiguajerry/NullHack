package love.xiguajerry.nullhack.modules.impl.visual.valkyrie.components

import love.xiguajerry.nullhack.modules.impl.visual.valkyrie.Dimensions
import love.xiguajerry.nullhack.modules.impl.visual.valkyrie.HudComponent
import love.xiguajerry.nullhack.utils.NonNullContext

class LocationIndicator(private val dim: Dimensions) : HudComponent() {
    context(NonNullContext)
    override fun render(partial: Float) {
        val x = dim.wScreen * 0.2f
        val y = dim.hScreen * 0.8f

        val xLoc = player.blockPosition().x
        val zLoc = player.blockPosition().z

        drawFont("$xLoc / $zLoc".format(xLoc, zLoc), x, y)
    }
}