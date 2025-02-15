package love.xiguajerry.nullhack.manager

import love.xiguajerry.nullhack.utils.Profiler

// TODO: Complete this
abstract class AbstractManager {
    // Some managers don't need to be initialized explicitly
    open fun load(profilerScope: Profiler.ProfilerScope) {}
}