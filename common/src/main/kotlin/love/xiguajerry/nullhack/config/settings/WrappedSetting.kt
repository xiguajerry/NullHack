package love.xiguajerry.nullhack.config.settings

import kotlin.properties.ReadWriteProperty

interface WrappedSetting<T : Any> : ReadWriteProperty<Any?, T> {
    val delegate: AbstractSetting<String, *>
}