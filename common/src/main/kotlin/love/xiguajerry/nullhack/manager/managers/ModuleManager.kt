package love.xiguajerry.nullhack.manager.managers

import kotlinx.coroutines.*
import love.xiguajerry.nullhack.I18NManager
import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.NullHackMod.resolve
import love.xiguajerry.nullhack.config.settings.BindSetting
import love.xiguajerry.nullhack.gui.hud.impl.ActiveModules
import love.xiguajerry.nullhack.gui.hud.impl.HudArrayList
import love.xiguajerry.nullhack.gui.hud.impl.HudEventPosts
import love.xiguajerry.nullhack.gui.hud.impl.HudFPS
import love.xiguajerry.nullhack.gui.hud.impl.Notification
import love.xiguajerry.nullhack.i18n.ILocalizedNameable
import love.xiguajerry.nullhack.i18n.LocalizedNameable
import love.xiguajerry.nullhack.manager.AbstractManager
import love.xiguajerry.nullhack.modules.*
import love.xiguajerry.nullhack.modules.impl.client.*
import love.xiguajerry.nullhack.modules.impl.combat.AutoTotem
import love.xiguajerry.nullhack.modules.impl.combat.Criticals
import love.xiguajerry.nullhack.modules.impl.combat.KillAura
import love.xiguajerry.nullhack.modules.impl.combat.MaceSpoof
import love.xiguajerry.nullhack.modules.impl.combat.zc.ZealotCrystal
import love.xiguajerry.nullhack.modules.impl.misc.AntiItemCrash
import love.xiguajerry.nullhack.modules.impl.misc.FakePlayer
import love.xiguajerry.nullhack.modules.impl.misc.FastPlace
import love.xiguajerry.nullhack.modules.impl.misc.HitSound
import love.xiguajerry.nullhack.modules.impl.misc.HitboxDesync
import love.xiguajerry.nullhack.modules.impl.movement.ElytraFlight
import love.xiguajerry.nullhack.modules.impl.movement.ElytraFlightNew
import love.xiguajerry.nullhack.modules.impl.movement.Flight
import love.xiguajerry.nullhack.modules.impl.movement.GuiMove
import love.xiguajerry.nullhack.modules.impl.movement.NoFall
import love.xiguajerry.nullhack.modules.impl.movement.NoSlowDown
import love.xiguajerry.nullhack.modules.impl.movement.SafeWalk
import love.xiguajerry.nullhack.modules.impl.movement.Sprint
import love.xiguajerry.nullhack.modules.impl.movement.Velocity
import love.xiguajerry.nullhack.modules.impl.player.AntiHunger
import love.xiguajerry.nullhack.modules.impl.player.AutoRespawn
import love.xiguajerry.nullhack.modules.impl.player.MultiTask
import love.xiguajerry.nullhack.modules.impl.player.NoEntityTrace
import love.xiguajerry.nullhack.modules.impl.player.NoRotate
import love.xiguajerry.nullhack.modules.impl.player.PacketMine
import love.xiguajerry.nullhack.modules.impl.player.Scaffold
import love.xiguajerry.nullhack.modules.impl.player.Search
import love.xiguajerry.nullhack.modules.impl.visual.AspectRatio
import love.xiguajerry.nullhack.modules.impl.visual.BlockHighlight
import love.xiguajerry.nullhack.modules.impl.visual.BlueArchiveHalo
import love.xiguajerry.nullhack.modules.impl.visual.CrystalDamage
import love.xiguajerry.nullhack.modules.impl.visual.FullBright
import love.xiguajerry.nullhack.modules.impl.visual.MotionBlur
import love.xiguajerry.nullhack.modules.impl.visual.NameTags
import love.xiguajerry.nullhack.modules.impl.visual.NoCameraClip
import love.xiguajerry.nullhack.modules.impl.visual.NoRender
import love.xiguajerry.nullhack.modules.impl.visual.PlaceRender
import love.xiguajerry.nullhack.modules.impl.visual.RenderTest
import love.xiguajerry.nullhack.modules.impl.visual.Shaders
import love.xiguajerry.nullhack.modules.impl.visual.Tracers
import love.xiguajerry.nullhack.modules.impl.visual.valkyrie.Valkyrie
import love.xiguajerry.nullhack.script.FabricPlatform
import love.xiguajerry.nullhack.utils.Profiler
import love.xiguajerry.nullhack.utils.input.KeyBind
import love.xiguajerry.nullhack.utils.runSafe
import love.xiguajerry.nullhack.utils.threads.Coroutine
import org.luaj.vm2.compiler.LuaC

