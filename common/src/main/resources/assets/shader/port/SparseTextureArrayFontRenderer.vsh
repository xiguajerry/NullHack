#version 450 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec4 vertColor;
layout (location = 2) in vec2 texCoords;
layout (location = 3) in int depth;

uniform mat4 matrix;

out vec4 color;
out vec2 uv;
out int z;

void main() {
    gl_Position = matrix * vec4(position,0.0 , 1.0);
    color = vertColor.abgr;
    uv = texCoords;
    z = depth;
}