/*
 * Copyright (c) 2023-2024 NullHack 保留所有权利。 All Right Reserved.
 */

package love.xiguajerry.nullhack.modules.impl.visual

import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module


object AspectRatio : Module("Aspect Ratio", "AspectRatio", Category.VISUAL) {
    @JvmStatic
    val ratio by setting("Ratio", 1.78f, 0.1f..8f)
}