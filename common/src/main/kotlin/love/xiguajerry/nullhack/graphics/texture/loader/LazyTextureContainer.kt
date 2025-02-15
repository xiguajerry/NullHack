package love.xiguajerry.nullhack.graphics.texture.loader

import love.xiguajerry.nullhack.utils.ResourceHelper
import love.xiguajerry.nullhack.graphics.texture.MipmapTexture
import love.xiguajerry.nullhack.graphics.texture.Texture
import love.xiguajerry.nullhack.graphics.texture.delegate.LateUploadTexture
import org.lwjgl.opengl.GL46
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @author SpartanB312
 */
class LazyTextureContainer(
    private val texture: LateUploadTexture = LateUploadTexture(),
    private val ioJob: () -> BufferedImage
) : Texture by texture {
    constructor(
        path: String,
        format: Int = GL46.GL_RGBA,
        levels: Int = 3,
        useMipmap: Boolean = true,
        qualityLevel: Int = 2
    ) : this(
        MipmapTexture.lateUpload(format, levels, useMipmap, qualityLevel),
        { ImageIO.read(ResourceHelper.getResourceStream(path)) }
    )

    val state get() = texture.state

    // Return a RenderThread Job
    fun asyncLoad(callback: (() -> Unit)? = null): () -> LateUploadTexture {
        val image = ioJob.invoke()
        return {
            texture.apply {
                upload(image).also { callback?.invoke() }
            }
        }
    }

    fun register(loader: TextureLoader): LazyTextureContainer {
        loader.add(this)
        return this
    }
}