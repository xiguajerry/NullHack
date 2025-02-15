#version 450
// 4 pixels per local_group
layout(local_size_x = 2, local_size_y = 2, local_size_z = 1) in;

layout(rgba8, binding = 0) uniform image2D input_g_buffer0;
layout(rgba8, binding = 1) uniform image2D input_g_buffer1;
layout(rgba8, binding = 2) uniform image2D output_g_buffer;

void main(void) {
    ivec2 pos = ivec2(gl_GlobalInvocationID.xy);
    vec4 color1 = imageLoad(input_g_buffer0, pos);
    vec4 color2 = imageLoad(input_g_buffer1, pos);
    vec4 color3 = color1 * 0.5 + color2 * 0.5;
    barrier();
    imageStore(output_g_buffer, pos, color3);
}