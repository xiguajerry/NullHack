package love.xiguajerry.nullhack.graphics.texture.delegate

import love.xiguajerry.nullhack.graphics.texture.AbstractTexture
import org.lwjgl.opengl.GL11.GL_RGBA
import java.awt.image.BufferedImage

/**
 * @author SpartanB312
 */
class InstantTexture(
    bufferedImage: BufferedImage,
    format: Int = GL_RGBA,
    beforeUpload: AbstractTexture.() -> Unit = {},
    afterUpload: AbstractTexture.() -> Unit = {}
) : DelegateTexture() {
    init {
        uploadImage(bufferedImage, format, beforeUpload, afterUpload)
    }

    override var width = 0
    override var height = 0
}