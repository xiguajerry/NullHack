package love.xiguajerry.nullhack.utils

@Suppress("UNCHECKED_CAST")
inline val <T> Class<out T>.companion: T? get() = try {
    getDeclaredField("Companion")[null] as? T
} catch (e: Exception) {
    null
}

@Suppress("UNCHECKED_CAST")
inline val <T> Class<out T>.instance: T? get() {
    if (this.packageName.startsWith("love.xiguajerry.nullhack.modules")) {
//        val simplified = this.packageName.substringAfter("love.xiguajerry.nullhack.modules.impl.")
//        if (simplified.startsWith("client") || simplified.startsWith("visual")
//            || simplified.startsWith("player") || simplified.startsWith("misc"))
            return this.instanceJava
    } else return this.instanceJava
    return null
}

@Suppress("UNCHECKED_CAST")
inline val <T> Class<out T>.instanceJava: T?
    get() = this.declaredFields.firstOrNull {
        if (it.name == "INSTANCE") try {
            it.isAccessible = true
            it[null]
//            NullHackMod.LOGGER.debug("Class ${this.name} has a valid instance, attempt to reflect...")
            true
        } catch (e: Exception) {
//            NullHackMod.LOGGER.debug("Class ${this.name} has no valid instance.")
            false
        } else false
    }?.run {
        isAccessible = true
        this[null] as T
    }

inline val Class<*>.shortName: String
    get() = typeName.substring(typeName.lastIndexOf(".") + 1)