@file:Suppress("KotlinConstantConditions")

package love.xiguajerry.nullhack

import kotlinx.coroutines.launch
import love.xiguajerry.nullhack.event.EventClasses
import love.xiguajerry.nullhack.event.EventProcessor
import love.xiguajerry.nullhack.event.api.AlwaysListening
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.render.CoreRender2DEvent
import love.xiguajerry.nullhack.graphics.GLHelper
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects.draw
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.font.TextComponent
import love.xiguajerry.nullhack.graphics.font.UnicodeFontRenderer
import love.xiguajerry.nullhack.graphics.matrix.scope
import love.xiguajerry.nullhack.graphics.matrix.translatef
import love.xiguajerry.nullhack.i18n.LocalizedNameable
import love.xiguajerry.nullhack.interfaces.IAdvancementsScreen
import love.xiguajerry.nullhack.language.Config
import love.xiguajerry.nullhack.libraries.LibraryLoader
import love.xiguajerry.nullhack.manager.ManagerLoader
import love.xiguajerry.nullhack.manager.managers.GuiManager
import love.xiguajerry.nullhack.manager.managers.ModuleManager
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.mixins.accessor.*
import love.xiguajerry.nullhack.modules.impl.client.ClientSettings.uid
import love.xiguajerry.nullhack.utils.Helper
import love.xiguajerry.nullhack.utils.Profiler
import love.xiguajerry.nullhack.utils.SafeLogger
import love.xiguajerry.nullhack.utils.input.KeyBind
import love.xiguajerry.nullhack.utils.threads.Coroutine
import love.xiguajerry.nullhack.utils.threads.pool0
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen
import net.minecraft.client.gui.screens.inventory.BookViewScreen
import net.minecraft.world.entity.Display
import net.minecraft.world.level.block.entity.SignBlockEntity
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.glBlendFuncSeparate
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.File
import java.util.*
import kotlin.concurrent.thread
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

