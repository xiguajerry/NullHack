package love.xiguajerry.nullhack.graphics.mask

import net.minecraft.core.Direction

@JvmInline
value class SideMask private constructor(val mask: Int) {
    val isEmpty get() = mask == 0

    operator fun plus(other: SideMask): SideMask {
        return SideMask(this.mask or other.mask)
    }

    operator fun minus(other: SideMask): SideMask {
        return SideMask(this.mask and other.mask.inv())
    }

    operator fun contains(other: SideMask): Boolean {
        return this.mask.inv() and other.mask == 0
    }

    fun toVertexMask(): BoxVertexMask {
        var mask = BoxVertexMask.Companion.EMPTY

        if (contains(DOWN)) {
            mask += BoxVertexMask.Companion.DOWN
        }

        if (contains(UP)) {
            mask += BoxVertexMask.Companion.UP
        }

        if (contains(NORTH)) {
            mask += BoxVertexMask.Companion.NORTH
        }

        if (contains(SOUTH)) {
            mask += BoxVertexMask.Companion.SOUTH
        }

        if (contains(WEST)) {
            mask += BoxVertexMask.Companion.WEST
        }

        if (contains(EAST)) {
            mask += BoxVertexMask.Companion.EAST
        }

        return mask
    }

    fun toOutlineMask(): BoxOutlineMask {
        var mask = BoxOutlineMask.Companion.EMPTY

        if (contains(DOWN)) {
            mask += BoxOutlineMask.Companion.DOWN
        }

        if (contains(UP)) {
            mask += BoxOutlineMask.Companion.UP
        }

        if (contains(NORTH)) {
            mask += BoxOutlineMask.Companion.NORTH
        }

        if (contains(SOUTH)) {
            mask += BoxOutlineMask.Companion.SOUTH
        }

        if (contains(WEST)) {
            mask += BoxOutlineMask.Companion.WEST
        }

        if (contains(EAST)) {
            mask += BoxOutlineMask.Companion.EAST
        }

        return mask
    }

    fun toOutlineMaskInv(): BoxOutlineMask {
        var mask = BoxOutlineMask.Companion.ALL

        if (!contains(DOWN)) {
            mask -= BoxOutlineMask.Companion.DOWN
        }

        if (!contains(UP)) {
            mask -= BoxOutlineMask.Companion.UP
        }

        if (!contains(NORTH)) {
            mask -= BoxOutlineMask.Companion.NORTH
        }

        if (!contains(SOUTH)) {
            mask -= BoxOutlineMask.Companion.SOUTH
        }

        if (!contains(WEST)) {
            mask -= BoxOutlineMask.Companion.WEST
        }

        if (!contains(EAST)) {
            mask -= BoxOutlineMask.Companion.EAST
        }

        return mask
    }

    companion object {
        val EMPTY = SideMask(0)

        val DOWN = SideMask(1 shl 0)
        val UP = SideMask(1 shl 1)
        val NORTH = SideMask(1 shl 2)
        val SOUTH = SideMask(1 shl 3)
        val WEST = SideMask(1 shl 4)
        val EAST = SideMask(1 shl 5)
        val ALL = SideMask(0x3F)

        fun Direction.toMask(): SideMask {
            return when (this) {
                Direction.DOWN -> DOWN
                Direction.UP -> UP
                Direction.NORTH -> NORTH
                Direction.SOUTH -> SOUTH
                Direction.WEST -> WEST
                Direction.EAST -> EAST
            }
        }
    }
}