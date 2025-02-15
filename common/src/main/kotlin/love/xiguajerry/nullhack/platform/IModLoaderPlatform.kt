package love.xiguajerry.nullhack.platform

interface IModLoaderPlatform {
    fun getModVersion(mod: String): String?
}