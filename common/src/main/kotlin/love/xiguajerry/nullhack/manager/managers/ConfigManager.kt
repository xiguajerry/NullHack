package love.xiguajerry.nullhack.manager.managers

import love.xiguajerry.nullhack.NullHackMod
import love.xiguajerry.nullhack.manager.AbstractManager
import love.xiguajerry.nullhack.utils.delegates.CachedValueN
import love.xiguajerry.nullhack.utils.extension.listFilesRecursively
import love.xiguajerry.nullhack.config.Categories
import love.xiguajerry.nullhack.config.FileSystemSource
import love.xiguajerry.nullhack.modules.impl.client.Presets
import love.xiguajerry.nullhack.utils.Profiler

object ConfigManager : AbstractManager() {
    var namespace = "default"
    var currentFileSystemSource = FileSystemSource.DefaultDiskSource
    val configRoot get() = currentFileSystemSource.root.resolve(NullHackMod.ID)
    val currentConfigRoot get() = configRoot.resolve("config").resolve(namespace)
    private val lastCleanedFile get() = configRoot.resolve(".lastcleaned")
    private val currentNamespaceFile get() = configRoot.resolve(".current")
    val lastCleanTime by CachedValueN(10000) {
        lastCleanedFile.readText().toLong()
    }

    private val configCategories = Categories()

    fun clean() {
        if (!currentConfigRoot.exists()) save()
        else {
            if (!lastCleanedFile.exists()) lastCleanedFile.createNewFile()
            configCategories.clean()
            lastCleanedFile.writeText(System.currentTimeMillis().toString())
        }
    }

    override fun load(profilerScope: Profiler.ProfilerScope) {
        if (configRoot.exists() && configRoot.isFile) configRoot.delete()
        configRoot.mkdirs()

        if (currentNamespaceFile.exists() && currentNamespaceFile.isFile) {
            namespace = currentNamespaceFile.readText()
            if (!currentConfigRoot.exists()) namespace = "default"
        } else {
            if (currentNamespaceFile.exists()) currentNamespaceFile.deleteRecursively()
            currentNamespaceFile.createNewFile()
            currentNamespaceFile.writeText("default")
        }
        Presets.target = namespace
        NullHackMod.LOGGER.info("Loading from '$namespace'")

        if (!currentConfigRoot.exists()) save()
        if (!lastCleanedFile.exists()) {
            lastCleanedFile.createNewFile()
            inferCleanTime()
        }
        configCategories.read()
        save()
    }

    fun tryLoad(): Boolean {
        if (!currentConfigRoot.exists()) return false
        if (!lastCleanedFile.exists()) {
            lastCleanedFile.createNewFile()
            inferCleanTime()
        }
        NullHackMod.LOGGER.info("Refreshing from '$namespace'")
        configCategories.read()
        currentNamespaceFile.writeText(namespace)
        save()
        return true
    }

    private fun inferCleanTime() {
        lastCleanedFile.writeText(configRoot.listFilesRecursively().maxOf { it.lastModified() }.toString())
    }

    fun save() {
        if (currentNamespaceFile.exists()) {
            if (currentNamespaceFile.isDirectory) {
                currentNamespaceFile.deleteRecursively()
                currentNamespaceFile.createNewFile()
            }
        } else currentNamespaceFile.createNewFile()
        currentNamespaceFile.writeText(namespace)

        NullHackMod.LOGGER.info("Saving to '$namespace'")

        if (!currentConfigRoot.exists()) currentConfigRoot.mkdirs()
        configCategories.save()
        clean()
    }

    fun getOrCreateCategory(category: String) = configCategories.getConfigurationManager(category)
}