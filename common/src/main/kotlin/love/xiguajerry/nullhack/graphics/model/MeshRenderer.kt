package love.xiguajerry.nullhack.graphics.model

import love.xiguajerry.nullhack.graphics.matrix.MatrixLayerStack
import love.xiguajerry.nullhack.graphics.shader.Shader

abstract class MeshRenderer {
    abstract val shader: Shader
    abstract fun MatrixLayerStack.MatrixScope.draw(mesh: Mesh)
}