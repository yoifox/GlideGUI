#version 330

in vec4 outPosition;
out vec4 fragOutColor;

void main()
{
    fragOutColor = outPosition;
}
