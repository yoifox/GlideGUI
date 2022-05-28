#version 330

in vec2 position;
in vec2 uv;

out vec2 fragUv;
out vec2 fragImgUv;

uniform mat4 transformationMatrix;
uniform vec2 uvsPos;
uniform vec2 uvsMul;

void main()
{
    gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);
    fragUv = uv;
    fragImgUv = uvsMul * uv + uvsPos;
}
