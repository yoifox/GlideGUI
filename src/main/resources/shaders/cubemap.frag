#version 330

in vec3 tex;
out vec4 fragOutColor;
uniform samplerCube sampler;

void main()
{
    fragOutColor = texture(sampler, tex);
}