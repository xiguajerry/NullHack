/*
 * Copyright (c) 2023-2024 NullHack 保留所有权利。 All Right Reserved.
 */

package love.xiguajerry.nullhack.utils

import love.xiguajerry.nullhack.NullHackMod
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    @JvmStatic
    fun isEvening(): Boolean {
        when (val today = SimpleDateFormat("HH").format(Date()).toInt()) {
            1, 2, 3, 4, 5, 6, 19, 20, 21, 22, 23, 0 -> {
                NullHackMod.LOGGER.info("It's night, Time is $today")
                return true
            }
            7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 -> {
                NullHackMod.LOGGER.info("It's not night, Time is $today")
                return false
            }
        }
        NullHackMod.LOGGER.info("Unknown time...")
        return false
    }

}