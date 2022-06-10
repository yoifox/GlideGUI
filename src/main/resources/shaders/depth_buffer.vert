#version 330

in vec3 position;
uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
out vec4 outPosition;

void main()
{
    gl_Position = gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1);
    outPosition = gl_Position;
}
