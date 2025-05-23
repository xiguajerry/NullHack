package love.xiguajerry.nullhack.utils.math.vectors

import love.xiguajerry.nullhack.utils.extension.pitch
import love.xiguajerry.nullhack.utils.extension.yaw
import love.xiguajerry.nullhack.utils.math.toRadian
import net.minecraft.world.entity.Entity
import kotlin.math.hypot
import kotlin.math.pow

class Vec2f(val x: Float, val y: Float) {

    /**
     * Create a Vec2f from this entity's rotations
     */
    constructor(entity: Entity) : this(entity.yaw, entity.pitch)

    constructor(x: Double, y: Double) : this(x.toFloat(), y.toFloat())
    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())

    constructor(vec2d: Vec2d) : this(vec2d.x.toFloat(), vec2d.y.toFloat())

    constructor() : this(0f, 0f)

    fun toRadians(): Vec2f {
        return Vec2f(x.toRadian(), y.toRadian())
    }

    fun length() = hypot(x, y)

    fun lengthSquared() = (x.pow(2) + y.pow(2))

    operator fun div(vec2f: Vec2f) = div(vec2f.x, vec2f.y)

    operator fun div(divider: Float) = div(divider, divider)

    fun div(x: Float, y: Float) = Vec2f(this.x / x, this.y / y)

    operator fun times(vec2f: Vec2f) = times(vec2f.x, vec2f.y)

    operator fun times(multiplier: Float) = times(multiplier, multiplier)

    fun times(x: Float, y: Float) = Vec2f(this.x * x, this.y * y)

    operator fun minus(vec2f: Vec2f) = minus(vec2f.x, vec2f.y)

    operator fun minus(value: Float) = minus(value, value)

    fun minus(x: Float, y: Float) = plus(-x, -y)

    operator fun plus(vec2f: Vec2f) = plus(vec2f.x, vec2f.y)

    operator fun plus(value: Float) = plus(value, value)

    fun plus(x: Float, y: Float) = Vec2f(this.x + x, this.y + y)

    fun toVec2d() = Vec2d(x.toDouble(), y.toDouble())

    operator fun component1() = x

    operator fun component2() = y

    override fun toString(): String {
        return "Vec2f(x=$x, y=$y)"
    }

    companion object {
        val ZERO = Vec2f(0f, 0f)

        @JvmStatic
        fun getX(bits: Long): Float {
            return Float.fromBits((bits shr 32).toInt())
        }

        @JvmStatic
        fun getY(bits: Long): Float {
            return Float.fromBits((bits and 0xFFFFFFFF).toInt())
        }
    }
}