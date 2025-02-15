package love.xiguajerry.nullhack.manager.managers

import com.mojang.blaze3d.systems.RenderSystem
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.RS
import love.xiguajerry.nullhack.event.api.AlwaysListening
import love.xiguajerry.nullhack.manager.AbstractManager
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings
import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.Profiler
import love.xiguajerry.nullhack.graphics.font.ArrayedUnicodeFontRenderer
import love.xiguajerry.nullhack.graphics.font.FontRenderer
import love.xiguajerry.nullhack.graphics.font.ICompatibleFontRenderer
import love.xiguajerry.nullhack.graphics.font.UnicodeFontRenderer
import love.xiguajerry.nullhack.graphics.imgui.ImGuiScreen
import love.xiguajerry.nullhack.utils.ResourceHelper
import love.xiguajerry.nullhack.utils.threads.RenderThreadCoroutine
import net.spartanb312.everett.graphics.SparseTextureArrayFontRenderer
import java.awt.Font
import java.io.ByteArrayInputStream

object UnicodeFontManager : AbstractManager(), AlwaysListening {
    val CURRENT_FONT: FontRenderer get() = ClientSettings.guiFont.font()
    lateinit var MSYAHEI_9: FontRenderer
    lateinit var MSYAHEI_12: FontRenderer
    lateinit var MSYAHEI_15: FontRenderer
    lateinit var MSYAHEI_20: FontRenderer
    lateinit var ICON_FONT: FontRenderer
    lateinit var GENSHIN_9: FontRenderer
    lateinit var GENSHIN_11: FontRenderer
    lateinit var GENSHIN_18: FontRenderer

    override fun load(profilerScope: Profiler.ProfilerScope) {
        RenderSystem.assertOnRenderThread()
        MSYAHEI_9 = create("/assets/nullhack/MicrosoftYahei.ttf", 9f)
        MSYAHEI_12 = create("/assets/nullhack/MicrosoftYahei.ttf", 9f)
        MSYAHEI_15 = create("/assets/nullhack/MicrosoftYahei.ttf", 15f)
        MSYAHEI_20 = create("/assets/nullhack/MicrosoftYahei.ttf", 20f)
        ICON_FONT = create("/assets/nullhack/IconFont.ttf", 10f)
        GENSHIN_9 = create("/assets/nullhack/Genshin.ttf", 9f, superSamplingLevel = 4)
        GENSHIN_11 = create("/assets/nullhack/Genshin.ttf", 11f)
        GENSHIN_18 = create("/assets/nullhack/Genshin.ttf", 18f, superSamplingLevel = 8)

        ImGuiScreen.Companion
        GuiFont.entries.forEach {
            val font = it.font()
            font.initForImGui()
        }
        ImGuiScreen.imGuiImplGl3.createFontsTexture()
    }

    private fun create(
        path: String, size: Float, antiAlias: Boolean = true,
        fractionalMetrics: Boolean = true, superSamplingLevel: Int = 4
    ): FontRenderer {
        val compatibility = RS.compatibility
        if (compatibility.intelGraphics || !compatibility.arbSparseTexture)
            return createCompatible(path, size, antiAlias, fractionalMetrics, superSamplingLevel)
        return createArrayed(path, size, antiAlias, fractionalMetrics, superSamplingLevel)
    }

    private fun createArrayed(
        path: String, size: Float, antiAlias: Boolean = true,
        fractionalMetrics: Boolean = true, superSamplingLevel: Int = 4
    ): ArrayedUnicodeFontRenderer {
        NullHackMod.LOGGER.debug("Creating arrayed font")
        if (RenderSystem.isOnRenderThread()) {
            return ArrayedUnicodeFontRenderer.fromPath(
                path,
                size * superSamplingLevel,
                640 * (superSamplingLevel / 4.0).coerceIn(1.0, Double.MAX_VALUE).toInt(),
                128,
                1f / superSamplingLevel,
                antiAlias,
                fractionalMetrics,
                true
            )
        } else {
            return runBlocking {
                RenderThreadCoroutine.async {
                    ArrayedUnicodeFontRenderer.fromPath(
                        path,
                        size * superSamplingLevel,
                        640 * (superSamplingLevel / 4.0).coerceIn(1.0, Double.MAX_VALUE).toInt(),
                        128,
                        1f / superSamplingLevel,
                        antiAlias,
                        fractionalMetrics,
                        true
                    )
                }.await()
            }
        }
    }

    /** May be called out of render thread after initialization **/
    private fun createCompatible(
        path: String, size: Float, antiAlias: Boolean = true,
        fractionalMetrics: Boolean = true, superSamplingLevel: Int = 4
    ): UnicodeFontRenderer {
        if (RenderSystem.isOnRenderThread()) {
            return UnicodeFontRenderer.fromPath(
                path,
                size * superSamplingLevel,
                1200 * (superSamplingLevel / 4.0).coerceIn(1.0, Double.MAX_VALUE).toInt(),
                128,
                1f / superSamplingLevel,
                antiAlias,
                fractionalMetrics,
                true
            )
        } else {
            return runBlocking {
                RenderThreadCoroutine.async {
                    UnicodeFontRenderer.fromPath(
                        path,
                        size * superSamplingLevel,
                        1200 * (superSamplingLevel / 4.0).coerceIn(1.0, Double.MAX_VALUE).toInt(),
                        128,
                        1f / superSamplingLevel,
                        antiAlias,
                        fractionalMetrics,
                        true
                    )
                }.await()
            }
        }
    }

    enum class GuiFont(val font: () -> FontRenderer) : Displayable {
        PING_FANG({ MSYAHEI_9 }),
        GENSHIN({ GENSHIN_9 })
    }
}