object ModuleManager : AbstractManager(), ILocalizedNameable by LocalizedNameable(resolve("modules"), I18NManager.i18N) {
    val modules by lazy { mutableListOf(
        ClickGui,
        ClientSettings,
        Colors,
        GraphicsInfo,
        HudEditor,
        HurtTimeDebug,
        PacketDebug,
        Presets,
        RefreshFontCache,
        ReloadScript,
        Watermark,

        ZealotCrystal,
        AutoTotem,
        Criticals,
        KillAura,
        MaceSpoof,

        AntiItemCrash,
        FakePlayer,
        FastPlace,
        HitboxDesync,
        HitSound,

        ElytraFlight,
        ElytraFlightNew,
        Flight,
        GuiMove,
        NoFall,
        NoSlowDown,
        SafeWalk,
        Sprint,
        Velocity,

        AntiHunger,
        AutoRespawn,
        MultiTask,
        NoEntityTrace,
        NoRotate,
        PacketMine,
        Scaffold,
        Search,

        Valkyrie,
        AspectRatio,
        BlockHighlight,
        BlueArchiveHalo,
        CrystalDamage,
        FullBright,
        MotionBlur,
        NameTags,
        NoCameraClip,
        NoRender,
        Notification,
        PlaceRender,
        RenderTest,
        Shaders,
        Tracers,

        ActiveModules,
        HudArrayList,
        HudEventPosts,
        HudFPS,
        Notification
    ) }

    fun getModuleByName(name: CharSequence) = getFilteredModule().find { it.nameAsString.equals(name.toString(), true) }

    fun getModulesByCategory(category: Category) = getFilteredModule().filter { it.category == category }

    fun getEnabledModules() = modules.filter { it.isEnabled }

    private fun getFilteredModule() = modules.filter { !it.internal }

    fun onKeyPressed(keyCode: KeyBind) =
        modules.associateWith { require(it.settings.isNotEmpty())
            it.settings.filterIsInstance<BindSetting>() }
            .forEach { (module, bList) ->
                bList.filter {
                    it.value.keyCode == keyCode.keyCode
                }.forEach { b ->
                    b.isPressed = true
                    if (module.isEnabled || b.alwaysActive) b.onPressConsumers.forEach { func ->
                        runSafe(func)
                    }
                }
            }

    fun onKeyReleased(keyCode: KeyBind) =
        modules.associateWith { it.settings.filterIsInstance<BindSetting>() }
            .forEach { (_, bList) ->
                bList.filter {
                    it.value.keyCode == keyCode.keyCode
                }.forEach { b ->
                    b.isPressed = false
                }
            }

    fun loadScript() {
        val scriptsFolder = NullHackMod.FOLDER.resolve("scripts")
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdirs()
            return
        }
        runBlocking {
            scriptsFolder.listFiles()?.filter { it.extension.lowercase() == "lua" }?.map { f ->
                Coroutine.async {
                    try {
                        f.inputStream().use { source ->
                            val code = f.readText()
                            if (code.startsWith("-- nullhack-scripts")) {
                                val prototype = LuaC.instance.compile(source, f.name)
                                val initialEnv = FabricPlatform.emptyGlobals()
                                initialEnv.loader.load(prototype, f.name, initialEnv).call()
                                val module = LuaModule(code, prototype, initialEnv)
                                NullHackMod.LOGGER.info("Loaded script ${module.name}")
                                modules.add(module)
                            }
                        }
                    } catch (e: Exception) {
                        NullHackMod.LOGGER.warn("Failed to load script ${f.name}, skipping", e)
                    }
                }
            }?.awaitAll() ?: NullHackMod.LOGGER.warn("Cannot read script folder")
        }
    }

    override fun load(profilerScope: Profiler.ProfilerScope) {
        modules.forEach {
            it.apply {
                if (it::class.java.isAnnotationPresent(ExperimentalModule::class.java)) onEnabled {
                    NotificationManager.push(
                        nameAsString,
                        "You are using an experimental module!",
                        1000,
                        Notification.NotificationType.WARN
                    )
                }
                if (alwaysListening) subscribe()
                if (enableByDefault) enable = true
            }
        }
        loadScript()
        val m = modules.distinctBy { it.moduleId }
        modules.clear()
        modules.addAll(m)
        NullHackMod.LOGGER.info("${modules.size} module(s) were loaded")
    }
}