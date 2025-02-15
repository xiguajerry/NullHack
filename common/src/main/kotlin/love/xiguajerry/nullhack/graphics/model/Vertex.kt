package love.xiguajerry.nullhack.graphics.model

import dev.luna5ama.kmogus.struct.Struct
import love.xiguajerry.nullhack.utils.math.vectors.Vec2f
import love.xiguajerry.nullhack.utils.math.vectors.Vec3f

/**
 * Size = (3+3+2) * 4 = 32bytes
 */
@Struct
data class Vertex(
    val position: Vec3f,
    val normal: Vec3f,
    val texCoords: Vec2f,
    val tangent:Vec3f,
    val bitangent:Vec3f
)