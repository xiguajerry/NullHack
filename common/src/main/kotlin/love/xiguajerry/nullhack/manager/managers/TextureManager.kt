package love.xiguajerry.nullhack.manager.managers

import love.xiguajerry.nullhack.graphics.texture.Texture
import love.xiguajerry.nullhack.graphics.texture.loader.AsyncTextureLoader
import love.xiguajerry.nullhack.graphics.texture.loader.LazyTextureContainer
import love.xiguajerry.nullhack.graphics.texture.loader.TextureLoader
import org.lwjgl.opengl.GL46

object TextureManager : TextureLoader by AsyncTextureLoader(2) {
    fun lazyTexture(
        path: String,
        format: Int = GL46.GL_RGBA,
        levels: Int = 3,
        useMipmap: Boolean = true,
        qualityLevel: Int = 2
    ): Texture = LazyTextureContainer(path, format, levels, useMipmap, qualityLevel).register()
}