object NullHackMod : LocalizedNameable(
    Metadata.ID,
    I18NManager.i18N
), AlwaysListening, Helper {
    const val NAME = Metadata.NAME
    const val ID = Metadata.ID
    const val VERSION = Metadata.VERSION
    const val TYPE = "Development Edition"
    const val USE_DEFAULT_LANG = true
    const val NO_LANGUAGE = "*"
//    const val CLIENT_TYPE = "(Protected by MingHui Shield)"
    const val CLIENT_TYPE = "(Modded)"
    const val VIDEO_SOUND = false

    val ICON = listOf(
        " ██████╗██╗     ██╗ ██████╗ ███╗   ██╗",
        "██╔════╝██║     ██║██╔═══██╗████╗  ██║",
        "██║     ██║     ██║██║   ██║██╔██╗ ██║",
        "██║     ██║     ██║██║   ██║██║╚██╗██║",
        "╚██████╗███████╗██║╚██████╔╝██║ ╚████║",
        " ╚═════╝╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═══╝"
    )
    var FOLDER = File(Path(".", ID).absolutePathString())

    @JvmField
    val LOGGER = SafeLogger(LoggerFactory.getLogger(NAME))
    var UID = ""
    var AUTH_STATUS = "not init"

    var shouldSetSystemLanguage = false

    var showMessageDialog = false
    lateinit var messageDialog: TextComponent

    val profiler = Profiler()

    init {
        Profiler.BootstrapProfiler("Load Library") {
            var flag = false
            LibraryLoader.load()
            Coroutine.launch {
                EventClasses.classes
                flag = true
            }
            val time = System.currentTimeMillis()
            if (pool0.activeCount == 0) {
                nonblocking = System.currentTimeMillis() - time
                LOGGER.info("Library finished loading within 2s")
            } else {
                thread {
                    while (true) {
                        if (!flag || LibraryLoader.loadFlags.none()) Thread.sleep(1)
                        else {
                            nonblocking = System.currentTimeMillis() - time
                            break
                        }
                    }
                    LOGGER.info(toString())
                }
            }
        }

        nonNullHandler<CoreRender2DEvent>(Int.MIN_VALUE) {
            if (uid && UID.isNotBlank()) {
                GLHelper.blend = true
                GLHelper.depth = false
                val text = "UID: $UID          "
                val font = UnicodeFontManager.GENSHIN_9
                val width = font.getWidth(text)
                val y = RenderSystem.scaledHeightF - font.height
                val x = RenderSystem.scaledWidthF - width
                font.drawStringShadowed0(text, x, y, Color.WHITE, 1f)
            }

            if (!::messageDialog.isInitialized) return@nonNullHandler
            val warningMessage = this@NullHackMod.messageDialog
            if (showMessageDialog && warningMessage.isNotEmpty()) {
                val messageWidth = warningMessage.getWidth()
                val messageHeight = warningMessage.getHeight(1)
                val font = warningMessage.font
                val tabTitle = "$NAME Dialog(Press ENTER to close)"
                val tabHeight = font.height
                RS.matrixLayer.scope {
                    translatef(10f, 10f, 0f)
                    GL_TRIANGLES.draw(PMVBObjects.VertexMode.Universal) {
                        rectSeparate(messageWidth, tabHeight, GuiManager.getColor())
                        rectSeparate(
                            0f, tabHeight, messageWidth,
                            tabHeight + messageHeight,
                            ColorRGBA(GuiManager.background)
                        )
                    }
                    font.drawStringWithShadow(tabTitle, ColorRGBA.WHITE)
                    translatef(0f, tabHeight, 0f)
                    warningMessage.draw()
                }
            }
        }

//        handler<LoopEvent.Start> {
//            mc.window.setTitle("${MinecraftClient.getInstance().windowTitle} (Protected by MingHui Shield)")
//        }
    }

    fun addMessage(text: String, color: ColorRGBA = ColorRGBA(255, 255, 255)) {
        if (!::messageDialog.isInitialized) messageDialog = TextComponent()
        messageDialog.addLine(text, color)
    }

    fun onKeyPressed(keyCode: KeyBind) {
        if (keyCode.keyCode == GLFW.GLFW_KEY_DELETE) {
            showMessageDialog = false
            messageDialog = TextComponent()
        }
        ModuleManager.onKeyPressed(keyCode)
    }

    fun onKeyReleased(keyCode: KeyBind) {
        ModuleManager.onKeyReleased(keyCode)
    }

    fun initializePre() {
        ICON.forEach {
            LOGGER.info(it)
        }

        LOGGER.info("$NAME $VERSION | $TYPE")
        Profiler.BootstrapProfiler("Ensure Folder Existence") {
            if (!FOLDER.exists()) {
                FOLDER.parentFile.mkdirs()
                FOLDER.mkdir()
                LOGGER.debug("Created folder")
            }
        }

        Profiler.BootstrapProfiler("Event Processor") {
            EventProcessor.toString()
        }

        LOGGER.info("Initializing Beacon-System")
        Profiler.BootstrapProfiler("Initialize Beacon-System") {
//            OriginBeacon
        }
    }

    fun initialize() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread()

        Profiler.BootstrapProfiler("Initialize RenderSystem") {
            RenderSystem.init()
        }

        LOGGER.info("Initializing Managers")
        ManagerLoader.load()

        Profiler.BootstrapProfiler("Refresh FontManager") {
            UnicodeFontRenderer.refresh()
        }

        Profiler.BootstrapProfiler("PostInitialize RenderSystem") {
            RenderSystem.framebuffer.resize(mc.window.framebufferWidth, mc.window.framebufferHeight)
        }

        Profiler.BootstrapProfiler("Initialize I18N") {
            I18NManager.read(Metadata.ID)
        }

        LOGGER.info("$NAME finished initialization after ${Profiler.BootstrapProfiler.sections.sumOf { it.blocking }}ms")
        LOGGER.info(Profiler.BootstrapProfiler.sections.joinToString(separator = "\n"))
    }

    fun postInitialize() {
    }

    object ClientLanguageReload {
        fun reloadLanguages() {
            val client = Minecraft.getInstance()

            // Reload language manager
            client.languageManager.onResourceManagerReload(client.resourceManager)

            // Update window title and chat
            client.updateTitle()
            client.gui.chat.rescaleChat()

            // Update book and advancements screens
            if (client.screen is BookViewScreen) {
                (client.screen as IBookScreenAccessor).languagereload_setCachedPageIndex(-1)
            } else if (client.screen is AdvancementsScreen) {
                (client.screen as IAdvancementsScreen).languagereload_recreateWidgets()
            }

            if (client.level != null) {
                // Update signs
                val chunkManager = client.level!!.chunkSource as IClientChunkManagerAccessor
                var chunks = (chunkManager.languagereload_getChunks() as IClientChunkMapAccessor).languagereload_getChunks()
                for (i in 0..<chunks.length()) {
                    var chunk = chunks.get(i)
                    if (chunk == null) continue
                    for (blockEntity in chunk.getBlockEntities().values) {
                        if (blockEntity !is SignBlockEntity) continue
                        (blockEntity.frontText as ISignTextAccessor).languagereload_setOrderedMessages(null)
                        (blockEntity.frontText as ISignTextAccessor).languagereload_setOrderedMessages(null)
                    }
                }

                // Update text displays
                for (entity in client.level!!.entitiesForRendering()) {
                    if (entity is Display.TextDisplay) {
                        (entity as ITextDisplayEntityAccessor).languagereload_setTextLines(null)
                    }
                }
            }
        }

        fun setLanguage(language: String, fallbacks: List<String>) {
            var client = Minecraft.getInstance()
            var languageManager = client.languageManager
            var config = Config.getInstance()

            var languageIsSame = languageManager.selected == language
            var fallbacksAreSame = config.fallbacks == fallbacks
            if (languageIsSame && fallbacksAreSame) return

            config.previousLanguage = languageManager.selected
            config.previousFallbacks = config.fallbacks
            config.language = language
            config.fallbacks = LinkedList(fallbacks)
            Config.save()

            languageManager.selected = language
            client.options.languageCode = language
            client.options.save()

            reloadLanguages()
        }
    }
}