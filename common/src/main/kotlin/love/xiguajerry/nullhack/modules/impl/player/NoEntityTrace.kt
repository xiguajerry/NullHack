package love.xiguajerry.nullhack.modules.impl.player

import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module

object NoEntityTrace : Module(
    "No Entity Trace", "NoEntityTrace", Category.PLAYER
){
     val ponly by setting("Pickaxe Only", true)
     val noSword by setting("No Sword", true)
}

