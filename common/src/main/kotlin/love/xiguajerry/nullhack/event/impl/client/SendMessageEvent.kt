/*
 * Copyright (c) 2023 NullHack 保留所有权利。 All Right Reserved.
 */

package love.xiguajerry.nullhack.event.impl.client

import love.xiguajerry.nullhack.event.api.*

class SendMessageEvent(var string: String) : IEvent, ICancellable by Cancellable(), IPosting by Companion {
    companion object : EventBus()
}