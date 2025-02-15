package love.xiguajerry.nullhack.modules.impl.visual

import com.mojang.blaze3d.platform.GlStateManager
import love.xiguajerry.nullhack.RenderSystem
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.render.CoreRender2DEvent
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.graphics.buffer.Render3DUtils
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.manager.managers.EntityManager
import love.xiguajerry.nullhack.utils.math.vectors.VectorUtils.minus
import love.xiguajerry.nullhack.utils.world.explosion.advanced.DamageCalculation
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.boss.enderdragon.EndCrystal
import net.minecraft.world.phys.Vec3

object CrystalDamage : Module("Crystal Damage", description = "不能代表真实伤害", category = Category.VISUAL) {
    init {
        nonNullHandler<CoreRender2DEvent> { event ->
            EntityManager.entity.filterIsInstance<EndCrystal>()
                .forEach {
                    val damage = DamageCalculation(this, player, player.position())
                        .calcDamage(it.x, it.y, it.z, false, BlockPos.MutableBlockPos())
                    RenderSystem.matrixLayer.scope {
                        val textPos = Render3DUtils.worldToScreen(it.position() - Vec3(.0, 0.5, .0))
                        translatef(textPos.x.toFloat(), textPos.y.toFloat(), 0.0f)
                        val font = UnicodeFontManager.CURRENT_FONT
                        val damageText = damage.toString()
                        translatef(-(font.getWidth(damageText) / 2.0f), 0.0f, 0.0f)
                        GlStateManager._disableDepthTest()
                        font.drawStringWithShadow(damageText, 1f, 1f, ColorRGBA.WHITE.awt)
                        GlStateManager._enableDepthTest()
                    }
                }
        }
    }
}