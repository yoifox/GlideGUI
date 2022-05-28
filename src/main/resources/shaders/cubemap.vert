#version 330

in vec3 position;
out vec3 tex;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform int size;

void main()
{
    vec3 pos = vec3(position.x, position.y - 1, position.z); //the default cube's origin is at bottom.
    gl_Position = projectionMatrix * viewMatrix * vec4(pos * size, 1);
    tex = pos * size;
}