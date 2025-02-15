/*
 * Copyright (c) 2021-2022, SagiriXiguajerry. All rights reserved.
 * This repository will be transformed to SuperMic_233.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package love.xiguajerry.nullhack.utils.collections

interface MutableIntIterator : MutableIterator<Int> {
    override fun next(): Int {
        return nextInt()
    }

    fun nextInt(): Int
}