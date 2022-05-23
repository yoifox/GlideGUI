#version 330

struct Material
{
    vec4 colorValue;
    vec4 metallicValue;
    vec4 specularValue;
    int hasColor;
    int hasMetallic;
    int hasNormal;
    int hasSpecular;
    vec2 uvScale;
    int hasTransparency;
    float transparency;
};

struct DirectionalLight
{
    vec4 color;
    float intensity;
    vec3 rotation;
};

struct DistanceFog
{
    float density;
    vec4 color;
};

struct PointLight
{
    vec4 color;
    float radius;
    float intensity;
    vec3 position;
    int isLast;
};

in vec4 fragPosition;
in vec2 fragUv;
in vec2 fragTerrainUv;
in vec3 fragNormal;
in vec3 fragTangent;
in vec4 fragWorldPosition;
in vec3 fragViewPosition;
in mat4 fragViewMatrix;
in mat4 fragTransformationMatrix;

uniform Material material;
uniform sampler2D height;
uniform sampler2D matColor;
uniform sampler2D matColorGrass;
uniform sampler2D matMetallic;
uniform sampler2D matSpecular;
uniform sampler2D matTransparency;
uniform sampler2D matNormal;
uniform DirectionalLight directionalLight;
uniform PointLight pointLights[32];
uniform vec4 worldColor;
uniform DistanceFog distanceFog;

out vec4 fragColor;

vec4 color();
vec4 calcPointLight(PointLight pointLight, vec4 diffuseColor, vec4 metallicColor, vec4 specularColor);
vec4 calcDirectionalLight(DirectionalLight directionalLight);

vec2 uv;
vec3 normal;

void main()
{
    uv = fragUv * material.uvScale;
    if(material.hasNormal == 1)
        normal = fragNormal + texture(matNormal, uv).xyz;
    else
        normal = fragNormal;

    vec4 color = color();
    if(material.hasTransparency == 1)
        color.w = texture(matTransparency, uv).r;
    else
        color.w = material.transparency;
    fragColor = color;
}

vec4 color()
{
    vec4 color, metallic, specular;

    if(material.hasColor == 1)
    {
        color = vec4(mix(texture(matColor, uv).rgb, texture(matColorGrass, uv).rgb, texture(height, fragTerrainUv).r).rgb, 1);
        //color = texture(matColor, fragTerrainUv) + texture(matColorGrass, fragTerrainUv) * (texture(height, fragTerrainUv).r / 4);
    }
    else
        color = material.colorValue;

    if(material.hasMetallic == 1)
        metallic = texture(matMetallic, uv);
    else
        metallic = material.metallicValue;

    if(material.hasSpecular == 1)
        specular = texture(matSpecular, uv);
    else
        specular = material.specularValue;

    vec4 result = vec4(0);

    for(int i = 0; i < pointLights.length(); i++)
    {
        result += calcPointLight(pointLights[i], color, metallic, specular);
        if(pointLights[i].isLast == 1)
            break;
    }

    result += calcDirectionalLight(directionalLight);

    vec4 lightColor = (distanceFog.density * distanceFog.color * length(fragViewMatrix * fragWorldPosition)) + (result * color);
    return color * worldColor * (1 + metallic) + specular + lightColor;
}

vec4 calcDirectionalLight(DirectionalLight directionalLight)
{
    vec3 lightDir = normalize(directionalLight.rotation - fragWorldPosition.xyz);
    float specularFac = max(dot(normal, lightDir), 0.0);
    vec4 diffuseFac = specularFac * directionalLight.color * directionalLight.intensity;

    return diffuseFac;
}

vec4 calcPointLight(PointLight pointLight, vec4 diffuseColor, vec4 metallicColor, vec4 specularColor)
{
    vec3 lightDir = normalize(pointLight.position - fragWorldPosition.xyz);
    float specularFac = max(dot(normal, lightDir), 0.0);
    vec4 diffuseFac = specularFac * pointLight.color * pointLight.intensity;

    return diffuseFac * (pointLight.radius / length(pointLight.position - fragWorldPosition.xyz));
}
