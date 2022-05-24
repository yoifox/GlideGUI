#version 330

in vec3 position;
in vec2 uv;
in vec3 normal;
in vec3 tangent;

out vec4 fragPosition;
out vec2 fragUv;
out vec3 fragNormal;
out vec3 fragTangent;
out vec4 fragWorldPosition;
out vec3 fragViewPosition;
out mat4 fragViewMatrix;
out mat4 fragTransformationMatrix;
//out mat3 fragTangentMatrix;

uniform mat4 projectionMatrix;
uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform vec2 uvScale;

void main()
{
    fragViewMatrix = viewMatrix;
    fragTransformationMatrix = transformationMatrix;
    fragViewPosition = position;
    fragWorldPosition = transformationMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * fragWorldPosition;
    fragUv = uv;
    fragPosition = gl_Position;
    fragNormal = normalize((transformationMatrix * vec4(normal, 0)).xyz);

    fragTangent = tangent;

    vec3 normal_ = normalize(fragNormal);
    vec3 tangent_ = normalize((viewMatrix*vec4(tangent, 0.0)).xyz);
    vec3 biTangent_ = normalize(cross(normal_, tangent_));

    //fragTangentMatrix = mat3(
    //    tangent_.x, biTangent_.x, normal_.x,
    //    tangent_.y, biTangent_.y, normal_.y,
    //    tangent_.z, biTangent_.z, normal_.z
    //);
}
