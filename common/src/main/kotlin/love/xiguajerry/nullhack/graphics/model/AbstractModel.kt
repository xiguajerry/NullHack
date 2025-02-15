package love.xiguajerry.nullhack.graphics.model

abstract class AbstractModel {
    protected val textures = mutableListOf<ModelTexture>()
    protected val meshes = mutableListOf<Mesh>()
}