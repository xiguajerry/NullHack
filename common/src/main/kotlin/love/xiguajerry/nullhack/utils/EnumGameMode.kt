package love.xiguajerry.nullhack.utils

import net.minecraft.world.level.GameType

enum class EnumGameMode(type: GameType) : Displayable {
    CREATIVE(GameType.CREATIVE),
    SURVIVAL(GameType.SURVIVAL),
    SPECTATOR(GameType.SPECTATOR),
    ADVENTURE(GameType.ADVENTURE)
}